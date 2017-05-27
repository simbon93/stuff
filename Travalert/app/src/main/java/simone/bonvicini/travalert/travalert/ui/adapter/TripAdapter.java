package simone.bonvicini.travalert.travalert.ui.adapter;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import simone.bonvicini.travalert.travalert.R;

/**
 * Created by simone on 27/05/17.
 */

public class TripAdapter extends BaseAdapter {

    private Context mContext;

    private List<Address> mAddresses;

    private OnTripClickListener mListener;

    public TripAdapter(Context context, List<Address> addresses, OnTripClickListener listener) {

        mContext = context;
        mAddresses = addresses;
        mListener = listener;
    }

    @Override
    public int getCount() {

        return mAddresses != null ? mAddresses.size() : 0;
    }

    @Override
    public Object getItem(int position) {

        return mAddresses != null ? mAddresses.get(position) : null;
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.trip_row, null);

        final Address address = mAddresses.get(position);

        TextView trip = ((TextView) view.findViewById(R.id.trip_name));
        trip.setText(address.getLocality());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null) {
                    mListener.onTripSelected(address);
                }
            }
        });

        return view;
    }

    //interface

    public interface OnTripClickListener{

        void onTripSelected(Address address);
    }
}
