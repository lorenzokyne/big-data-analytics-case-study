package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({"station", "name", "date", "awnd", "prcp", "snow", "tmax", "tmin", "tobs"})
public class ClimateData implements Comparable<ClimateData> {
    String station;
    String name;
    String date;
    String prcp; //mm
    String sn32; //mm
    String snow; //mm
    String sx32; //mm
    String tmax;//Celsius
    String tmin;//Celsius
    String tobs;//Celsius
    String awnd;//mm

    @Override
    public int compareTo(ClimateData o) {
        return prcp.compareTo(o.getPrcp());
    }
}
