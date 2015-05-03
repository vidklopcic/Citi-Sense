package eu.citi_sense.vic.citi_sense.support_classes.map_activity;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterStation implements ClusterItem {
    private final LatLng mPosition;
    public String mId;
    public ClusterStation(double lat, double lng, String name) {
        mPosition = new LatLng(lat, lng);
        mId = name;
    }

    public String getId() {
        return mId;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

}
