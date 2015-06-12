package eu.citi_sense.vic.citi_sense.support_classes.sliding_up_pane;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.HashMap;
import java.util.Set;
import java.util.zip.Inflater;

import eu.citi_sense.vic.citi_sense.R;
import eu.citi_sense.vic.citi_sense.global.Pollutants;
import eu.citi_sense.vic.citi_sense.support_classes.map_activity.LocalTileProvider;

public class SlidingUpPaneCollapsedFragment extends Fragment {
    private RelativeLayout mFragmentView;
    private FlowLayout mPollutantsWrapper;
    private HashMap<Integer, RelativeLayout> mPollutantCards;
    private Activity mActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return mFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        mPollutantCards = new HashMap<>();
        LayoutInflater layoutInflater = (LayoutInflater) mActivity.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFragmentView = (RelativeLayout) layoutInflater.inflate(R.layout.collapsed_sliding_pane_fragmanet, null);
        mPollutantsWrapper = (FlowLayout) mFragmentView.findViewById(R.id.pollutant_cards_wrapper);
    }

    public void addPollutant(Pollutants pollutant) {
        if(mPollutantCards.containsKey(pollutant.current)) {
            updatePollutant(pollutant);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(mActivity.getApplicationContext());
        RelativeLayout pollutantCard = (RelativeLayout) inflater.inflate(R.layout.sliding_pane_pollutant, null, false);
        mPollutantsWrapper.addView(pollutantCard);
        mPollutantCards.put(pollutant.current, pollutantCard);
        updatePollutant(pollutant);
    }

    public void updatePollutant(Pollutants pollutant) {
        RelativeLayout container = (RelativeLayout) mPollutantCards.get(pollutant.current)
                .getChildAt(0);
        TextView text = (TextView) container.getChildAt(0);
        text.setText(pollutant.getUnicodeName(pollutant.current));
        text.setTextColor(pollutant.color);
        TextView aqi = (TextView) container.getChildAt(1);
        aqi.setText(pollutant.aqi.toString());
    }

    public void clearPollutants() {
        Set<Integer> pollutatns = mPollutantCards.keySet();
        for(int pollutant : pollutatns) {
            removePollutant(pollutant);
        }
    }

    public void removePollutant(int pollutant) {
        RelativeLayout pollutantCard = mPollutantCards.get(pollutant);
        ((ViewManager)pollutantCard.getParent()).removeView(pollutantCard);
        mPollutantCards.remove(pollutant);
    }
}
