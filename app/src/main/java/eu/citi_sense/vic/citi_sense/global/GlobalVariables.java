package eu.citi_sense.vic.citi_sense.global;
import android.app.Application;
import android.content.SharedPreferences;

public class GlobalVariables extends Application {
    public MapVariables mMap = new MapVariables();
    public Pollutants Pollutant = new Pollutants();
    public SharedPreferencesKeys Keys = new SharedPreferencesKeys();
}

