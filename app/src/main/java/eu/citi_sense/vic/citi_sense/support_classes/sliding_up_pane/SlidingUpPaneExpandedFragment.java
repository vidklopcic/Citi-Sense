package eu.citi_sense.vic.citi_sense.support_classes.sliding_up_pane;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import eu.citi_sense.vic.citi_sense.R;
import eu.citi_sense.vic.citi_sense.global.Databases.FavoritePlace;

public class SlidingUpPaneExpandedFragment extends Fragment {
    private RelativeLayout mFragmentView;
    private ListView mFavoritePlacesListView;
    private FavoritePlacesAdapter mFavoritePlacesAdapter;
    private ArrayList<FavoritePlace> mFavoritePlaces;
    private Activity mActivity;
    private PlaceClickListener mPlaceClickListener;
    private RelativeLayout mEditPlaceLayout;
    private Integer mEditItemPosition;
    public interface PlaceClickListener {
        void onClick(FavoritePlace place);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return mFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        if (mFragmentView != null) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) mActivity.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFragmentView = (RelativeLayout) inflater.inflate(R.layout.expanded_sliding_pane_fragmnet, null);
        mEditPlaceLayout = (RelativeLayout) inflater.inflate(R.layout.edit_favorite_place_popup, null);
        mFavoritePlaces = new ArrayList<>(FavoritePlace.listAll(FavoritePlace.class));
        mFavoritePlacesListView = (ListView) mFragmentView.findViewById(R.id.favorite_places_list_view);
        mFavoritePlacesAdapter = new FavoritePlacesAdapter(mActivity.getApplicationContext(), mFavoritePlaces);
        mFavoritePlacesListView.setAdapter(mFavoritePlacesAdapter);
        mFavoritePlacesAdapter.setOnClickListener(new FavoritePlacesAdapter.PlaceClickListener() {
            @Override
            public void onEditClick(View view) {
                mEditItemPosition = (Integer) view.getTag();
                final PopupWindow popupWindow = new PopupWindow(mEditPlaceLayout, LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindow.showAtLocation(mFragmentView, Gravity.CENTER, 0, 0);
                Button removeButton = (Button) mEditPlaceLayout.findViewById(R.id.place_remove_btn);
                Button saveButton = (Button) mEditPlaceLayout.findViewById(R.id.place_save_btn);
                EditText name = (EditText) mEditPlaceLayout.findViewById(R.id.place_name_edit);
                LinearLayout wrapper = (LinearLayout) mEditPlaceLayout.findViewById(R.id.place_popup_wrapper);
                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });
                wrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });


            }

            @Override
            public void onPlaceClick(View view) {
                if (mPlaceClickListener != null) {
                    mPlaceClickListener.onClick(mFavoritePlacesAdapter.getItem((Integer) view.getTag()));
                }
            }
        });
    }

    public void setOnPlaceClickListener(PlaceClickListener listener) {
        mPlaceClickListener = listener;
    }

    public void addFavoritePlace(FavoritePlace place) {
        mFavoritePlaces.add(place);
    }

    public void removeFavoritePlace(LatLng position) {
        for (FavoritePlace place : mFavoritePlaces) {
            if (position.equals(new LatLng(place.lat, place.lng))) {
                mFavoritePlaces.remove(place);
            }
        }
    }
}
