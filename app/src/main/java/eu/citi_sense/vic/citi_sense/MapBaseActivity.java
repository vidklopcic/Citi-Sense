package eu.citi_sense.vic.citi_sense;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
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
import eu.citi_sense.vic.citi_sense.support_classes.Charts;
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
    public FloatingActionMenu mFABPollutants;
    public SlidingMenuHandler mSlidingMenu;
    public boolean alreadyAnimatedFAB = false;
    private Animation FABDownAnimation;
    private Animation FABUpAnimation;
    private ClusterManager<ClusterStation> mClusterManager;
    private SharedPreferences mSharedPreferences;
    private Marker mCurrentLocationMarker = null;
    private Charts mCharts = new Charts();
    private RelativeLayout mSlidinPaneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mGVar = (GlobalVariables) getApplicationContext();
        mSharedPreferences = getSharedPreferences(
                getString(R.string.shared_references_name), MODE_PRIVATE
        );
        loadSettings();
        mSlidinPaneLayout = (RelativeLayout) findViewById(R.id.sliding_pane_layout);
        mFABPollutants = (FloatingActionMenu) findViewById(R.id.menu_pollutant);
        mSlidingUpPane = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        setupGui();

        mPullupTitle = (TextView) findViewById(R.id.pullup_title);

        mPlaces = new Places(this);
        setUpMapIfNeeded();
        setUpListeners();

        setUpClusterer();

        initFAB();

        LineData data = mGVar.data.getAQIData(24, 5);
        LineChart chart = (LineChart) findViewById(R.id.sliding_menu_chart);
        mCharts.setupAQISlidingChart(data, chart);
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
        Double paralaxOffset = (height-height*0.65) / 2 + pullUpPaneHeight/2;
        mSlidingUpPane.setParalaxOffset(paralaxOffset.intValue());
        mSlidingUpPane.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
            }

            @Override
            public void onPanelCollapsed(View view) {
                if (!alreadyAnimatedFAB) {
                    animateFABUp();
                }
                alreadyAnimatedFAB = false;
            }


            @Override
            public void onPanelExpanded(View view) {
                if (!alreadyAnimatedFAB) {
                    animateFABDown();
                }
                alreadyAnimatedFAB = false;
            }

            @Override
            public void onPanelAnchored(View view) {}

            @Override
            public void onPanelHidden(View view) {
                if (!alreadyAnimatedFAB) {
                    animateFABDown();
                }
                alreadyAnimatedFAB = false;
            }
        });
        mSlidinPaneLayout.getLayoutParams().height = (int) (height*0.65);
        mSlidingMenu = new SlidingMenuHandler(this);
    }

    private int getPx(float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
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
            try {
                mPullupTitle.setText(address);
                mPointOfInterestMarker.setTitle(address);
            } catch (NullPointerException ignored) {}
        }
    }

//    floating action button
    private void initFAB() {
//        restore last used pollutant
        Integer pollutant = mGVar.data.getLastFABPollutant();
        mGVar.Pollutant.setPollutant(pollutant);
        ImageView menu_icon = mFABPollutants.getMenuIconView();
        menu_icon.setImageResource(mGVar.Pollutant.icon);
        mFABPollutants.setMenuButtonColorNormalResId(mGVar.Pollutant.color);
        mFABPollutants.setMenuButtonColorPressedResId(mGVar.Pollutant.color_pressed);

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
            fab.setColorNormalResId(p.color);
            fab.setOnClickListener(new FloatingActionButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer pollutant = (int) view.getTag();
                    mGVar.data.setLastFABPollutant(pollutant);
                    mGVar.Pollutant.setPollutant(pollutant);
                    mFABPollutants.setMenuButtonColorNormalResId(mGVar.Pollutant.color);
                    mFABPollutants.setMenuButtonColorPressedResId(mGVar.Pollutant.color_pressed);
                    mFABPollutants.close(true);
                }
            });
            fab.setColorPressedResId(p.color_pressed);
            mFABPollutants.addMenuButton(fab);
        }
        createCustomAnimation();
    }

    private void createCustomAnimation() {
//        FAB image
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(mFABPollutants.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(mFABPollutants.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(mFABPollutants.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(mFABPollutants.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFABPollutants.getMenuIconView().setImageResource(mFABPollutants.isOpened()
                        ? R.drawable.ic_close : mGVar.Pollutant.icon);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        mFABPollutants.setIconToggleAnimatorSet(set);

//        FAB margin
        final int margin = getPx(40);
        FABDownAnimation = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFABPollutants.getLayoutParams();
                params.bottomMargin = (int)(margin - margin*interpolatedTime);
                mFABPollutants.setLayoutParams(params);
            }
        };
        FABDownAnimation.setDuration(100); // in ms

        FABUpAnimation = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFABPollutants.getLayoutParams();
                params.bottomMargin = (int)(margin * interpolatedTime);
                mFABPollutants.setLayoutParams(params);
            }
        };
        FABUpAnimation.setDuration(100); // in ms


    }

    public void animateFABDown() {
        mFABPollutants.startAnimation(FABDownAnimation);
    }

    public void animateFABUp() {
        mFABPollutants.startAnimation(FABUpAnimation);
    }
}