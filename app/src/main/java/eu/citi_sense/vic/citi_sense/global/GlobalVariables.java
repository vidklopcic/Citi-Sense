package eu.citi_sense.vic.citi_sense.global;
import android.app.Application;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class GlobalVariables extends Application {
    public HashMap<String, double[]> stations = null;
    public LatLng location = null;
}
