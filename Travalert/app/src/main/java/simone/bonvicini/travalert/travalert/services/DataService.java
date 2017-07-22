package simone.bonvicini.travalert.travalert.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import simone.bonvicini.travalert.travalert.R;
import simone.bonvicini.travalert.travalert.model.LocationAlarm;

public class DataService {

    private static final String LOCATION_ALARM = "LOCATION_ALARM";

    private static final String FAVORITE_LOCATIONS = "FAVORITE_LOCATIONS";

    private static final String PREFS_NAME = "PREFS_NAME";

    private AndroidStore mAndroidStore;

    private static DataService instance = null;

    private Context mContext;

    private SharedPreferences prefs;

//    private Preferences(Context context) {
//        // Load the shared prefs config
//    }

    public static DataService get(Context context) {

        if (instance == null) {
            instance = new DataService(context);
        }

        return instance;
    }

    private DataService(final Context context) {

        mContext = context;
        mAndroidStore = new AndroidStore(mContext);
        prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getAlarmFormat() {

        return prefs.getInt(serviceString(R.string.data_service_time_format), 0);
    }

    public void setAlarmFrequency(int value) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(serviceString(R.string.data_service_time_format), value);
        log(serviceString(R.string.data_service_time_format), String.valueOf(value));
        editor.apply();
    }

    public int getAlarmDuration() {

        return prefs.getInt(serviceString(R.string.data_service_alarm_duration), 0);
    }

    public void setAlarmDuration(int value) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(serviceString(R.string.data_service_alarm_duration), value);
        log(serviceString(R.string.data_service_alarm_duration), String.valueOf(value));
        editor.apply();
    }

    public LocationAlarm loadLocation() {

        return (LocationAlarm) mAndroidStore.loadObject(serviceString(R.string.data_service_saved_location_alarm), AndroidStore.StoreType.Persistent);
    }

    public void storeLocation(LocationAlarm location) {

        mAndroidStore.storeObject(serviceString(R.string.data_service_saved_location_alarm), location, AndroidStore.StoreType.Persistent);
    }

    public List<LocationAlarm> loadFavoriteLocations() {

        return (List<LocationAlarm>) mAndroidStore.loadObject(serviceString(R.string.data_service_favorite_alarms), AndroidStore.StoreType.Persistent);
    }

    public void addFavoriteLocation(LocationAlarm location) {

        List<LocationAlarm> favoriteLocations = loadFavoriteLocations();
        if (favoriteLocations == null) {
            favoriteLocations = new ArrayList<>();
        }
        favoriteLocations.add(location);
        mAndroidStore.storeObject(serviceString(R.string.data_service_favorite_alarms), favoriteLocations, AndroidStore.StoreType.Persistent);
    }

    public void removeFavoriteLocation(LocationAlarm location) {

        List<LocationAlarm> favoriteLocations = loadFavoriteLocations();
        if (favoriteLocations == null || favoriteLocations.isEmpty()) {
            return;
        }

        favoriteLocations.remove(location);
        mAndroidStore.storeObject(serviceString(R.string.data_service_favorite_alarms), favoriteLocations, AndroidStore.StoreType.Persistent);
    }

    private String serviceString(int resId){

        return  mContext.getString(resId);
    }

    private void log(String entity, String value) {

        Log.d("Action", entity + " changed: " + value);
    }
}
