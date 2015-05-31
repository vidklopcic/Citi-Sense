package eu.citi_sense.vic.citi_sense.support_classes.map_activity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Places {
    Context mContext;
    public Places(Context context) {
        mContext = context;
    }

    public String getAddress(Double lat, Double lon) {
        Geocoder geocoder = new Geocoder(mContext, Locale.ENGLISH);
        String address = "Somewhere";
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                address = returnedAddress.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }
    public LatLng getClosest(LatLng[] places, LatLng location) {
        double currentMinLocation = Double.MAX_VALUE;
        LatLng result = null;
        for (LatLng place: places) {
            double dist = distance(place, location);
            if (dist < currentMinLocation) {
                currentMinLocation = dist;
                result = place;
            }
        }
        return result;
    }

    public double distance(LatLng start, LatLng end) {
        double lat1 = start.latitude;
        double lon1 = start.longitude;
        double lat2 = end.latitude;
        double lon2 = end.longitude;

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60;
        dist = dist * 1852;
        return (dist);
    }

    public double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
