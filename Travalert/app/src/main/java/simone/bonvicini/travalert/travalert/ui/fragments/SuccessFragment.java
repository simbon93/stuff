package simone.bonvicini.travalert.travalert.ui.fragments;

import android.app.TimePickerDialog;
import android.opengl.ETC1;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import simone.bonvicini.travalert.travalert.R;
import simone.bonvicini.travalert.travalert.model.LocationAlarm;
import simone.bonvicini.travalert.travalert.ui.activities.MapsActivity;

public class SuccessFragment extends Fragment {

    private MapsActivity mActivity;

    private LocationAlarm mCurrentLocationAlarm;

    private View mView;

    private EditText mAlarmName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mActivity = ((MapsActivity) getActivity());
        mCurrentLocationAlarm = mActivity.getSelectedLocationAlarm();

        mView = inflater.inflate(R.layout.fragment_success, container, false);

        return mView;
    }

    private void initUi() {

        mAlarmName = (EditText)mView.findViewById(R.id.alarm_name);
        final SwitchCompat addToFavorite = (SwitchCompat) mView.findViewById(R.id.add_to_favorite);
        final Button setAlarmButton = (Button) mView.findViewById(R.id.set_alarm_button);
        mAlarmName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                boolean emptyName = s.toString().isEmpty();
                setAlarmButton.setEnabled(!emptyName);
            }
        });

        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (addToFavorite.isChecked()) {
                    mCurrentLocationAlarm.setDescription(mAlarmName.getText().toString());
                    mActivity.addToFavorite(mCurrentLocationAlarm);
                }
                mActivity.setAlarm();
            }
        });

        mView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mActivity.computeViewFlow(false, null);
            }
        });

        addToFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mAlarmName.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
                boolean emptyName = mAlarmName.getText().toString().isEmpty();
                setAlarmButton.setEnabled(!emptyName || !isChecked);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        initUi();
    }
}
