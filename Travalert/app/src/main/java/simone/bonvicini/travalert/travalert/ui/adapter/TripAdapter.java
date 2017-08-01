package simone.bonvicini.travalert.travalert.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import simone.bonvicini.travalert.travalert.R;
import simone.bonvicini.travalert.travalert.model.LocationAlarm;

/**
 * Created by simone on 27/05/17.
 */

public class TripAdapter extends BaseAdapter {

    private Context mContext;

    private List<LocationAlarm> mAlarms;

    private OnTripClickListener mListener;

    public TripAdapter(Context context, List<LocationAlarm> alarms, OnTripClickListener listener) {

        mContext = context;
        mAlarms = alarms;
        mListener = listener;
    }

    public void setAlarms(List<LocationAlarm> alarms) {

        mAlarms = alarms;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        return mAlarms != null ? mAlarms.size() : 0;
    }

    @Override
    public Object getItem(int position) {

        return mAlarms != null ? mAlarms.get(position) : null;
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.trip_row, null);

        final LocationAlarm locationAlarm = mAlarms.get(position);

        TextView trip = ((TextView) view.findViewById(R.id.trip_name));
        trip.setText(locationAlarm.getDescription());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null) {
                    mListener.onTripSelected(locationAlarm);
                }
            }
        });

        view.findViewById(R.id.delete_favourite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onTripDeleted(locationAlarm);
                }
            }
        });

        return view;
    }

    //interface

    public interface OnTripClickListener{

        void onTripSelected(LocationAlarm place);

        void onTripDeleted(LocationAlarm place);
    }
}
