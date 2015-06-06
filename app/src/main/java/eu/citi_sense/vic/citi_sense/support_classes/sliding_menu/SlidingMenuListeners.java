package eu.citi_sense.vic.citi_sense.support_classes.sliding_menu;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import eu.citi_sense.vic.citi_sense.AnalysisActivity;
import eu.citi_sense.vic.citi_sense.MapBaseActivity;
import eu.citi_sense.vic.citi_sense.StationsActivity;

public class SlidingMenuListeners {
    public static final Integer MAP_ACTIVITY = 0;
    public static final Integer ANALYSIS_ACTIVITY = 1;
    public static final Integer STATIONS_ACTIVITY = 2;

    private LinearLayout mapButton;
    private LinearLayout analysisButton;
    private LinearLayout stationsButton;
    private Integer currentActivity;
    private Activity context;
    private SlidingMenuHandler slidingMenu;
    public SlidingMenuListeners(LinearLayout mapButton, LinearLayout analysisButton,
                                LinearLayout stationsButton, Integer currentActivity,
                                SlidingMenuHandler slidingMenu, Activity context) {
        this.currentActivity = currentActivity;
        this.mapButton = mapButton;
        this.analysisButton = analysisButton;
        this.stationsButton = stationsButton;
        this.context = context;
        this.slidingMenu = slidingMenu;
        setListeners();
    }

    private void setListeners() {
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentActivity != MAP_ACTIVITY) {
                    context.finish();
                } else {
                    slidingMenu.menu.toggle();
                }
            }
        });
        analysisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentActivity != ANALYSIS_ACTIVITY) {
                    Intent mapActivityIntent = new Intent(context, AnalysisActivity.class);
                    context.startActivity(mapActivityIntent);
                    slidingMenu.menu.toggle();
                    if (currentActivity != MAP_ACTIVITY) {
                        context.finish();
                    }
                } else {
                    slidingMenu.menu.toggle();
                }
            }
        });
        stationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentActivity != STATIONS_ACTIVITY) {
                    Intent mapActivityIntent = new Intent(context, StationsActivity.class);
                    context.startActivity(mapActivityIntent);
                    slidingMenu.menu.toggle();
                    if (currentActivity != MAP_ACTIVITY) {
                        context.finish();
                    }
                } else {
                    slidingMenu.menu.toggle();
                }
            }
        });

    }
}
