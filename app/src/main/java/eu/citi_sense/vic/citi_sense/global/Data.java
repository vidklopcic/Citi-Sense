package eu.citi_sense.vic.citi_sense.global;

import android.graphics.Color;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import eu.citi_sense.vic.citi_sense.global.Databases.CachedData;

public class Data {
    public static final String LAST_FAB_POLLUTANT = "last-FAB-pollutant";
    public static final String LAST_LAT = "last-location-lat";
    public static final String LAST_LNG = "last-location-lng";
    CachedData mCachedData;
    public Data() {
        mCachedData = new CachedData();
    }

    public Integer getLastFABPollutant() {
        return Integer.decode(mCachedData.get(LAST_FAB_POLLUTANT, "1"));
    }

    public void setLastFABPollutant(Integer pollutant) {
        mCachedData.put(LAST_FAB_POLLUTANT, pollutant.toString());
    }

    public LineData getAQIData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            xVals.add(Integer.toString(i % 24));
        }

        ArrayList<Entry> yVals = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) + 3;
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        set1.setLineWidth(1.75f);
        set1.setCircleSize(0);
        set1.setColor(Color.WHITE);
        set1.setCircleColor(Color.WHITE);
        set1.setHighLightColor(Color.WHITE);
        set1.setDrawValues(false);
        set1.setDrawCubic(true);

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets

        return new LineData(xVals, dataSets);
    }

}
