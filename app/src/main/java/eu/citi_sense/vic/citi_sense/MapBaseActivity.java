package eu.citi_sense.vic.citi_sense;

import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Map;
import eu.citi_sense.vic.citi_sense.global.GlobalVariables;
import eu.citi_sense.vic.citi_sense.support_classes.map_activity.ClusterStation;

public abstract class MapBaseActivity extends FragmentActivity {
    public GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ClusterManager<ClusterStation> mClusterManager;
    public GlobalVariables mGlobalVariables;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mPreferencesEditor;
    private Marker mCurrentLocationMarker = null;
    public Marker mPointOfInterestMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mGlobalVariables = (GlobalVariables) getApplicationContext();
        mSharedPreferences = getSharedPreferences(
                getString(R.string.shared_references_name), MODE_PRIVATE
        );
        mPreferencesEditor = mSharedPreferences.edit();

        setUpMapIfNeeded();
        setUpListeners();

        setUpClusterer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGlobalVariables.Map.cameraPosition = mMap.getCameraPosition();
    }

    protected abstract void cameraChanged(CameraPosition cameraPosition);
    protected abstract void mapClicked(LatLng latLng);
    protected abstract void locationChanged(Location location);
    protected abstract void mapLongClicked(LatLng latLng);

    private void setUpListeners() {
        GoogleMap.OnMapLongClickListener onMapLongClickListener = new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (mPointOfInterestMarker != null) {
                    mPointOfInterestMarker.remove();
                }
                mapLongClicked(latLng);
            }
        };
        mMap.setOnMapLongClickListener(onMapLongClickListener);

        GoogleMap.OnMapClickListener onMapClickListener = new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mapClicked(latLng);
            }
        };
        mMap.setOnMapClickListener(onMapClickListener);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (mGlobalVariables.Map.location == null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                }
                if (mCurrentLocationMarker == null) {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .alpha(0);
                    mCurrentLocationMarker = mMap.addMarker(markerOptions);
                }
                mCurrentLocationMarker.setPosition(latLng);
                mGlobalVariables.Map.location = latLng;
                locationChanged(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        ArrayList<String> providers = new ArrayList<>(locationManager.getProviders(true));
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

        GoogleMap.OnCameraChangeListener onCameraChangeListener = new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                cameraChanged(cameraPosition);
            }
        };
        mMap.setOnCameraChangeListener(onCameraChangeListener);

        GoogleMap.OnMarkerClickListener onMarkerClickListener = new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("asdfasdf", marker.getId());
                return false;
            }
        };
        mMap.setOnMarkerClickListener(onMarkerClickListener);

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
        mMap.setBuildingsEnabled(true);
        mMap.setMyLocationEnabled(true);

        if (mGlobalVariables.Map.cameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mGlobalVariables.Map.cameraPosition));
        }
    }

    private void setUpClusterer() {
        mClusterManager = new ClusterManager<ClusterStation>(this, mMap);
        updateStations();
    }

    private void updateStations() {
        mClusterManager.clearItems();
        if (mGlobalVariables.Map.stations != null) {
           for(Map.Entry<String, double[]> station: mGlobalVariables.Map.stations.entrySet()) {
               double[] latLng = station.getValue();
               mClusterManager.addItem(new ClusterStation(latLng[0], latLng[1], station.getKey()));
           }
        }
    }

    public Marker addMarker(LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position);
        return mMap.addMarker(markerOptions);
    }

}
