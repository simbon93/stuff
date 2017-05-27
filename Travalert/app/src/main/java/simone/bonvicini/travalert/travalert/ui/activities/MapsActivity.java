package simone.bonvicini.travalert.travalert.ui.activities;

import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import simone.bonvicini.travalert.travalert.R;
import simone.bonvicini.travalert.travalert.ui.adapter.TripAdapter;
import simone.bonvicini.travalert.travalert.ui.fragments.AlarmAreaFragment;
import simone.bonvicini.travalert.travalert.ui.fragments.AlarmFragment;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public final String TAG = this.getClass().getSimpleName();

    public static final double NESTED_VIEW_PERCENTAGE = 0.65;

    public enum MapView {
        AREA, ALARM, SUCCESS
    }

    private GoogleMap mMap;

    private MapView mCurrentView = MapView.AREA;

    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setUpNestedScrollView();

//        ListView listView = (ListView) findViewById(R.id.listview);

        List<Address> addresses = new ArrayList<>();

        for (int i = 0; i < 20; i++) {

            Address address = new Address(new Locale("IT"));
            address.setCountryName("Italy");
            address.setLocality("Fornaci " + i);
            addresses.add(address);
        }

        TripAdapter adapter = new TripAdapter(this, addresses, new TripAdapter.OnTripClickListener() {
            @Override
            public void onTripSelected(Address address) {

                Toast.makeText(MapsActivity.this, address.toString(), Toast.LENGTH_SHORT).show();
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        fillTripList(adapter);

//        listView.setAdapter(adapter);
//        fragmentCarousel();
    }

    private void fillTripList(TripAdapter adapter) {

        LinearLayout container = (LinearLayout) findViewById(R.id.listview);

        container.removeAllViews();

        for (int i = 0; i < adapter.getCount(); i++) {

            container.addView(adapter.getView(i, null, container));
        }
    }

    private void setUpNestedScrollView() {

        View bottomSheet = findViewById(R.id.bottom_sheet);
        int heightPixels = getResources().getDisplayMetrics().heightPixels;
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
        layoutParams.height = (int) (heightPixels * NESTED_VIEW_PERCENTAGE);
        bottomSheet.setLayoutParams(layoutParams);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        findViewById(R.id.saved_trips_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    private void fragmentCarousel() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mCurrentView.ordinal() < MapView.values().length - 1) {

                    Toast.makeText(MapsActivity.this, String.valueOf(mCurrentView.ordinal()), Toast.LENGTH_SHORT).show();
                    pushFragment(mCurrentView);
                    mCurrentView = MapView.values()[mCurrentView.ordinal() + 1];
                    fragmentCarousel();
                }
            }
        }, 2000);
    }

    private void pushFragment(MapView mapView) {

        Fragment fragment = null;

        mCurrentView = mapView;

        switch (mapView) {

            case AREA:
                fragment = new AlarmAreaFragment();
                break;
            case ALARM:
                fragment = new AlarmFragment();
                break;
            case SUCCESS:
                fragment = new AlarmAreaFragment();
                break;
        }

        if (fragment == null) {
            return;
        }

        Log.d(TAG, "Switched to fragment: " + mCurrentView.name());

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
        transaction.replace(R.id.container, fragment, null);
        if (!isDestroyed() && !isFinishing()) {
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
