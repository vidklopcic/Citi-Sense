package eu.citi_sense.vic.citi_sense.support_classes.map_activity;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import eu.citi_sense.vic.citi_sense.global.Databases.Station;

public class ClusterStation implements ClusterItem {
    private final LatLng mPosition;
    public String mName;
    public Station station;
    public ClusterStation(LatLng position, String name, Station station) {
        mPosition = position;
        mName = name;
        this.station = station;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
