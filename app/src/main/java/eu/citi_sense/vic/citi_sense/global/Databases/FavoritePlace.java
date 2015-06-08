package eu.citi_sense.vic.citi_sense.global.Databases;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;

import java.util.ArrayList;

public class FavoritePlace extends SugarRecord<FavoritePlace> {
    public Double lat;
    public Double lng;
    public String name;
    public Long last_used;
    public String nickname;

    public FavoritePlace() {
    }

    public FavoritePlace(LatLng position, String name, String nickname) {
        this.lat = position.latitude;
        this.lng = position.longitude;
        this.name = name;
        this.last_used = System.currentTimeMillis();
        this.nickname = nickname;
    }

    public ArrayList<FavoritePlace> getFavoritePlaces() {
        return new ArrayList<>(
                FavoritePlace.listAll(FavoritePlace.class)
        );
    }

    public void setUsed() {
        this.last_used = System.currentTimeMillis();
        this.save();
    }
}
