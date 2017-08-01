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
import simone.bonvicini.travalert.travalert.helper.DateTimeHelper;
import simone.bonvicini.travalert.travalert.model.LocationAlarm;
import simone.bonvicini.travalert.travalert.services.DataService;
import simone.bonvicini.travalert.travalert.ui.activities.MapsActivity;

public class AlarmFragment extends Fragment {

    private MapsActivity mActivity;

    private LocationAlarm mCurrentLocationAlarm;

    private View mView;

    private TextView mHours;

    private TextView mMinutes;

    private TextView mAmPm;

    private boolean mIs24h;

    private boolean mAlarmSet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mActivity = ((MapsActivity) getActivity());
        mCurrentLocationAlarm = mActivity.getSelectedLocationAlarm();

        mView = inflater.inflate(R.layout.fragment_alarm, container, false);
        mIs24h = DataService.get(mActivity).getAlarmFormat() == 1;

        return mView;
    }

    private void initUi() {

        LinearLayout alarmContainer = (LinearLayout) mView.findViewById(R.id.alarm_container);
        mHours = (TextView) mView.findViewById(R.id.hours);
        mMinutes = (TextView) mView.findViewById(R.id.minutes);
        mAmPm = (TextView) mView.findViewById(R.id.am_pm);

        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        mHours.setText(DateTimeHelper.formatHours(c.getTime(), mIs24h));
        mMinutes.setText(DateTimeHelper.formatMinutes(c.getTime()));
        mAmPm.setVisibility(!mIs24h ? View.VISIBLE : View.GONE);
        mAmPm.setText(DateTimeHelper.formatAmPm(c.getTime()));

        alarmContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showTimePicker();
            }
        });

        mView.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mAlarmSet) {
                    mActivity.updateTime(c);
                }
                mActivity.computeViewFlow(true, null);
            }
        });

        mView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mActivity.computeViewFlow(false, null);
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
                mAlarmSet = true;
                mHours.setText(DateTimeHelper.formatHours(c.getTime(), mIs24h));
                mMinutes.setText(DateTimeHelper.formatMinutes(c.getTime()));
                mAmPm.setText(DateTimeHelper.formatAmPm(c.getTime()));

            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), mIs24h);

        timePickerDialog.show();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        initUi();
    }
}
