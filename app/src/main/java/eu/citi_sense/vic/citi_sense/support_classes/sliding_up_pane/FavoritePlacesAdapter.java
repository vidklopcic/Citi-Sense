 package eu.citi_sense.vic.citi_sense.support_classes.sliding_up_pane;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import eu.citi_sense.vic.citi_sense.R;
import eu.citi_sense.vic.citi_sense.global.Databases.FavoritePlace;

public class FavoritePlacesAdapter extends ArrayAdapter<FavoritePlace> {
    private PlaceClickListener mPlaceClickListener;
    public FavoritePlacesAdapter(Context context, ArrayList<FavoritePlace> users) {
        super(context, 0, users);
    }

    public interface PlaceClickListener {
        void onEditClick(View view);
        void onPlaceClick(View view);
    }

    public void setOnClickListener(PlaceClickListener listener) {
        mPlaceClickListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FavoritePlace place = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_favorite_place, parent, false);
        }
        RelativeLayout container = (RelativeLayout) convertView.findViewById(R.id.favorite_place_container);
        container.setTag(position);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlaceClickListener != null) {
                    mPlaceClickListener.onPlaceClick(view);
                    mPlaceClickListener.onPlaceClick(view);
                }
            }
        });
        TextView name = (TextView) convertView.findViewById(R.id.favorite_place_name);
        name.setText(place.nickname);
        ImageView edit = (ImageView) convertView.findViewById(R.id.edit_icon);
        edit.setTag(position);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlaceClickListener != null) {
                    mPlaceClickListener.onEditClick(view);
                }
            }
        });
        return convertView;
    }
}
