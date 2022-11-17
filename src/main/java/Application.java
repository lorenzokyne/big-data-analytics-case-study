import static java.util.stream.Collectors.groupingBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.jkarma.mining.joiners.TidSet;
import org.jkarma.mining.providers.TidSetProvider;
import org.jkarma.mining.structures.MiningStrategy;
import org.jkarma.mining.structures.Strategies;
import org.jkarma.mining.windows.WindowingStrategy;
import org.jkarma.mining.windows.Windows;
import org.jkarma.pbcd.descriptors.Descriptors;
import org.jkarma.pbcd.detectors.Detectors;
import org.jkarma.pbcd.detectors.PBCD;
import org.jkarma.pbcd.events.ChangeDescriptionCompletedEvent;
import org.jkarma.pbcd.events.ChangeDescriptionStartedEvent;
import org.jkarma.pbcd.events.ChangeDetectedEvent;
import org.jkarma.pbcd.events.ChangeNotDetectedEvent;
import org.jkarma.pbcd.events.PBCDEventListener;
import org.jkarma.pbcd.events.PatternUpdateCompletedEvent;
import org.jkarma.pbcd.events.PatternUpdateStartedEvent;
import org.jkarma.pbcd.patterns.Patterns;
import org.jkarma.pbcd.similarities.UnweightedJaccard;

import lombok.var;
import lombok.extern.slf4j.Slf4j;
import models.ClimateData;
import models.Relevation;

@Slf4j
public class Application {
    public static void main(String[] args) throws IOException {

        String filePath = "./csv/";
        String servicePath = "local";
        if (args.length > 0) {
            filePath = args[0];
            if(args.length > 1){
                servicePath = args[1];
            }
        }
        SparkSession spark = SparkSession
                .builder()
                .master(servicePath)
                .appName("BDA - Case Study - Capocchiano Narracci: Climate Change Detection")
                .getOrCreate();
        Dataset<Row> df = null;
        for (Path path : Files.list(Paths.get(filePath)).collect(Collectors.toList())) {
            var temp = spark.read().format("csv")
                    .option("header", "true")
                    .option("delimiter", ",")
                    .option("inferSchema", "true").load(path.toString());
            if (df == null) {
                df = temp;
            } else {
                df = df.union(temp);
            }
        }

        assert df != null;
        var result = df.groupBy("DATE").df().sort("DATE");
        result.show();
        result = result.filter((result.col("AWND").isNull().and(result.col("TOBS").isNotNull())));
        Encoder<ClimateData> personEncoder = Encoders.bean(ClimateData.class);
        Dataset<ClimateData> climateDataset = result.as(personEncoder);

//        File csvFile = new File(filePath);
//        CsvMapper csvMapper = new CsvMapper();
//
//        CsvSchema csvSchema = csvMapper.typedSchemaFor(ClimateData.class).withHeader().withColumnSeparator(',');
//
//        MappingIterator<ClimateData> complexUsersIter = csvMapper.readerWithTypedSchemaFor(ClimateData.class).with(csvSchema).readValues(csvFile);

        List<ClimateData> complexUsers = climateDataset.collectAsList();

        int blockSize = 25;
        float minFreq = 0.25f;
        float minChange = 0.3f;
        Stream<Relevation> dataset = generateRelevationDataset(complexUsers);

        PBCD<Relevation, ClimateData, TidSet, Boolean> detector = getPBCD(minFreq, minChange, blockSize);

        detector.registerListener(new PBCDEventListener<ClimateData, TidSet>() {

            @Override
            public void patternUpdateCompleted(PatternUpdateCompletedEvent<ClimateData, TidSet> arg0) {
                //do nothing
                System.out.println("pattern updated " + arg0);
            }

            @Override
            public void patternUpdateStarted(PatternUpdateStartedEvent<ClimateData, TidSet> arg0) {
                //do nothing
                System.out.println("started");
            }

            @Override
            public void changeDetected(ChangeDetectedEvent<ClimateData, TidSet> event) {
                System.out.println("change detected: " + event.getAmount());
                System.out.println("\tdescribed by:");
                event.getDescription().forEach(p -> {
                    double freqReference = p.getFirstEval().getRelativeFrequency() * 100;
                    double freqTarget = p.getSecondEval().getRelativeFrequency() * 100;

                    String message;
                    if (freqTarget > freqReference) {
                        message = "increased frequency from ";
                    } else {
                        message = "decreased frequency from ";
                    }
                    message += Double.toString(freqReference) + "% to " + Double.toString(freqTarget) + "%";
                    System.out.println("\t\t" + p.getItemSet() + " " + message);
                });
            }

            @Override
            public void changeNotDetected(ChangeNotDetectedEvent<ClimateData, TidSet> arg0) {
                System.out.println("change not detected: " + arg0.getAmount());
            }

            @Override
            public void changeDescriptionCompleted(ChangeDescriptionCompletedEvent<ClimateData, TidSet> arg0) {
                //do nothing
                System.out.println("Descriptor changed");
            }

            @Override
            public void changeDescriptionStarted(ChangeDescriptionStartedEvent<ClimateData, TidSet> arg0) {
                //do nothing
            }
        });
        try {

            dataset.forEach((e) -> {
                detector.accept(e);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static Stream<Relevation> generateRelevationDataset(List<ClimateData> complexUsers) {
        Map<String, List<ClimateData>> map = complexUsers.stream().collect(groupingBy(ClimateData::getDate));
        List<Relevation> result = new LinkedList<>();
        map.forEach((key, value) -> {
            value.sort(Comparator.comparing(ClimateData::getDate));
            result.add(new Relevation(value));
        });

        return result.stream();
    }

    public static PBCD<Relevation, ClimateData, TidSet, Boolean> getPBCD(float minFreq, float minChange,
                                                                         int blockSize) {
        //we prepare the time window model and the data accessor
        WindowingStrategy<TidSet> model = Windows.blockwiseSliding();
        TidSetProvider<ClimateData> accessor = new TidSetProvider<>(model);

        //we instantiate the pattern language delegate
        MixedClimateJoiner joiner = new MixedClimateJoiner();
        //we instantiate the mining strategy
        MiningStrategy<ClimateData, TidSet> strategy = Strategies.upon(joiner).eclat(minFreq).dfs(accessor);

        //we assemble the PBCD
        return Detectors.upon(strategy).unweighted((p, t) -> Patterns.isFrequent(p, minFreq, t), new UnweightedJaccard()).describe(Descriptors.partialEps(minFreq, 1.00)).build(minChange, blockSize);
    }

}