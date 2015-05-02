package eu.citi_sense.vic.citi_sense.support_classes.map_activity;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterStation implements ClusterItem {
    private final LatLng mPosition;

    public ClusterStation(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

}
