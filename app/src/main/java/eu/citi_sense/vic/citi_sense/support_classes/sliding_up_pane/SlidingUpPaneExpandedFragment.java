package eu.citi_sense.vic.citi_sense.support_classes.sliding_up_pane;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import eu.citi_sense.vic.citi_sense.R;

public class SlidingUpPaneExpandedFragment extends Fragment {
    private RelativeLayout mFragmentView;
    private ListView mFavoritePlaces;
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
        LayoutInflater layoutInflater = (LayoutInflater) mActivity.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFragmentView = (RelativeLayout) layoutInflater.inflate(R.layout.expanded_sliding_pane_fragmnet, null);
        mFavoritePlaces = (ListView) mFragmentView.findViewById(R.id.favorite_places_list_view);
    }
}
