package eu.citi_sense.vic.citi_sense.global;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class MapVariables {
    public HashMap<String, double[]> stations = null;
    public LatLng location = null;
    public CameraPosition cameraPosition = null;
    public boolean moveCameraWithLocation = false;
}
