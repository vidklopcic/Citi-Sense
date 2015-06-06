package eu.citi_sense.vic.citi_sense.global;

import android.graphics.Color;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import eu.citi_sense.vic.citi_sense.R;
import eu.citi_sense.vic.citi_sense.global.Databases.CachedData;

public class Data {
    public String LAST_FAB_POLLUTANT = "last-FAB-pollutant";
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

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add(Integer.toString(i % 24));
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

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

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        return data;
    }

}
