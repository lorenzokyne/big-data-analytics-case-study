package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.var;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"station", "name", "date", "prcp", "snow", "tmax", "tmin", "tobs"})
public class ClimateData implements Comparable<ClimateData> {
    String station;
    String name;
    String date;
    String prcp; //mm
    String snow; //mm
    String tmax; //Celsius
    String tmin; //Celsius
    String tobs; //Celsius
    @JsonIgnore
    boolean samePeriod = false;

    public ClimateData(ClimateData data) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            var date = parser.parse(data.date);
            parser = new java.text.SimpleDateFormat("MMMM");
            this.date = parser.format(date);
        } catch (Exception e) {
            this.date = null;
        }

        this.prcp = data.prcp;
        this.snow = data.snow;
        this.tmax = data.tmax;
        this.tmin = data.tmin;
        this.tobs = data.tobs;
    }

    @Override
    public int compareTo(@NotNull ClimateData o) {
        return prcp == null ? 0 : date.equals(o.getDate()) ? prcp.compareTo(o.getPrcp()) + snow.compareTo(o.getSnow()) + tmin.compareTo(o.getTmin()) + tmax.compareTo(o.getTmax()) : 0;
    }
}
