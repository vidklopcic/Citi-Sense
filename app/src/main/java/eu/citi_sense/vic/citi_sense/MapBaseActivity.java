package eu.citi_sense.vic.citi_sense;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import eu.citi_sense.vic.citi_sense.support_classes.map_activity.ClusterStation;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.Map;

import eu.citi_sense.vic.citi_sense.global.GlobalVariables;

public abstract class MapBaseActivity extends FragmentActivity {
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ClusterManager<ClusterStation> mClusterManager;
    private GlobalVariables globalVars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();
        setUpLocationListener();

        globalVars = (GlobalVariables) getApplicationContext();
        setUpClusterer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpLocationListener() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (globalVars.location == null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                }
                globalVars.location = latLng;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

    }

    private void setUpClusterer() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));
        mClusterManager = new ClusterManager<ClusterStation>(this, mMap);
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        updateItems();
    }

    private void updateItems() {
        mClusterManager.clearItems();
        if (globalVars.stations != null) {
           for(Map.Entry<String, double[]> station: globalVars.stations.entrySet()) {

           }
        }
    }

}
