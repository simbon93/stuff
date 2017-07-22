package simone.bonvicini.travalert.travalert.ui.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import simone.bonvicini.travalert.travalert.R;
import simone.bonvicini.travalert.travalert.model.LocationAlarm;
import simone.bonvicini.travalert.travalert.ui.activities.MapsActivity;

public class AlarmFragment extends Fragment {

    private MapsActivity mActivity;

    private LocationAlarm mCurrentLocationAlarm;

    private View mView;

    private TextView mHours;

    private TextView mMinutes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mActivity = ((MapsActivity) getActivity());
        mCurrentLocationAlarm = mActivity.getSelectedLocationAlarm();

        mView = inflater.inflate(R.layout.fragment_alarm, container, false);

        return mView;
    }

    private void initUi() {

        LinearLayout alarmContainer = (LinearLayout) mView.findViewById(R.id.alarm_container);
        mHours = (TextView) mView.findViewById(R.id.hours);
        mMinutes = (TextView) mView.findViewById(R.id.minutes);

        alarmContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showTimePicker();
            }
        });

        mView.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.computeViewFlow(true,null);
            }
        });

        mView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.computeViewFlow(false,null);
            }
        });
    }

    private void showTimePicker() {

        Calendar calendar = Calendar.getInstance();

        if (mCurrentLocationAlarm.getEmergencyAlarm() != null) {
            calendar = mCurrentLocationAlarm.getEmergencyAlarm();
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);

                mActivity.updateTime(c);
                mHours.setText(String.valueOf(hourOfDay));
                mMinutes.setText(String.valueOf(minute));

            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        timePickerDialog.show();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        initUi();
    }
}
