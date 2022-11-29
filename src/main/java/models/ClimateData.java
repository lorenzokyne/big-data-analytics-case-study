package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import scala.Serializable;

import java.text.SimpleDateFormat;
import java.util.Comparator;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"station", "name", "date", "prcp", "snow", "tmax", "tmin", "tobs", "period"})
public class ClimateData implements Comparable<ClimateData>, Serializable {
    String station;
    String name;
    String date;
    Double prcp; // mm
    Double snow; // mm
    Double tmax; // Celsius
    Double tmin; // Celsius
    Double tobs; // Celsius
    String period;
    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    public boolean samePeriod = false;

    private static Comparator<ClimateData> comparator = Comparator.comparing(ClimateData::getPeriod)
            .thenComparing(ClimateData::getPrcp, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(ClimateData::getSnow, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(ClimateData::getTmin, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(ClimateData::getTmax, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(ClimateData::getTobs, Comparator.nullsFirst(Comparator.naturalOrder()));

    public ClimateData(ClimateData data) {
        this.formatPeriod(data.period);
        this.date = data.date;
        this.prcp = data.prcp;
        this.snow = data.snow;
        this.tmax = data.tmax;
        this.tmin = data.tmin;
        this.tobs = data.tobs;
    }

    public void formatPeriod(String newDate) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            var date = parser.parse(newDate);
            parser = new java.text.SimpleDateFormat("MMMM");
            this.period = parser.format(date);
        } catch (Exception e) {
            this.period = newDate;
        }
    }

    @Override
    public int compareTo(@NotNull ClimateData o) {
        return comparator.compare(this, o);
    }

    @SneakyThrows
    @Override
    public String toString() {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        var formattedDate = parser.parse(date);
        parser = new java.text.SimpleDateFormat("dd-MM-yyyy");
        return "date=" + parser.format(formattedDate) +
                ", prcp=" + prcp + " mm" +
                ", snow=" + snow + " mm" +
                ", tmax=" + tmax + "°C" +
                ", tmin=" + tmin + "°C" +
                ", tobs=" + tobs + "°C" +
                ", period=" + period;
    }
}
