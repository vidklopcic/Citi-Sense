package eu.citi_sense.vic.citi_sense;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import eu.citi_sense.vic.citi_sense.global.Data;
import eu.citi_sense.vic.citi_sense.global.GlobalVariables;
import eu.citi_sense.vic.citi_sense.global.Pollutants;
import eu.citi_sense.vic.citi_sense.support_classes.Charts;
import eu.citi_sense.vic.citi_sense.support_classes.station_activity.PollutantsAdapter;


public class StationActivity extends Activity {
    public static final int PORTABLE_STATION = 0;
    public static final int STATIC_STATION = 1;
    public static final String STATION_TYPE_KEY = "station-type";
    public static final String LATITUDE_KEY = "lat";
    public static final String LONGITUDE_KEY = "lng";


    private ArrayList<Pollutants> mPollutants;
    private ListView mPollutantsListView;
    private GlobalVariables mGVar;
    private LatLng mPosition;
    private int mStationType;
    private Charts mCharts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);
        Bundle bundle = getIntent().getExtras();
        mStationType = bundle.getInt(STATION_TYPE_KEY);
        if (mStationType == STATIC_STATION) {
            mPosition = new LatLng(
                    bundle.getLong(LATITUDE_KEY), bundle.getLong(LONGITUDE_KEY));
        }
        mGVar = (GlobalVariables) getApplicationContext();
        mPollutantsListView = (ListView) findViewById(R.id.pollutants_station_list_view);
        setUpPollutants();
        ArrayAdapter<Pollutants> adapter = new PollutantsAdapter(this, mPollutants);
        mPollutantsListView.setAdapter(adapter);

        LineData data = new Data().getAQIData(24, 3);
        LineChart chart = (LineChart) findViewById(R.id.stations_chart);
        mCharts = new Charts();
        mCharts.setupAQIStationChart(data, chart);
    }

    private void setUpPollutants() {
        Pollutants pollutants = new Pollutants(this);
        mPollutants = new ArrayList<>();
        for(int i=0;i< Pollutants.nOfPollutants;i++) {
            mPollutants.add(pollutants.getPollutant(i+1));
        }
    }

    public void locationFABClicked(View view) {
        if (mStationType == PORTABLE_STATION) {
            mGVar.mMap.moveCameraWithLocation = true;
            mGVar.mMap.centerOnResume = mGVar.mMap.location;
        } else if (mStationType == STATIC_STATION) {
            mGVar.mMap.centerOnResume = mPosition;
        }
        finish();
    }

    public void analysisFABClicked(View view) {
    }

    public void backButtonClicked(View view) {
        this.finish();
    }
}
