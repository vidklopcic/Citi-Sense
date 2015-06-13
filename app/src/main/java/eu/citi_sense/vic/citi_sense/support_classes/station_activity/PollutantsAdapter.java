package eu.citi_sense.vic.citi_sense.support_classes.station_activity;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import eu.citi_sense.vic.citi_sense.R;
import eu.citi_sense.vic.citi_sense.global.Pollutants;

public class PollutantsAdapter extends ArrayAdapter<Pollutants> {
    public PollutantsAdapter(Context context, ArrayList<Pollutants> pollutants) {
        super(context, 0, pollutants);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Pollutants pollutant = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_pollutant, parent, false);
        }
        TextView short_name = (TextView) convertView.findViewById(R.id.pollutant_item_pollutant_text);
        short_name.setText(pollutant.getUnicodeName(pollutant.current));
        GradientDrawable bgShape = (GradientDrawable) short_name.getBackground();
        bgShape.setColor(pollutant.color);
        TextView long_name = (TextView) convertView.findViewById(R.id.pollutant_item_pollutant_long_text);
        long_name.setText(pollutant.description);
        TextView value = (TextView) convertView.findViewById(R.id.pollutant_item_value);
        value.setText(pollutant.raw_value.toString() + pollutant.raw_value_unit);
        return convertView;
    }
}
