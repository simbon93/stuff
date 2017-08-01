package simone.bonvicini.travalert.travalert.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import simone.bonvicini.travalert.travalert.R;
import simone.bonvicini.travalert.travalert.alarm.AlarmController;
import simone.bonvicini.travalert.travalert.alarm.AlarmScheduler;
import simone.bonvicini.travalert.travalert.helper.MapHelper;
import simone.bonvicini.travalert.travalert.helper.MetricsHelper;
import simone.bonvicini.travalert.travalert.model.LocationAlarm;
import simone.bonvicini.travalert.travalert.services.DataService;
import simone.bonvicini.travalert.travalert.services.LocationService;
import simone.bonvicini.travalert.travalert.ui.adapter.TripAdapter;
import simone.bonvicini.travalert.travalert.ui.fragments.AlarmAreaFragment;
import simone.bonvicini.travalert.travalert.ui.fragments.AlarmFragment;
import simone.bonvicini.travalert.travalert.ui.fragments.SuccessFragment;

import static simone.bonvicini.travalert.travalert.services.LocationService.EXTRA_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerDragListener {

    public final String TAG = this.getClass().getSimpleName();

    public static final double NESTED_VIEW_PERCENTAGE = 0.65;

    private FloatingActionButton mFab;

    private LatLng mUserLocation;

    private TripAdapter mAdapter;

    @Override
    public void onMarkerDragStart(Marker marker) {

        handleMarkerDrag(marker);
    }

    @Override
    public void onMarkerDrag(Marker marker) {

        handleMarkerDrag(marker);
    }

    private void handleMarkerDrag(Marker marker) {

        LatLng dragPosition = marker.getPosition();
        Location dragLocation = new Location("Test");
        dragLocation.setLatitude(dragPosition.latitude);
        dragLocation.setLongitude(dragPosition.longitude);
        dragLocation.setTime(new Date().getTime());
        updateLocation(dragLocation);
        drawCircle(mSelectedLocationAlarm.getRadius());
    }

    public LatLng getUserLocation() {

        return mUserLocation;
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    public enum MapView {
        TRIPS, AREA, ALARM, SUCCESS
    }

    private GoogleMap mMap;

    private GoogleApiClient mClient;

    private LocationRequest mLocationRequest;

    private MapView mCurrentView = MapView.TRIPS;

    private BottomSheetBehavior mBottomSheetBehavior;

    private View mOptionsContainer;

    private RelativeLayout mMapContainer;

    private LocationAlarm mSelectedLocationAlarm;

    private Circle mLocationArea;

    private boolean mZoomOnUser = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initUi();
    }

    public LocationAlarm getSelectedLocationAlarm() {

        return mSelectedLocationAlarm;
    }

    private void setUpList(/*List<Address> addresses*/) {

        List<LocationAlarm> addresses = DataService.get(this).loadFavoriteLocations();

        if (addresses == null) {
            return;
        }

        mAdapter = new TripAdapter(this, addresses, new TripAdapter.OnTripClickListener() {
            @Override
            public void onTripSelected(LocationAlarm locationAlarm) {

                mSelectedLocationAlarm = locationAlarm;
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                computeViewFlow(true, null);
                drawCircle(mSelectedLocationAlarm.getRadius());
            }

            @Override
            public void onTripDeleted(LocationAlarm place) {

                mAdapter.setAlarms(DataService.get(MapsActivity.this).removeFavoriteLocation(place));
                fillTripList(mAdapter);
            }
        });

        fillTripList(mAdapter);
    }

    private void initUi() {

        mFab = (FloatingActionButton) findViewById(R.id.saved_trips_button);
        mOptionsContainer = findViewById(R.id.options_container);
        mMapContainer = (RelativeLayout) findViewById(R.id.map_container);

        setUpPlaceAutocompleteFragment();
        setUpMap();
        setUpNestedScrollView();
        setUpList();
    }

    private void fillTripList(TripAdapter adapter) {

        LinearLayout container = (LinearLayout) findViewById(R.id.listview);

        container.removeAllViews();

        for (int i = 0; i < adapter.getCount(); i++) {

            container.addView(adapter.getView(i, null, container));
        }
    }

    private void setUpNestedScrollView() {

        View bottomSheet = findViewById(R.id.bottom_sheet);
        int heightPixels = getResources().getDisplayMetrics().heightPixels;
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
        layoutParams.height = (int) (heightPixels * NESTED_VIEW_PERCENTAGE);
        bottomSheet.setLayoutParams(layoutParams);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    private void fragmentCarousel() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mCurrentView.ordinal() < MapView.values().length - 1) {

                    Toast.makeText(MapsActivity.this, String.valueOf(mCurrentView.ordinal()), Toast.LENGTH_SHORT).show();
                    pushFragment(mCurrentView, null, true);
                    mCurrentView = MapView.values()[mCurrentView.ordinal() + 1];
                    fragmentCarousel();
                }
            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {

        computeViewFlow(false, null);
    }

    public void computeViewFlow(boolean ahead, Bundle bundle) {

        if (ahead) {
            if (mCurrentView.ordinal() == 0) {
                mFab.setVisibility(View.GONE);
                // changeContainerWeight(false);
            }

            if (mCurrentView.ordinal() < MapView.values().length - 1) {

                pushFragment(MapView.values()[mCurrentView.ordinal() + 1], bundle, true);
            }

        } else {

            if (mCurrentView.ordinal() == 0 && mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else if (mCurrentView.ordinal() == 0 && mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                super.onBackPressed();
            } else if (mCurrentView == MapView.AREA) {
                mSelectedLocationAlarm = null;
                mCurrentView = MapView.TRIPS;
                mFab.setVisibility(View.VISIBLE);
                mOptionsContainer.setVisibility(View.GONE);
                mMap.clear();
                mLocationArea.remove();
                // changeContainerWeight(true);
            } else {
                pushFragment(MapView.values()[mCurrentView.ordinal() - 1], bundle, false);
            }
        }
        int options = (int) ((LinearLayout.LayoutParams) findViewById(R.id.options_container).getLayoutParams()).weight;
        int map = (int) ((LinearLayout.LayoutParams) findViewById(R.id.map_container).getLayoutParams()).weight;

        Log.d(TAG, "options weight: " + options);
        Log.d(TAG, "map weight: " + map);
    }

    private void pushFragment(MapView mapView, Bundle bundle, boolean ahead) {

        mOptionsContainer.setVisibility(View.VISIBLE);

        Fragment fragment = null;

        mCurrentView = mapView;

        switch (mapView) {

            case AREA:
                fragment = new AlarmAreaFragment();
                break;
            case ALARM:
                fragment = new AlarmFragment();
                break;
            case SUCCESS:
                fragment = new SuccessFragment();
                break;
        }

        if (fragment == null) {
            return;
        }

        if (bundle != null) {
            fragment.setArguments(bundle);
        }

        Log.d(TAG, "Switched to fragment: " + mCurrentView.name());

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (ahead) {
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
        } else {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left);
        }
        transaction.replace(R.id.container, fragment, null);
        if (!isDestroyed() && !isFinishing()) {
            transaction.commitAllowingStateLoss();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                MetricsHelper.setMetricsListener(findViewById(R.id.container), new MetricsHelper.OnMetricsListener() {
                    @Override
                    public void onMeasured(View v, int height, int width) {

                        FrameLayout container = (FrameLayout) findViewById(R.id.container);
                        ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
                        layoutParams.height = height;
                        container.setLayoutParams(layoutParams);
                    }
                });
            }
        }, 300);
    }

    public void updateLocation(Location location) {

        mSelectedLocationAlarm.setLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    public void updateTime(Calendar time) {

        mSelectedLocationAlarm.setEmergencyAlarm(time);
    }

    public void updateRadius(int radius) {

        mSelectedLocationAlarm.setRadius(radius);
    }

    @Override
    protected void onPause() {

        super.onPause();
        if (mClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mClient, this);
        }
    }

    //MAP STUFF

    private void setUpPlaceAutocompleteFragment() {

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("IT")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS | AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteFragment.setFilter(typeFilter);
        ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setHintTextColor(ContextCompat.getColor(this, R.color.textColor));
        ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                if (mSelectedLocationAlarm != null) {
                    mSelectedLocationAlarm = new LocationAlarm(place.getLatLng(), place.getName().toString());
                    if (mCurrentView != MapView.AREA) {
                        pushFragment(MapView.AREA, null, true);
                        mFab.setVisibility(View.GONE);
                    }
                } else {
                    mSelectedLocationAlarm = new LocationAlarm(place.getLatLng(), place.getName().toString());
                    computeViewFlow(true, null);
                }

                addSelectedPositionMarker();
//                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).draggable(true));
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
//                drawCircle(mSelectedLocationAlarm.getRadius());
                Log.i(TAG, "Place: " + place.getName());//get place details here
            }

            @Override
            public void onError(Status status) {

                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private void setUpMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void buildGoogleApiClient() {

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setIndoorEnabled(false);
        mMap.setOnMarkerDragListener(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            }
        } else {
            buildGoogleApiClient();
        }

        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_map));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                if (mSelectedLocationAlarm != null) {
                    mSelectedLocationAlarm = new LocationAlarm(latLng, latLng.toString());
                    if (mCurrentView != MapView.AREA) {
                        pushFragment(MapView.AREA, null, true);
                        mFab.setVisibility(View.GONE);
                    }
                } else {
                    mSelectedLocationAlarm = new LocationAlarm(latLng, latLng.toString());
                    computeViewFlow(true, null);
                }
                mMap.clear();
//                if (mUserLocation != null) {
//                    BitmapDescriptor icon = MapHelper.getMapIcon(R.drawable.ic_person_pin_circle, MapsActivity.this);
//                    mMap.addMarker(new MarkerOptions().position(mUserLocation).icon(icon));
//                }
                addSelectedPositionMarker();
                Log.i(TAG, "Place: " + latLng.toString());//get place details here
            }
        });
    }

    public void drawCircle(int radius) {

        CircleOptions circleOptions = MapHelper.getCircleOptions(radius, mSelectedLocationAlarm.getLocation(), this);

        // Adding the circle to the GoogleMap
        if (mLocationArea != null) {
            mLocationArea.remove();
        }

        mLocationArea = mMap.addCircle(circleOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(circleOptions.getCenter(), MapHelper.getZoomLevel(mLocationArea)));
    }

    public void addToFavorite(LocationAlarm locationAlarm) {

        mSelectedLocationAlarm = locationAlarm;
        DataService service = DataService.get(this);
        service.addFavoriteLocation(mSelectedLocationAlarm);
    }

    public void setAlarm() {

        DataService service = DataService.get(this);
        service.storeLocation(mSelectedLocationAlarm);

        Intent intent = new Intent(this, LocationService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_LOCATION, mSelectedLocationAlarm);
        intent.putExtras(bundle);
        startService(intent);

        AlarmScheduler.get().setAlarm(mSelectedLocationAlarm);
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {

        mUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        BitmapDescriptor icon = MapHelper.getMapIcon(R.drawable.ic_person_pin_circle, this);
        mMap.addMarker(new MarkerOptions().position(mUserLocation).icon(icon));
        if (mSelectedLocationAlarm != null) {
            addSelectedPositionMarker();
        }

        //zoom on user only first time
        if (mZoomOnUser) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mUserLocation, 15));
        }
//        } else {
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(me));
//        }
        mZoomOnUser = false;
    }

    public void addSelectedPositionMarker() {

        BitmapDescriptor icon = MapHelper.getMapIcon(R.drawable.ic_location, this);
        mMap.addMarker(new MarkerOptions().position(mSelectedLocationAlarm.getLocation()).icon(icon));
        drawCircle(mSelectedLocationAlarm.getRadius());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mClient, mLocationRequest, this);
        }
        AlarmController.get().setGpsDisabled(false);
    }

    @Override
    public void onConnectionSuspended(int i) {
        AlarmController.get().setGpsDisabled(true);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        AlarmController.get().setGpsDisabled(true);
    }
}
