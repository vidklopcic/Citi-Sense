package eu.citi_sense.vic.citi_sense.support_classes.map_activity;

import com.google.android.gms.maps.GoogleMap;

import eu.citi_sense.vic.citi_sense.global.GlobalVariables;

public class OverlayDrawer {
    private GoogleMap mMap;
    private GlobalVariables mGVars;
    public OverlayDrawer(GoogleMap map, GlobalVariables gvars) {
        mMap = map;
        mGVars = gvars;
    }

    public void changePollutant(Integer pollutant) {

    }
}
