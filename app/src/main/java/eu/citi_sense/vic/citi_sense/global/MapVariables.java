package eu.citi_sense.vic.citi_sense.global;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class MapVariables {
    public LatLng location = null;
    public CameraPosition cameraPosition = null;
    public boolean moveCameraWithLocation = false;
    public static final int animationDuration = 200;
    public LatLng centerOnResume = null;
}
