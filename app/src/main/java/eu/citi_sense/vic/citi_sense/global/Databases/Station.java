package eu.citi_sense.vic.citi_sense.global.Databases;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;

import eu.citi_sense.vic.citi_sense.global.Pollutants;

public class Station extends SugarRecord<Station> {
    public Double lat;
    public Double lng;
    public String color;
    public String owner;
    public boolean CO = false;
    public boolean CO2 = false;
    public boolean H2O = false;
    public boolean NO = false;
    public boolean O3 = false;
    public boolean PM10 = false;
    public boolean PM25 = false;
    public boolean SO2 = false;
    public String city;

    public Station() {
    }

    public Station(String owner, LatLng latLng,
                   String color, Integer[] pollutants, String city) {
        this.owner = owner;
        this.lat = latLng.latitude;
        this.lng = latLng.longitude;
        for(int pollutant: pollutants) {
            addPollutant(pollutant);
        }
        this.color = color;
        this.city = city;
    }

    public void addPollutant(int pollutant) {
        switch (pollutant) {
            case 1:
                CO = true;
                break;
            case 2:
                CO2 = true;
                break;
            case 3:
                H2O = true;
                break;
            case 4:
                NO = true;
                break;
            case 5:
                O3 = true;
                break;
            case 6:
                PM10 = true;
                break;
            case 7:
                PM25 = true;
                break;
            case 8:
                SO2 = true;
                break;
        }
    }

    public void putMeasurement(Integer pollutant, Float value, Long time, String units) {
        putMeasurement(Pollutants.pollutantNames.get(pollutant), value, time, units);
    }

    public void putMeasurement(String pollutant, Float value, Long time, String units) {
        Measurement newMeasurement = new Measurement(
                        time, value, pollutant, this, units);
        newMeasurement.save();
    }

    public ArrayList<Measurement> getMeasurements(Integer startTime, Integer endTime) {
        return new ArrayList<>(
                Measurement.find(
                    Measurement.class, "station = ? and time between ? and ?",
                    this.id.toString(), startTime.toString(), endTime.toString())
        );
    }

    public LatLng getPosition() {
        return new LatLng(lat, lng);
    }
    public Measurement getClosestMeasurement(Long time) {
        return Measurement.findWithQuery(Measurement.class,
                "SELECT MAX(time) FROM Measurement WHERE time < "+time.toString()).get(0);
    }

    public ArrayList<Station> getStations(String region) {
        return new ArrayList<>(
                Station.listAll(Station.class)
        );
    }
}
