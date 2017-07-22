package simone.bonvicini.travalert.travalert.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import simone.bonvicini.travalert.travalert.model.LocationAlarm;

public class LocationService extends Service {

    public static final String EXTRA_LOCATION = "EXTRA_LOCATION";

    private final String TAG = this.getClass().getSimpleName() + "Travalert";

    private LocationManager mLocationManager = null;

    private LocationAlarm mLocation;

    private static final int LOCATION_INTERVAL = 1000;

    private static final float LOCATION_DISTANCE = 10f;

    private class LocationListener implements android.location.LocationListener {

        Location mLastLocation;

        public LocationListener(String provider) {

            Log.d(TAG, "Location listener uses " + provider + " provider");
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {

            Log.d(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);

            if (mLocation != null) {

                Location centralLocation = new Location("");
                centralLocation.setLatitude(mLocation.getLocation().latitude);
                centralLocation.setLongitude(mLocation.getLocation().longitude);

                float distance = mLastLocation.distanceTo(centralLocation);

                if (distance <= mLocation.getRadius()) {

                    Log.d(TAG, "OnTargetReached: " + true);

                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(2000);
//                    String notificationMessage = mCentralPoint.getTitle() == null ? getString(R.string.notification_message) : mCentralPoint.getTitle();
//                    NotificationHelper.showNotification(JecoLocationService.this, null, notificationMessage);
//
                } else {

                    Log.d(TAG, "OnTargetReached: " + false);
                }

            }
        }

        @Override
        public void onProviderDisabled(String provider) {

            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {

            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationService.LocationListener[] mLocationListeners = new LocationService.LocationListener[]{
            new LocationService.LocationListener(LocationManager.GPS_PROVIDER),
            new LocationService.LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "Location service started");
        super.onStartCommand(intent, flags, startId);

        if (intent != null) {

            Bundle bundle = intent.getExtras();
            mLocation = (LocationAlarm) bundle.getSerializable(EXTRA_LOCATION);
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {

        Log.d(TAG, "Location service created");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {

        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.d(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {

        Log.d(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
