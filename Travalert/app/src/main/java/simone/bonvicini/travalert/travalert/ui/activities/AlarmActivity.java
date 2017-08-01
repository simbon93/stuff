package simone.bonvicini.travalert.travalert.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import simone.bonvicini.travalert.travalert.R;
import simone.bonvicini.travalert.travalert.alarm.AlarmController;
import simone.bonvicini.travalert.travalert.alarm.AlarmScheduler;
import simone.bonvicini.travalert.travalert.services.DataService;
import simone.bonvicini.travalert.travalert.services.LocationService;

public class AlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        stopService(new Intent(this, LocationService.class));
        AlarmScheduler.get().cancelAlarm();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        disableAllAlarms();
    }

    public void disableAllAlarms() {

        AlarmController.get().cancel();
        DataService.get(this).deleteLocation();
    }
}
