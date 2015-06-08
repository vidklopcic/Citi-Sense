package eu.citi_sense.vic.citi_sense.global.Databases;

import com.orm.SugarRecord;

public class Measurement extends SugarRecord<Measurement> {
    Long time;
    Float value;
    String pollutant;
    String units;
    Station station;

    public Measurement() {
    }

    public Measurement(Long time, Float value, String pollutant, Station station,
                       String units) {
        this.time = time;
        this.value = value;
        this.pollutant = pollutant;
        this.station = station;
        this.units = units;
    }
}
