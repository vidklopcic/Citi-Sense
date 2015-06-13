package eu.citi_sense.vic.citi_sense.support_classes;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.FillFormatter;

import eu.citi_sense.vic.citi_sense.R;

public class Charts {
    public void setupAQISlidingChart(LineData data, LineChart chart) {
        int color = Color.argb(0, 0, 0, 0);


        // enable / disable grid background
        chart.setDrawGridBackground(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setBackgroundColor(color);
        chart.setHighlightEnabled(false);
        // set custom chart offsets (automatic offset calculation is hereby disabled)

        // add data
        chart.setData(data);
        chart.setDescription("");

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);

        chart.getAxisRight().setEnabled(false);
        XAxis xl = chart.getXAxis();
        xl.setEnabled(true);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTextColor(Color.WHITE);
        xl.setAxisLineColor(Color.WHITE);
        xl.setDrawGridLines(false);
        YAxis yl = chart.getAxisLeft();
        yl.setTextColor(Color.WHITE);
        yl.setDrawAxisLine(false);
        yl.setDrawGridLines(true);
        yl.setEnabled(true);


        chart.getXAxis().setEnabled(true);

        // animate calls invalidate()...
        chart.invalidate();
    }

    public void setupAQIStationChart(LineData data, LineChart chart) {
        int color = Color.argb(0, 0, 0, 0);
        LineDataSet set = data.getDataSets().get(0);
        set.setDrawFilled(true);
        set.setFillColor(Color.rgb(2, 187, 210));
        set.setFillAlpha(255);

        // enable / disable grid background
        chart.setDrawGridBackground(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);
        chart.setHighlightEnabled(false);

        chart.setBackgroundColor(color);

        // set custom chart offsets (automatic offset calculation is hereby disabled)

        // add data
        chart.setData(data);
        chart.setDescription("");

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);

        chart.getAxisRight().setEnabled(false);
        XAxis xl = chart.getXAxis();
        xl.setEnabled(true);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTextColor(Color.WHITE);
        xl.setAxisLineColor(Color.WHITE);
        xl.setDrawGridLines(false);
        YAxis yl = chart.getAxisLeft();
        yl.setDrawAxisLine(false);
        yl.setDrawGridLines(true);
        yl.setDrawLabels(false);
        yl.setAxisMaxValue(data.getYMax()*4);
        yl.setEnabled(true);


        chart.getXAxis().setEnabled(true);

        // animate calls invalidate()...
        chart.invalidate();
    }
}
