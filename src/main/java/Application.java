import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.jetbrains.annotations.Nullable;
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
    private static boolean debugPattern = false;

    public static void main(String[] args) throws IOException {

        String filePath = "./csv/";
        String servicePath = "local";
        if (args.length > 0) {
            filePath = args[0];
            if (args.length > 1) {
                servicePath = args[1];
            }
            if (args.length > 2) {
                debugPattern = Boolean.parseBoolean(args[2]);
            }
        }
        SparkSession spark = SparkSession
                .builder()
                .master(servicePath)
                .appName("BDA - Case Study - Capocchiano Narracci: Climate Change Detection")
                .getOrCreate();

        Dataset<Row> df = spark.read().format("csv")
                .option("header", "true")
                .option("delimiter", ",")
                .option("inferSchema", "true")
                .csv(filePath + "/*");
        var result = df.drop("AWND");
        result = result.drop("AWND");
        Column col = result.col("DATE");
        result = result.withColumn("PERIOD", col);
        result = result.filter(result.col("TOBS").isNotNull());
        result = result.groupBy("DATE").df().sort("DATE");
        JavaPairRDD<String, Iterable<Relevation>> datasets = prepareDataset(result);
        // dataset.saveAsTextFile("C:\\Spark\\test2");
        float minFreq = 0.25f;
        float minChange = 0.2f;

        try {
            datasets.collect().forEach(el -> {
                log.info("Starting new PBCD on period: " + el._1);
                int blockSize = getDaysOfMonth(el._1);
                PBCD<Relevation, ClimateData, TidSet, Boolean> detector = getPBCD(minFreq, minChange, blockSize);
                detectChanges(detector);
                el._2.forEach(detector);
            });
        } catch (Exception e) {
            log.error("Error during Change Detection", e);
        }
    }

    private static int getDaysOfMonth(String month) {
        switch (month) {
            case "04":
            case "06":
            case "09":
            case "11":
                return 30;
            case "02":
                return 28;
            default:
                return 31;
        }
    }

    private static JavaPairRDD<String, Iterable<Relevation>> prepareDataset(Dataset<Row> result) {
        Dataset<ClimateData> climateDataset = result.as(Encoders.bean(ClimateData.class));
        climateDataset.foreach(cd -> {
            cd.formatPeriod(cd.getPeriod());
        });
        return generateRelevationDataset(climateDataset);
    }

    @Nullable
    private static Dataset<Row> readCsvFile(String filePath, SparkSession spark) throws IOException {
        return spark.read().format("csv")
                .option("header", "true")
                .option("delimiter", ",")
                .option("inferSchema", "true")
                .csv(filePath + "/*");
    }

    private static void detectChanges(PBCD<Relevation, ClimateData, TidSet, Boolean> detector) {
        detector.registerListener(new PBCDEventListener<ClimateData, TidSet>() {

            @Override
            public void patternUpdateCompleted(PatternUpdateCompletedEvent<ClimateData, TidSet> arg0) {
                if (debugPattern) {
                    log.info("pattern updated " + arg0);
                    for (org.jkarma.model.Transaction<ClimateData> climateData : arg0.getLatestBlock()) {
                        Collection<ClimateData> items = climateData.getItems();
                        items.forEach(el -> log.info("{}", el));
                    }
                    log.info("pattern details finished");
                }
            }

            @Override
            public void patternUpdateStarted(PatternUpdateStartedEvent<ClimateData, TidSet> arg0) {
                // do nothing
                // log.info("started");
            }

            @Override
            public void changeDetected(ChangeDetectedEvent<ClimateData, TidSet> event) {
                log.info("");
                log.info("change detected: " + event.getAmount());
                log.info("\tdescribed by:");
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
                    log.info("\t\t" + p.getItemSet() + " " + message);
                });
            }

            @Override
            public void changeNotDetected(ChangeNotDetectedEvent<ClimateData, TidSet> arg0) {
                // log.info("change not detected: " + arg0.getAmount());
            }

            @Override
            public void changeDescriptionCompleted(ChangeDescriptionCompletedEvent<ClimateData, TidSet> arg0) {
                // do nothing
                // log.info("Descriptor changed");
            }

            @Override
            public void changeDescriptionStarted(ChangeDescriptionStartedEvent<ClimateData, TidSet> arg0) {
                // do nothing
            }
        });
    }

    private static JavaPairRDD<String, Iterable<Relevation>> generateRelevationDataset(
            Dataset<ClimateData> climateDataset) {
        JavaRDD<ClimateData> javaRDD = climateDataset.sort("DATE").toJavaRDD();
        JavaPairRDD<String, Iterable<ClimateData>> stringIterableJavaPairRDD = javaRDD.groupBy(ClimateData::getDate)
                .sortByKey();
        JavaRDD<Relevation> ungroupedDataset = stringIterableJavaPairRDD.map(
                el -> (new Relevation(StreamSupport.stream(el._2.spliterator(), false).collect(Collectors.toList()))));
        return ungroupedDataset
                .groupBy(rel -> rel.reads.stream().findFirst().get().getPeriod(), ungroupedDataset.getNumPartitions())
                .sortByKey();
    }

    public static PBCD<Relevation, ClimateData, TidSet, Boolean> getPBCD(float minFreq, float minChange,
            int blockSize) {
        // we prepare the time window model and the data accessor
        WindowingStrategy<TidSet> model = Windows.cumulativeSliding();
        TidSetProvider<ClimateData> accessor = new TidSetProvider<>(model);

        // we instantiate the pattern language delegate
        MixedClimateJoiner joiner = new MixedClimateJoiner();
        // we instantiate the mining strategy
        MiningStrategy<ClimateData, TidSet> strategy = Strategies.upon(joiner).eclat(minFreq).dfs(accessor);
        // we assemble the PBCD
        return Detectors.upon(strategy)
                .unweighted((p, t) -> Patterns.isFrequent(p, minFreq, t), new UnweightedJaccard())
                .describe(Descriptors.partialEps(minFreq, 1.00)).build(minChange, blockSize);
    }

}