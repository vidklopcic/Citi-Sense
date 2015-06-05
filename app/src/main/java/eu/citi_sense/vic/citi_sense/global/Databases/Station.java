package eu.citi_sense.vic.citi_sense.global.Databases;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;

public class Station extends SugarRecord<Station> {
    String lat;
    String lng;
    String color;
    String owner;
    String jPollutants;
    String city;

    public Station() {

    }

    public Station(String owner, LatLng latLng,
                   String color, String[] pollutants, String city) {
        this.owner = owner;
        this.lat = Double.toString(latLng.latitude);
        this.lng = Double.toString(latLng.longitude);
        this.jPollutants = new flexjson.JSONSerializer().serialize(pollutants);
        this.color = color;
        this.city = city;
    }
}
