package eu.citi_sense.vic.citi_sense;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.google.android.gms.maps.model.LatLng;

import eu.citi_sense.vic.citi_sense.global.GlobalVariables;
import eu.citi_sense.vic.citi_sense.support_classes.Charts;
import eu.citi_sense.vic.citi_sense.support_classes.sliding_menu.SlidingMenuHandler;
import eu.citi_sense.vic.citi_sense.support_classes.sliding_menu.SlidingMenuListeners;


public class AnalysisActivity extends Activity {
    private GlobalVariables mGVar;
    private Charts mCharts = new Charts();
    private TextView mActionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        mGVar = (GlobalVariables) getApplicationContext();
        mActionBarTitle = (TextView) findViewById(R.id.analysis_action_bar_title);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
    }


    private void setActionBarTitle(String title) {
        mActionBarTitle.setText(title);
    }

    public void backButtonClicked(View view) {
        finish();
    }
}
