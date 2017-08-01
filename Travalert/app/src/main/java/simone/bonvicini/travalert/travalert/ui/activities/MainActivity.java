package simone.bonvicini.travalert.travalert.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.jaredrummler.android.widget.AnimatedSvgView;

import java.util.Calendar;

import simone.bonvicini.travalert.travalert.R;
import simone.bonvicini.travalert.travalert.alarm.AlarmScheduler;
import simone.bonvicini.travalert.travalert.helper.DialogHelper;
import simone.bonvicini.travalert.travalert.model.LocationAlarm;
import simone.bonvicini.travalert.travalert.services.DataService;
import simone.bonvicini.travalert.travalert.services.LocationService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        AnimatedSvgView svgView = (AnimatedSvgView) findViewById(R.id.animated_svg_view);
        svgView.start();

        findViewById(R.id.maps_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

        findViewById(R.id.settings_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        findViewById(R.id.alarm_monitoring_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, AlarmMonitoringActivity.class));
            }
        });

        findViewById(R.id.alarm_delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog();
            }
        });
    }

    private void showDialog() {

        DialogHelper.showDialog(this, getString(R.string.alarm_delete_confirmation), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == DialogInterface.BUTTON_POSITIVE) {

                    deleteAlarm();
                    updateUi();
                }

                dialog.dismiss();
            }
        });
    }

    private void deleteAlarm() {

        stopService(new Intent(MainActivity.this, LocationService.class));
        DataService.get(MainActivity.this).deleteLocation();
    }

    @Override
    protected void onResume() {

        super.onResume();
        updateUi();
//        LocationAlarm alarm = new LocationAlarm(new LatLng(22.3,22.3),"cacca");
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.SECOND,10);
//        alarm.setEmergencyAlarm(calendar);
//        AlarmScheduler.get().setAlarm(alarm);
    }

    private void updateUi() {

        DataService service = DataService.get(this);
        View container = findViewById(R.id.current_alarm_container);
        if (service.loadLocation() != null) {
            container.setVisibility(View.VISIBLE);
            TextView currentAlarm = (TextView) findViewById(R.id.current_alarm);
            currentAlarm.setText(service.loadLocation().getDescription());
        } else {
            container.setVisibility(View.INVISIBLE);
        }
    }
}
