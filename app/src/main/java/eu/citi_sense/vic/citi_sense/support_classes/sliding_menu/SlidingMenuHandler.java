package eu.citi_sense.vic.citi_sense.support_classes.sliding_menu;

import android.app.Activity;
import android.content.Context;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import eu.citi_sense.vic.citi_sense.R;

public class SlidingMenuHandler {
    public SlidingMenuHandler(Activity context) {
        SlidingMenu menu = new SlidingMenu(context);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(context, SlidingMenu.SLIDING_CONTENT);
        menu.setBehindOffset(200);
        menu.setMenu(R.layout.sliding_menu);
    }
}
