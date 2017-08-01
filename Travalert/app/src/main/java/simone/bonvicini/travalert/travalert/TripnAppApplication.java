package simone.bonvicini.travalert.travalert;

import android.app.Application;

import simone.bonvicini.travalert.travalert.alarm.AlarmController;
import simone.bonvicini.travalert.travalert.alarm.AlarmScheduler;

/**
 * Created by simone on 22/07/2017.
 */

public class TripnAppApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
        AlarmController.init(getApplicationContext());
        AlarmScheduler.init(getApplicationContext());
    }
}
