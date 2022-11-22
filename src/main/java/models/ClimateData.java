package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import scala.Serializable;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Comparator;

@Data
@NoArgsConstructor
@JsonPropertyOrder({ "station", "name", "date", "prcp", "snow", "tmax", "tmin", "tobs" })
public class ClimateData implements Comparable<ClimateData>, Serializable {
    String station;
    String name;
    String date;
    String prcp; // mm
    String snow; // mm
    String tmax; // Celsius
    String tmin; // Celsius
    String tobs; // Celsius
    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    public boolean samePeriod = false;

    private static Comparator<ClimateData> comparator = Comparator.comparing(ClimateData::getDate)
            .thenComparing(ClimateData::getPrcp, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(ClimateData::getSnow, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(ClimateData::getTmin, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(ClimateData::getTmax, Comparator.nullsFirst(Comparator.naturalOrder()));

    public ClimateData(ClimateData data) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            var date = parser.parse(data.date);
            parser = new java.text.SimpleDateFormat("MMMM");
            this.date = parser.format(date);
        } catch (Exception e) {
            this.date = data.date;
        }

        this.prcp = data.prcp;
        this.snow = data.snow;
        this.tmax = data.tmax;
        this.tmin = data.tmin;
        this.tobs = data.tobs;
    }

    @Override
    public int compareTo(@NotNull ClimateData o) {
        return comparator.compare(this, o);
    }
}
