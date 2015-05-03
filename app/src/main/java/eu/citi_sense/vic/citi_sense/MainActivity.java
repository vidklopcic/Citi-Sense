package eu.citi_sense.vic.citi_sense;

import android.location.Location;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MainActivity extends MapBaseActivity {
    @Override
    protected void cameraChanged(CameraPosition cameraPosition) {

    }

    @Override
    protected void mapClicked(LatLng latLng) {

    }

    @Override
    public void locationChanged(Location location) {

    }

    @Override
    protected void mapLongClicked(LatLng latLng) {
        mPointOfInterestMarker = addMarker(latLng);
    }
}
