package simone.bonvicini.travalert.travalert.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import simone.bonvicini.travalert.travalert.R;
import simone.bonvicini.travalert.travalert.model.LocationAlarm;
import simone.bonvicini.travalert.travalert.ui.activities.MapsActivity;

public class AlarmAreaFragment extends Fragment {

    public static final int MIN = 1;

    public static final int SCALE = 300;

    private View mView;

    private MapsActivity mActivity;

    private LocationAlarm mCurrentLocationAlarm;

    private TextView mArea;

    private int mRadius;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mActivity = ((MapsActivity) getActivity());
        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_alarm_area, container, false);

        return mView;
    }

    private void getData() {

        mCurrentLocationAlarm = mActivity.getSelectedLocationAlarm();
        mRadius = mCurrentLocationAlarm.getRadius();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mArea = (TextView) mView.findViewById(R.id.area);
        mArea.setText(getFormattedLabel(mRadius));

        final AppCompatSeekBar areaSeekBar = (AppCompatSeekBar) mView.findViewById(R.id.location_area);
        areaSeekBar.setMax(300);
        areaSeekBar.setProgress(mCurrentLocationAlarm.getRadius()*10/SCALE+MIN);
        areaSeekBar.setKeyProgressIncrement(1);
        areaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int radius = ((MIN + progress) * SCALE) / 10;
                mArea.setText(getFormattedLabel(radius));
                mActivity.updateRadius(radius);
                mActivity.drawCircle(radius);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mView.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mActivity.computeViewFlow(true, null);
            }
        });

        mView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mActivity.computeViewFlow(false, null);
            }
        });

        mView.findViewById(R.id.increment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                areaSeekBar.setProgress(areaSeekBar.getProgress()+1);
            }
        });

        mView.findViewById(R.id.decrement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                areaSeekBar.setProgress(areaSeekBar.getProgress()-1);
            }
        });
    }

    private String getFormattedLabel(int radius) {

        if (radius < 1000) {
            return radius + "m";
        } else {
            return String.format("%.1f", ((float) radius / 1000)) + "km";
        }
    }
}
