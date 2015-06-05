package eu.citi_sense.vic.citi_sense.support_classes.sliding_menu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import eu.citi_sense.vic.citi_sense.R;

public class SlidingMenuHandler {
    public SlidingMenuHandler(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        SlidingMenu menu = new SlidingMenu(context);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(context, SlidingMenu.SLIDING_CONTENT);
        menu.setBehindOffset((int) (size.x*0.1));
        menu.setMenu(R.layout.sliding_menu);
    }
}
