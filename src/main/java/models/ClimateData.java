package models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

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


    public ClimateData(ClimateData data) {
        this.date = data.date;
        this.prcp = data.prcp;
        this.snow = data.snow;
        this.tmax = data.tmax;
        this.tmin = data.tmin;
        this.tobs = data.tobs;
    }

    @Override
    public int compareTo(ClimateData o) {
        return prcp == null ? 0 : prcp.compareTo(o.getPrcp());
    }
}
