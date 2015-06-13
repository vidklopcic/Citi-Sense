package eu.citi_sense.vic.citi_sense;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import eu.citi_sense.vic.citi_sense.global.Databases.Measurement;
import eu.citi_sense.vic.citi_sense.global.Databases.Station;
import eu.citi_sense.vic.citi_sense.global.GlobalVariables;
import eu.citi_sense.vic.citi_sense.global.Pollutants;
import eu.citi_sense.vic.citi_sense.support_classes.Charts;
import eu.citi_sense.vic.citi_sense.support_classes.sliding_menu.SlidingMenuHandler;
import eu.citi_sense.vic.citi_sense.support_classes.sliding_menu.SlidingMenuListeners;


public class StationsActivity extends Activity {
    private static final int DAY = 24*60*60*1000;
    private static final int HOUR = 60*60*1000;
    private static final int MINUTE = 60*1000;
    private GlobalVariables mGVar;
    private Charts mCharts = new Charts();
    private SlidingMenuHandler mSlidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        mGVar = (GlobalVariables) getApplicationContext();
        mSlidingMenu = new SlidingMenuHandler(this);
        LineData data = mGVar.data.getAQIData(24, 5);
        LineChart chart = (LineChart) findViewById(R.id.sliding_menu_chart);
        mCharts.setupAQISlidingChart(data, chart);
        LinearLayout mapButton = (LinearLayout) findViewById(R.id.sliding_menu_map);
        LinearLayout analysisButton = (LinearLayout) findViewById(R.id.sliding_menu_analysis);
        LinearLayout stationsButton = (LinearLayout) findViewById(R.id.sliding_menu_stations);
        new SlidingMenuListeners(mapButton, analysisButton, stationsButton,
                SlidingMenuListeners.STATIONS_ACTIVITY, mSlidingMenu, this);

        generateRandomStations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGVar.mMap.centerOnResume != null) {
            finish();
        }
    }

    private void generateRandomStations() {
        if (Station.listAll(Station.class).size() != 0) {
            return;
        }
        tmp(new LatLng(46.077343895679604, 14.516583234071733));
        tmp(new LatLng(46.069013769050486,14.483683928847315));
        tmp(new LatLng(46.05769628259097, 14.548797234892847));
        tmp(new LatLng(46.026138889927836, 14.55143518745899));
        tmp(new LatLng(46.03404208961555,14.497980773448946));
        tmp(new LatLng(46.063427549117954,14.533101953566074));
        tmp(new LatLng(46.044498032578915,14.529215097427368));
        tmp(new LatLng(46.04102930675535,14.470218569040298));
        tmp(new LatLng(46.073402512051366,14.473951198160648));
        tmp(new LatLng(46.05379514485974,14.495205022394655));
        tmp(new LatLng(46.0550953220546,14.51019689440727));
        tmp(new LatLng(46.01526246150808,14.553770720958708));
        tmp(new LatLng(46.04337889485482,14.514065310359003));
    }
    private void tmp(LatLng position) {
        ArrayList<Measurement> measurementsArray = new ArrayList<>();
        Integer[] pollutants = new Integer[] {1,2,3,3,4,5,6,7,8};
        Station station = new Station("Citi-sense", position, "#fff", pollutants, "Ljubljana");

        for(int pollutant : pollutants) {
            Integer value = (int)(Math.random() * 301);
            Integer offset = 0;
            for (int i=0; i<DAY; i+=10*MINUTE) {
                offset++;
                measurementsArray.add(new Measurement(
                        System.currentTimeMillis(), Float.valueOf(value)+offset,
                        Pollutants.pollutantNames.get(pollutant), station, "ppb"));
            }
        }
        Measurement.saveInTx(measurementsArray);
        station.save();
        Log.d("asdfg", "finished");
    }

    public void menuBtnClicked(View view) {
        mSlidingMenu.menu.showMenu(true);
    }

    public void portableStationClicked(View view) {
        Intent intent = new Intent(this, StationActivity.class);
        intent.putExtra(StationActivity.STATION_TYPE_KEY, StationActivity.PORTABLE_STATION);
        this.startActivity(intent);
    }
}