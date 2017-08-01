package simone.bonvicini.travalert.travalert.ui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import simone.bonvicini.travalert.travalert.R;
import simone.bonvicini.travalert.travalert.helper.MapHelper;
import simone.bonvicini.travalert.travalert.model.LocationAlarm;
import simone.bonvicini.travalert.travalert.services.DataService;

public class AlarmMonitoringActivity extends FragmentActivity implements OnMapReadyCallback, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public final String TAG = this.getClass().getSimpleName();

    public static final double NESTED_VIEW_PERCENTAGE = 0.65;

    private LatLng mUserLocation;

    private Marker mUserMarker;

    private GoogleMap mMap;

    private GoogleApiClient mClient;

    private LocationRequest mLocationRequest;

    private LocationAlarm mSelectedLocationAlarm;

    private Circle mLocationArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_monitoring);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mSelectedLocationAlarm = DataService.get(this).loadLocation();
        initUi();
    }

    private void initUi() {

        setUpMap();
    }

    @Override
    protected void onPause() {

        super.onPause();
        if (mClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mClient, this);
        }
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
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

        addSelectedPositionMarker();
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

    @Override
    public void onLocationChanged(Location location) {

        mUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        BitmapDescriptor icon = MapHelper.getMapIcon(R.drawable.ic_person_pin_circle, this);
        mMap.addMarker(new MarkerOptions().position(mUserLocation).icon(icon));
        if (mSelectedLocationAlarm != null) {
            addSelectedPositionMarker();
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(location.getLatitude(), location.getLongitude()));
        builder.include(mSelectedLocationAlarm.getLocation());
        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));
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
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
