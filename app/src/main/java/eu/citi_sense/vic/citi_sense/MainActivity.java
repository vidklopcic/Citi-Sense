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
        if (mSlidingUpPane.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
            mSlidingUpPane.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
    }

    @Override
    public void locationChanged(Location location) {

    }

    @Override
    protected void mapLongClicked(LatLng latLng) {
        if (mSlidingUpPane.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
            mSlidingUpPane.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            hideFab();
            alreadyAnimatedFAB = true;
        }
        mGVar.mMap.moveCameraWithLocation = false;
        Marker marker = setPointOfInterest(latLng);
        setActionBarTitle(marker);
        centerMap(latLng);
    }

    @Override
    protected void markerClicked(Marker marker) {
        if (marker.getSnippet() != null && marker.getSnippet().equals("your-location")) {
            removePointOfInterest();
            mGVar.mMap.moveCameraWithLocation = true;
        }
        mActionBarFragment.setTitle(marker.getTitle(), marker.getPosition());
        if (marker.getTitle() == null) {
            setActionBarTitle(marker);
        }
        if (mSlidingUpPane.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
            mSlidingUpPane.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            hideFab();
            alreadyAnimatedFAB = true;
        }
    }
}
