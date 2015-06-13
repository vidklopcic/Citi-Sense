package eu.citi_sense.vic.citi_sense;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import eu.citi_sense.vic.citi_sense.global.Databases.FavoritePlace;
import eu.citi_sense.vic.citi_sense.global.Databases.Station;
import eu.citi_sense.vic.citi_sense.global.GlobalVariables;
import eu.citi_sense.vic.citi_sense.global.MapVariables;
import eu.citi_sense.vic.citi_sense.global.Pollutants;
import eu.citi_sense.vic.citi_sense.support_classes.Charts;
import eu.citi_sense.vic.citi_sense.support_classes.general_widgets.SearchFragment;
import eu.citi_sense.vic.citi_sense.support_classes.map_activity.ActionBarFragment;
import eu.citi_sense.vic.citi_sense.support_classes.map_activity.ClusterStation;
import eu.citi_sense.vic.citi_sense.support_classes.map_activity.LocalTileProvider;
import eu.citi_sense.vic.citi_sense.support_classes.map_activity.Places;
import eu.citi_sense.vic.citi_sense.support_classes.sliding_menu.SlidingMenuHandler;
import eu.citi_sense.vic.citi_sense.support_classes.sliding_menu.SlidingMenuListeners;
import eu.citi_sense.vic.citi_sense.support_classes.sliding_up_pane.SlidingUpPaneCollapsedFragment;
import eu.citi_sense.vic.citi_sense.support_classes.sliding_up_pane.SlidingUpPaneExpandedFragment;

public abstract class MapBaseActivity extends FragmentActivity implements ActionBarFragment.MenuClickInterface {
    public TileOverlay mTileOverlay;
    public LocalTileProvider mTileProvider;
    public GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public GlobalVariables mGVar;
    public Marker mPointOfInterestMarker = null;
    public Places mPlaces;
    public SlidingUpPanelLayout mSlidingUpPane;
    public boolean isMovingAuto = false;
    public FloatingActionMenu mFABPollutants;
    public FloatingActionButton mFABFavorites;
    public SlidingMenuHandler mSlidingMenu;
    public boolean alreadyRegisteredPaneChange = false;
    public ActionBarFragment mActionBarFragment;
    public SearchFragment mSearchFragment;
    public int animationDuration;
    private Animation mMapUpAnimation;
    private Integer mapOffset;
    private Animation mMapDownAnimation;
    private ClusterManager<ClusterStation> mClusterManager;
    private boolean mapIsUp = false;
    private FrameLayout mMapWrapper;
    private Marker mCurrentLocationMarker = null;
    private Charts mCharts = new Charts();
    private FragmentManager mFragmentManager;
    private SlidingUpPaneCollapsedFragment mSlidingPaneCollapsedFragment;
    private SlidingUpPaneExpandedFragment mSlidingPaneExpandedFragment;
    private FloatingActionButton mFABAnalysis;
    private Context mContext;
    private RelativeLayout mSlidingPaneLayout;
    private RelativeLayout mSpacer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        mFragmentManager = getFragmentManager();
        mSlidingPaneCollapsedFragment = new SlidingUpPaneCollapsedFragment();
        mSlidingPaneExpandedFragment = new SlidingUpPaneExpandedFragment();
        mSlidingPaneLayout = (RelativeLayout) findViewById(R.id.sliding_pane_layout);
        mGVar = (GlobalVariables) getApplicationContext();
        animationDuration = MapVariables.animationDuration;
        mapOffset = Float.valueOf(getResources().getDimension(R.dimen.pullup_panel_height)/2).intValue();
        mapOffset -= getPx(55)/2;
        mFABPollutants = (FloatingActionMenu) findViewById(R.id.menu_pollutant);
        mFABFavorites = (FloatingActionButton) findViewById(R.id.favorites_fab);
        mFABAnalysis = (FloatingActionButton) findViewById(R.id.fab_analysis);
        mFABAnalysis.hide(false);
        mActionBarFragment = (ActionBarFragment) getFragmentManager().findFragmentById(R.id.map_action_bar_fragment);
        mSearchFragment = (SearchFragment) getFragmentManager().findFragmentById(R.id.map_search_fragment);
        mSlidingUpPane = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mMapWrapper = (FrameLayout) findViewById(R.id.map_fragment_wrapper);
        setupGui();

        mPlaces = new Places(this);
        setUpMapIfNeeded();
        setUpListeners();

        setUpClusterer();

        initFAB();

        LineData data = mGVar.data.getAQIData(10, 5);
        LineChart chart = (LineChart) findViewById(R.id.sliding_menu_chart);
        mCharts.setupAQISlidingChart(data, chart);

        LinearLayout mapButton = (LinearLayout) findViewById(R.id.sliding_menu_map);
        LinearLayout analysisButton = (LinearLayout) findViewById(R.id.sliding_menu_analysis);
        LinearLayout stationsButton = (LinearLayout) findViewById(R.id.sliding_menu_stations);
        new SlidingMenuListeners(mapButton, analysisButton, stationsButton,
                SlidingMenuListeners.MAP_ACTIVITY, mSlidingMenu, this);
        mContext = this;
        mActionBarFragment.hideMenu(false);
    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mSlidingUpPane.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    public void menuClicked() {
        mSlidingMenu.menu.showMenu();
    }
    protected void setupGui() {
        mSlidingUpPane.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                if (!alreadyRegisteredPaneChange) {
                    mFABAnalysis.hide(true);
                }
            }

            @Override
            public void onPanelCollapsed(View view) {
                if (!alreadyRegisteredPaneChange) {
                    hideFab();
                    mFABAnalysis.show(true);
                    mFABFavorites.hide(true);
                }
                alreadyRegisteredPaneChange = false;
                TypedValue tv = new TypedValue();
                mActionBarFragment.setTitleNormal();
                if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                    int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
                    mSlidingPaneLayout.getLayoutParams().height = mSlidingMenu.menu.getHeight()-actionBarHeight;
                    mSlidingPaneLayout.requestLayout();
                }
            }

            @Override
            public void onPanelExpanded(View view) {
                if (!alreadyRegisteredPaneChange) {
                    mFABAnalysis.hide(true);
                    mFABPollutants.hideMenuButton(true);
                    mActionBarFragment.setTitleFavorites();
                }
                alreadyRegisteredPaneChange = false;
            }

            @Override
            public void onPanelAnchored(View view) {
            }

            @Override
            public void onPanelHidden(View view) {
                if (!alreadyRegisteredPaneChange) {
                    showFab();
                    mFABAnalysis.hide(true);
                }
                mFABFavorites.show(true);
                mActionBarFragment.setTitleNormal();
                alreadyRegisteredPaneChange = false;
            }
        });

        mSlidingMenu = new SlidingMenuHandler(this);

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.sliding_pane_layout, mSlidingPaneCollapsedFragment);
        ft.commit();
        mFragmentManager.executePendingTransactions();
    }

    public int getPx(float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (mGVar.mMap.centerOnResume != null) {
            if (mGVar.mMap.centerOnResume == mGVar.mMap.location) {
                markerClicked(mCurrentLocationMarker);
            } else {
                mapLongClicked(mGVar.mMap.centerOnResume);
            }
        }
        mGVar.mMap.centerOnResume = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGVar.mMap.cameraPosition = mMap.getCameraPosition();
    }

    public void setPaneExpanded() {
        alreadyRegisteredPaneChange = true;
        mSlidingUpPane.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        mFABAnalysis.hide(true);
        mFABPollutants.hideMenuButton(true);
    }

    public void setPaneHidden() {
        alreadyRegisteredPaneChange = true;
        mSlidingUpPane.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        mFABAnalysis.hide(true);
        showFab();
    }

    public void setPaneCollapsed() {
        alreadyRegisteredPaneChange = true;
        mSlidingUpPane.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        mFABAnalysis.show(true);
        mFABFavorites.hide(true);
        hideFab();
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
            mActionBarFragment.setTitle(null);
            return true;
        } else {
            return false;
        }
    }
    public Marker setPointOfInterest(LatLng latLng) {
        mPointOfInterestMarker = addMarker(latLng, "...", null);
        return mPointOfInterestMarker;
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
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 14));
        }
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
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
                if (mGVar.mMap.location == null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                    markerClicked(mCurrentLocationMarker);
                    isMovingAuto = true;
                }
                mCurrentLocationMarker.setPosition(latLng);
                mGVar.mMap.location = latLng;
                locationChanged(location);

                mActionBarFragment.setModeSwitchedListener(new ActionBarFragment.ModeSwitchedListener() {
                    @Override
                    public void onChange(boolean isInFavoritesMode) {
                        if (isInFavoritesMode) {
                            FragmentTransaction ft = mFragmentManager.beginTransaction();
                            ft.replace(R.id.sliding_pane_layout, mSlidingPaneExpandedFragment);
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            ft.commit();
                            mFragmentManager.executePendingTransactions();
                        } else {
                            FragmentTransaction ft = mFragmentManager.beginTransaction();
                            ft.replace(R.id.sliding_pane_layout, mSlidingPaneCollapsedFragment);
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            ft.commit();
                            mFragmentManager.executePendingTransactions();
                        }
                    }
                });
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
                mClusterManager.onCameraChange(cameraPosition);
            }
        };
        mMap.setOnCameraChangeListener(onCameraChangeListener);

        GoogleMap.OnMarkerClickListener onMarkerClickListener = new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                isMovingAuto = true;
                markerClicked(marker);
                mClusterManager.onMarkerClick(marker);
                return true;
            }
        };
        mMap.setOnMarkerClickListener(onMarkerClickListener);

        mFABAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AnalysisActivity.class);
                intent.putExtra("lat", mActionBarFragment.getLocation().latitude);
                intent.putExtra("lng", mActionBarFragment.getLocation().longitude);
                intent.putExtra("name", mActionBarFragment.getTitle());
                mContext.startActivity(intent);
            }
        });

        mFABFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPaneExpanded();
                mFABAnalysis.hide(true);
                mFABFavorites.hide(true);
                mActionBarFragment.setTitleFavorites();
                mActionBarFragment.showMenu();
            }
        });

        mActionBarFragment.setFavoritePlacesListener(new ActionBarFragment.FavoritePlacesListener() {
            @Override
            public void onAdd(FavoritePlace place) {
                mSlidingPaneExpandedFragment.addFavoritePlace(place);
            }

            @Override
            public void onRemove(FavoritePlace place) {
                mSlidingPaneExpandedFragment.removeFavoritePlace(place);
            }
        });
    }

    public void centerMap(LatLng position) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(position);
        mMap.animateCamera(cameraUpdate);
        isMovingAuto = true;
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
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
        mTileProvider = new LocalTileProvider(getResources().getAssets());
        mTileOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mTileProvider));
        if (mGVar.mMap.cameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mGVar.mMap.cameraPosition));
        }
    }

    private void setUpClusterer() {
        mClusterManager = new ClusterManager<>(this, mMap);
    }

    private void updateStations(ArrayList<Station> stations) {
        mClusterManager.clearItems();
        for(Station station : stations) {
            LatLng latLng = station.getPosition();
            mClusterManager.addItem(new ClusterStation(latLng, station.owner, station));
       }
    }

    public Marker addMarker(LatLng position, String title, String id) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(title)
                .snippet(id);
        return mMap.addMarker(markerOptions);
    }

    public void setActionBarTitle(Marker marker) {
        mActionBarFragment.setTitle(null);
        new setPullupTitle(marker).execute();
    }

    class setPullupTitle extends AsyncTask<Void, Void, String> {
        Marker mMarker;
        LatLng position;

        public setPullupTitle(Marker marker) {
            position = marker.getPosition();
            mMarker = marker;
        }

        @Override
        protected String doInBackground(Void... params) {
            double lat = position.latitude;
            double lon = position.longitude;
            return mPlaces.getAddress(lat, lon);
        }

        @Override
        protected void onPostExecute(String address) {
            try {
                mMarker.setTitle(address);
                mActionBarFragment.setTitle(address, mMarker.getPosition());
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
        mFABPollutants.setMenuButtonColorNormal(mGVar.Pollutant.color);
        mFABPollutants.setMenuButtonColorPressed(mGVar.Pollutant.color_pressed);
        mTileProvider.setCurrentPollutant(Pollutants.pollutantNames.get(mGVar.Pollutant.current));
        mTileOverlay.remove();
        mTileOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mTileProvider));

        for(int i=1; i<=mGVar.Pollutant.nOfPollutants; i++) {
            FloatingActionButton fab = new FloatingActionButton(this);
            Pollutants p = mGVar.Pollutant.getPollutant(i);
            fab.setImageDrawable(getResources().getDrawable(p.icon));
            fab.setButtonSize(FloatingActionButton.SIZE_MINI);
            fab.setLabelText(getString(p.description) + ": " + p.aqi.toString());
            Animation animation = AnimationUtils.loadAnimation(
                    getApplicationContext(), R.anim.abc_shrink_fade_out_from_bottom);
            fab.setHideAnimation(animation);
            fab.setTag(i);
            fab.setColorNormal(p.color);
            mSlidingPaneCollapsedFragment.addPollutant(p);
            fab.setOnClickListener(new FloatingActionButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer pollutant = (int) view.getTag();
                    mGVar.data.setLastFABPollutant(pollutant);
                    mGVar.Pollutant.setPollutant(pollutant);
                    mFABPollutants.setMenuButtonColorNormal(mGVar.Pollutant.color);
                    mFABPollutants.setMenuButtonColorPressed(mGVar.Pollutant.color_pressed);
                    mTileProvider.setCurrentPollutant(Pollutants.pollutantNames.get(mGVar.Pollutant.current));
                    mTileOverlay.remove();
                    mTileOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mTileProvider));
                    mFABPollutants.close(true);
                }
            });
            fab.setColorPressed(p.color_pressed);
            mFABPollutants.addMenuButton(fab);
        }
        mFABPollutants.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean b) {
                if (b) {
                    mFABFavorites.hide(true);
                } else {
                    mFABFavorites.show(true);
                }
            }
        });
        createCustomAnimation();
        updateStations(mGVar.Pollutant.stations);
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

        mMapDownAnimation = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mMapWrapper.getLayoutParams();
                int calculatedOffset = (int)(mapOffset-mapOffset*interpolatedTime);
                params.bottomMargin = calculatedOffset;
                params.topMargin = -calculatedOffset;
                mMapWrapper.setLayoutParams(params);
            }
        };
        mMapDownAnimation.setDuration(animationDuration); // in ms

        mMapUpAnimation = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mMapWrapper.getLayoutParams();
                int calculatedOffset = (int) (mapOffset * interpolatedTime);
                params.bottomMargin = calculatedOffset;
                params.topMargin = -calculatedOffset;
                mMapWrapper.setLayoutParams(params);
            }
        };
        mMapUpAnimation.setDuration(animationDuration); // in ms
    }

    public void animateMapDown() {
        if (mapIsUp) {
            mMapWrapper.startAnimation(mMapDownAnimation);
            mapIsUp = false;
        }
    }

    public void animateMapUp() {
        if (!mapIsUp) {
            mMapWrapper.startAnimation(mMapUpAnimation);
            mapIsUp = true;
        }
    }

    public void showFab() {
        mFABPollutants.showMenuButton(true);
        mActionBarFragment.hideMenu(true);
        mMapWrapper.startAnimation(mMapDownAnimation);
        animateMapDown();
    }

    public void hideFab() {
        mFABPollutants.hideMenuButton(true);
        mActionBarFragment.showMenu();
        animateMapUp();
    }
}