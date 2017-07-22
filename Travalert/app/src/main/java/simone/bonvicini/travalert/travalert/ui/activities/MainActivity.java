package simone.bonvicini.travalert.travalert.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.jaredrummler.android.widget.AnimatedSvgView;

import simone.bonvicini.travalert.travalert.R;
import simone.bonvicini.travalert.travalert.services.DataService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    @Override
    protected void onResume() {

        super.onResume();
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
