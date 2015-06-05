package eu.citi_sense.vic.citi_sense.global.Databases;

import com.orm.SugarRecord;

public class Measurement extends SugarRecord<Measurement> {
    String time;
    String value;
    String pollutant;
    Station station;
    String units;

    public Measurement() {
    }

    public Measurement(Integer time, Float value, String pollutant, Station station,
                       String units) {
        this.time = Integer.toString(time);
        this.value = Float.toString(value);
        this.pollutant = pollutant;
        this.station = station;
        this.units = units;
    }

}
