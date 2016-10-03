package example.android.com.pacecounter;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by mbj94 on 2016-10-02.
 */
public class RecordingFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    protected TextView count;
    protected TextView distance;
    protected TextView location;
    protected View rootView;

    Thread thread;
    BroadcastReceiver receiver;


    private InfoUpdateHandler handler = new InfoUpdateHandler();
    private static final int EVENT_FIND_ADDRESS_DONE = 1;
    private static final int EVENT_FIND_ADDRESS = 2;
    String result_address;

    LocationManager locationManager;
    double longitude;
    double latitude;

    public RecordingFragment() {
    }

    class InfoUpdateHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_FIND_ADDRESS_DONE:
                    location.setText(result_address);
                    break;
                case EVENT_FIND_ADDRESS:
                    //thread.start();
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_page1, container, false);
        rootView.findViewById(R.id.start).setOnClickListener(this);
        rootView.findViewById(R.id.stop).setOnClickListener(this);

        count = (TextView) rootView.findViewById(R.id.count);
        distance = (TextView) rootView.findViewById(R.id.distance);
        location = (TextView) rootView.findViewById(R.id.location);

        receiver = new CountRecever();
        IntentFilter intentFilter = new IntentFilter("example.android.com.pacecounter");
        getActivity().registerReceiver(receiver, intentFilter);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, mLocationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, mLocationListener);

        thread = new NetworkThread();
        thread.start();

        return rootView;
    }

    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude(); //경도
            latitude = location.getLatitude(); //위도
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    @Override
    public void onClick(View v) {
        int view = v.getId();
        if (view == R.id.start) {
            getActivity().startService(new Intent(RecordingFragment.this.getActivity(), PaceCheckerService.class));
            Log.v("qioip", "start clicked");
        } else {
            getActivity().stopService(new Intent(RecordingFragment.this.getActivity(), PaceCheckerService.class));
            Log.v("qioip", "stop clicked");
        }
    }


    class NetworkThread extends Thread {
        @Override
        public void run() {
            while (true) {
                // TODO Auto-generated method stub
                result_address = Utils.getReverseGeoCodingInfo(longitude, latitude);
                Log.v("qioip", result_address);
                handler.sendEmptyMessage(EVENT_FIND_ADDRESS_DONE);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    class CountRecever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("example.android.com.pacecounter")) {
                Log.v("ffff", "receive intent");
                String countValue = intent.getStringExtra("count");
                String distanceValue = intent.getStringExtra("distance");
                count.setText(countValue);
                distance.setText(distanceValue);
            }
        }

    }

}
