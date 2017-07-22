package simone.bonvicini.travalert.travalert.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by simone on 27/05/17.
 */

public class LocationAlarm implements Serializable {

    private String mDescription;

    private double mLatitude;

    private double mLongitude;

    private int mRadius = 30;

    private Calendar mEmergencyAlarm;

    public LocationAlarm(LatLng location, String description) {

        mLatitude = location.latitude;
        mLongitude = location.longitude;
        mDescription = description;
    }

    public LatLng getLocation() {

        return new LatLng(mLatitude,mLongitude);
    }

    public void setLocation(LatLng location) {

        mLatitude = location.latitude;
        mLongitude = location.longitude;
    }

    public int getRadius() {

        return mRadius;
    }

    public void setRadius(int radius) {

        mRadius = radius;
    }

    public Calendar getEmergencyAlarm() {

        return mEmergencyAlarm;
    }

    public void setEmergencyAlarm(Calendar emergencyAlarm) {

        mEmergencyAlarm = emergencyAlarm;
    }

    public String getDescription() {

        return mDescription;
    }

    public void setDescription(String description) {

        mDescription = description;
    }
}
