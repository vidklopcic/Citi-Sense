package eu.citi_sense.vic.citi_sense;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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
import eu.citi_sense.vic.citi_sense.global.Pollutants;
import eu.citi_sense.vic.citi_sense.support_classes.map_activity.ClusterStation;
import eu.citi_sense.vic.citi_sense.support_classes.map_activity.Places;
import eu.citi_sense.vic.citi_sense.support_classes.sliding_menu.SlidingMenuHandler;

public abstract class MapBaseActivity extends FragmentActivity {
    public GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public GlobalVariables mGVar;
    public Marker mPointOfInterestMarker = null;
    public Places mPlaces;
    public SlidingUpPanelLayout mSlidingUpPane;
    public TextView mPullupTitle;
    public boolean isMovingAuto = false;
    public FloatingActionMenu mMenuPollutant;
    public SlidingMenuHandler mSlidingMenu;

    private ClusterManager<ClusterStation> mClusterManager;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mPreferencesEditor;
    private Marker mCurrentLocationMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mGVar = (GlobalVariables) getApplicationContext();
        mSharedPreferences = getSharedPreferences(
                getString(R.string.shared_references_name), MODE_PRIVATE
        );
        mPreferencesEditor = mSharedPreferences.edit();
        loadSettings();
        mMenuPollutant = (FloatingActionMenu) findViewById(R.id.menu_pollutant);
        mSlidingUpPane = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        setupGui();

        mPullupTitle = (TextView) findViewById(R.id.pullup_title);

        mPlaces = new Places(this);
        setUpMapIfNeeded();
        setUpListeners();

        setUpClusterer();

        initFAB();

        setupChart();
    }

    protected void loadSettings() {
        int currentPollutant = mSharedPreferences.getInt(
                mGVar.Keys.last_pollutant, mGVar.Pollutant.CO);
        mGVar.Pollutant.setPollutant(currentPollutant);
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
        mSlidingMenu = new SlidingMenuHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGVar.mMap.cameraPosition = mMap.getCameraPosition();
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
                if (mGVar.mMap.location == null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                    isMovingAuto = true;
                }
                if (mGVar.mMap.moveCameraWithLocation) {
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
                mGVar.mMap.location = latLng;
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

        if (mGVar.mMap.cameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mGVar.mMap.cameraPosition));
        }
    }

    private void setUpClusterer() {
        mClusterManager = new ClusterManager<>(this, mMap);
        updateStations();
    }

    private void updateStations() {
        mClusterManager.clearItems();
        if (mGVar.mMap.stations != null) {
           for(Map.Entry<String, double[]> station: mGVar.mMap.stations.entrySet()) {
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

//    floating action button
    private void initFAB() {
        ImageView menu_icon = mMenuPollutant.getMenuIconView();
        menu_icon.setImageResource(mGVar.Pollutant.icon);
        mMenuPollutant.setMenuButtonColorNormalResId(mGVar.Pollutant.color);
        mMenuPollutant.setMenuButtonColorPressedResId(mGVar.Pollutant.color_pressed);
        for(int i=1; i<=mGVar.Pollutant.nOfPollutants; i++) {
            FloatingActionButton fab = new FloatingActionButton(this);
            Pollutants p = mGVar.Pollutant.getPollutant(i);
            fab.setImageDrawable(getResources().getDrawable(p.icon));
            fab.setButtonSize(FloatingActionButton.SIZE_MINI);
            fab.setLabelText(getString(p.description));
            Animation animation = AnimationUtils.loadAnimation(
                    getApplicationContext(), R.anim.abc_shrink_fade_out_from_bottom);
            fab.setHideAnimation(animation);
            fab.setTag(i);
            fab.setBackgroundColor(p.color);
            fab.setOnClickListener(new FloatingActionButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGVar.Pollutant.setPollutant((int) view.getTag());
                    mMenuPollutant.setMenuButtonColorNormalResId(mGVar.Pollutant.color);
                    mMenuPollutant.setMenuButtonColorPressedResId(mGVar.Pollutant.color_pressed);
                    mMenuPollutant.close(true);
                }
            });
            fab.setColorNormalResId(p.color);
            fab.setColorPressedResId(p.color_pressed);
            mMenuPollutant.addMenuButton(fab);
        }
        createCustomAnimation();
    }

    private void updateFAB() {
        for (int i=0; i<mMenuPollutant.getChildCount(); i++) {
            FloatingActionButton fab = (FloatingActionButton) mMenuPollutant.getChildAt(i);
            int pollutant = (int) fab.getTag();
            Pollutants p = mGVar.Pollutant.getPollutant(pollutant);
            fab.setColorNormalResId(p.color);
            fab.setColorPressedResId(p.color_pressed);
        }
    }

    private void createCustomAnimation() {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(mMenuPollutant.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(mMenuPollutant.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(mMenuPollutant.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(mMenuPollutant.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mMenuPollutant.getMenuIconView().setImageResource(mMenuPollutant.isOpened()
                        ? R.drawable.ic_close : mGVar.Pollutant.icon);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        mMenuPollutant.setIconToggleAnimatorSet(set);
    }

//    Sliding menu
private LineData getData(int count, float range) {

    ArrayList<String> xVals = new ArrayList<String>();
    for (int i = 0; i < count; i++) {
        xVals.add(Integer.toString(i % 24));
    }

    ArrayList<Entry> yVals = new ArrayList<Entry>();

    for (int i = 0; i < count; i++) {
        float val = (float) (Math.random() * range) + 3;
        yVals.add(new Entry(val, i));
    }

    // create a dataset and give it a type
    LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
    // set1.setFillAlpha(110);
    // set1.setFillColor(Color.RED);

    set1.setLineWidth(1.75f);
    set1.setCircleSize(0);
    set1.setColor(Color.WHITE);
    set1.setCircleColor(Color.WHITE);
    set1.setHighLightColor(Color.WHITE);
    set1.setDrawValues(false);

    ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
    dataSets.add(set1); // add the datasets

    // create a data object with the datasets
    LineData data = new LineData(xVals, dataSets);

    return data;
}
    private void setupChart() {
        LineData data = getData(24, 5);
        int color = Color.argb(0, 0, 0, 0);
        LineChart chart = (LineChart) findViewById(R.id.sliding_menu_chart);


        // enable / disable grid background
        chart.setDrawGridBackground(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setBackgroundColor(color);

        // set custom chart offsets (automatic offset calculation is hereby disabled)

        // add data
        chart.setData(data);
        chart.setDescription("");

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);

        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        XAxis xl = chart.getXAxis();
        xl.setEnabled(true);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTextColor(Color.WHITE);
        xl.setAxisLineColor(Color.WHITE);
        xl.setDrawGridLines(false);

        chart.getXAxis().setEnabled(true);

        // animate calls invalidate()...
        chart.invalidate();
    }
}
