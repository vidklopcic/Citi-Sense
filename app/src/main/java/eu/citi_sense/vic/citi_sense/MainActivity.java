package eu.citi_sense.vic.citi_sense;

import android.location.Location;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends MapBaseActivity {
    @Override
    protected void cameraChanged(CameraPosition cameraPosition) {
        if (!isMovingAuto) {
            mGVar.mMap.moveCameraWithLocation = false;
        }
        isMovingAuto = false;
    }

    @Override
    protected void mapClicked(LatLng latLng) {
        removePointOfInterest();
        mSlidingUpPane.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    @Override
    public void locationChanged(Location location) {

    }

    @Override
    protected void mapLongClicked(LatLng latLng) {
        if (mSlidingUpPane.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
            mSlidingUpPane.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        mGVar.mMap.moveCameraWithLocation = false;
        setPointOfInterest(latLng);
        setPullupTitle(latLng);
        centerMap(latLng);
    }

    @Override
    protected void markerClicked(Marker marker) {
        if (marker.getSnippet() != null) {
            removePointOfInterest();
            mGVar.mMap.moveCameraWithLocation = true;
        }
        mPullupTitle.setText(marker.getTitle());
        if (mSlidingUpPane.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
            mSlidingUpPane.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }
}
