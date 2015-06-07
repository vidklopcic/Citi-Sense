package eu.citi_sense.vic.citi_sense.global;
import android.app.Application;
import android.content.SharedPreferences;

import com.orm.SugarApp;

public class GlobalVariables extends SugarApp {
    public MapVariables mMap = new MapVariables();
    public Pollutants Pollutant;
    public SharedPreferencesKeys Keys = new SharedPreferencesKeys();
    public Data data = new Data();

    @Override
    public void onCreate() {
        super.onCreate();
        Pollutant = new Pollutants(getApplicationContext());
    }
}

