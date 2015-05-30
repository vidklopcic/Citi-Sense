package eu.citi_sense.vic.citi_sense;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MainActivity extends MapBaseActivity {
    @Override
    protected void cameraChanged(CameraPosition cameraPosition) {
        if (!isMovingAuto) {
            mGlobalVariables.Map.moveCameraWithLocation = false;
        }
        isMovingAuto = false;
    }

    @Override
    protected void mapClicked(LatLng latLng) {
        removePointOfInterest();
    }

    @Override
    public void locationChanged(Location location) {

    }

    @Override
    protected void mapLongClicked(LatLng latLng) {
        mGlobalVariables.Map.moveCameraWithLocation = false;
        setPointOfInterest(latLng);
        setPullupTitle(latLng);
        centerMap(latLng);
    }

    @Override
    protected void markerClicked(Marker marker) {
        if (marker.getSnippet().equals("your-location")) {
            removePointOfInterest();
            mGlobalVariables.Map.moveCameraWithLocation = true;
        }
        mPullupTitle.setText(marker.getTitle());
    }
}
