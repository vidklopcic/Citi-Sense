package eu.citi_sense.vic.citi_sense;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Map;

import eu.citi_sense.vic.citi_sense.global.GlobalVariables;
import eu.citi_sense.vic.citi_sense.support_classes.map_activity.ClusterStation;
import eu.citi_sense.vic.citi_sense.support_classes.map_activity.Places;

public abstract class MapBaseActivity extends FragmentActivity {
    public GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public GlobalVariables mGlobalVariables;
    public Marker mPointOfInterestMarker = null;
    public Places mPlaces;
    public SlidingUpPanelLayout mSlidingUpPane;
    public TextView mPullupTitle;
    public boolean isMovingAuto = false;

    private ClusterManager<ClusterStation> mClusterManager;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mPreferencesEditor;
    private Marker mCurrentLocationMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mSlidingUpPane = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        setupGui();

        mPullupTitle = (TextView) findViewById(R.id.pullup_title);

        mPlaces = new Places(this);
        mGlobalVariables = (GlobalVariables) getApplicationContext();
        mSharedPreferences = getSharedPreferences(
                getString(R.string.shared_references_name), MODE_PRIVATE
        );
        mPreferencesEditor = mSharedPreferences.edit();

        setUpMapIfNeeded();
        setUpListeners();

        setUpClusterer();
    }

    protected void setupGui() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        float pullUpPaneHeight = getResources().getDimension(R.dimen.pullup_panel_height);
        Double paralaxOffset = (height - pullUpPaneHeight) / 2.1;
        mSlidingUpPane.setParalaxOffset(paralaxOffset.intValue());
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
    protected abstract void markerClicked(Marker marker);

    public boolean removePointOfInterest() {
        if (mPointOfInterestMarker != null) {
            mPointOfInterestMarker.remove();
            mPointOfInterestMarker = null;
            mPullupTitle.setText(null);
            mPullupTitle.invalidate();
            mPullupTitle.requestLayout();
            return true;
        } else {
            return false;
        }
    }
    public void setPointOfInterest(LatLng latLng) {
        mPointOfInterestMarker = addMarker(latLng, "...", null);
        mPullupTitle.setText("...");
    }
    private void setUpListeners() {
        GoogleMap.OnMapLongClickListener onMapLongClickListener = new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                removePointOfInterest();
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
                    isMovingAuto = true;
                }
                if (mGlobalVariables.Map.moveCameraWithLocation) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    isMovingAuto = true;
                }
                if (mCurrentLocationMarker == null) {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .alpha(0)
                            .title(getString(R.string.pullup_title_your_location))
                            .snippet("your-location");
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
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                isMovingAuto = true;
                markerClicked(marker);
                return true;
            }
        };
        mMap.setOnMarkerClickListener(onMarkerClickListener);
    }

    public void centerMap(LatLng position) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(position);
        mMap.animateCamera(cameraUpdate);
        isMovingAuto = true;
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            mapFragment.setRetainInstance(true);
            mMap = mapFragment.getMap();
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
        mClusterManager = new ClusterManager<>(this, mMap);
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

    public Marker addMarker(LatLng position, String title, String id) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(title)
                .snippet(id);
        return mMap.addMarker(markerOptions);
    }

    public void setPullupTitle(LatLng position) {
        new setPullupTitle().execute(position);
    }

    class setPullupTitle extends AsyncTask<LatLng, Void, String> {
        @Override
        protected String doInBackground(LatLng... params) {
            double lat = params[0].latitude;
            double lon = params[0].longitude;
            return mPlaces.getAddress(lat, lon);
        }

        @Override
        protected void onPostExecute(String address) {
            mPullupTitle.setText(address);
            mPointOfInterestMarker.setTitle(address);
        }
    }
}
