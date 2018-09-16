package org.streampipes.sensordemo.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.streampipes.sensordemo.R;
import org.streampipes.sensordemo.model.BeaconEvent;
import org.streampipes.sensordemo.model.LightEvent;
import org.streampipes.sensordemo.model.PictureEvent;
import org.streampipes.sensordemo.model.PressureEvent;

import java.text.DecimalFormat;
import java.util.Collection;

public class SensorsFragment extends BaseFragment implements BeaconConsumer, MonitorNotifier, SensorEventListener {

    private BeaconManager mBeaconManager;

    private static final String TAG = "SensorsFragment";

    private TextView mBeaconNameTextView;
    private TextView mBeaconDistanceTextView;

    private SensorManager mSensorManager;
    private Sensor mPressure;
    private Sensor mLight;


    @Override
    protected int getLayout() {
        return R.layout.fragment_sensors;
    }

    @Override
    protected void performFragmentLogic(View view) {

        mBeaconNameTextView = getViewById(view, R.id.beaconId, TextView.class);
        mBeaconDistanceTextView = getViewById(view, R.id.beaconSignal, TextView.class);

        mSensorManager = (SensorManager) mainActivity().getSystemService(Context.SENSOR_SERVICE);
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);


        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the main Eddystone-UID frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        mBeaconManager.setBackgroundScanPeriod(100);
        try {
            mBeaconManager.updateScanPeriods();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        // Set the two identifiers below to null to detect any beacon regardless of identifiers
        Identifier myBeaconNamespaceId = null;
        Identifier myBeaconInstanceId = null;
        //LinearLayout layout = findViewById(R.id.beaconmainlayout);
        Region region = new Region("my-beacon-region", myBeaconNamespaceId, myBeaconInstanceId, null);
//        mBeaconManager.addMonitorNotifier(this);
//        try {
//            mBeaconManager.startMonitoringBeaconsInRegion(region);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        mBeaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
//                if (beacons.size() > 0) {
//                    Log.i(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
//                }
                for (Beacon beacon : beacons) {
                    if (beacon.getBluetoothAddress().equals("C3:A0:82:F2:C1:CB")) {
                        mBeaconNameTextView.setText(beacon.getBluetoothAddress());
                        DecimalFormat df = new DecimalFormat("#.##");
                        String distance = df.format(beacon.getDistance());
                        mBeaconDistanceTextView.setText(String.valueOf(distance));
                        Log.i(TAG, "Beacon: (uid) +" + beacon.getBluetoothName() + " (id) " + beacon.getBluetoothAddress() + ", Distance: " + beacon.getDistance() + " meters away. " + region.getUniqueId());

                        BeaconEvent beaconEvent = new BeaconEvent(System.currentTimeMillis(), beacon.getDistance(), beacon.getBluetoothAddress());

                        mainActivity().publish(beaconEvent);
                    }
                }

            }
        });

        try {
            mBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }

    @Override
    public Context getApplicationContext() {
        return mainActivity().getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        mBeaconManager.unbind(this);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        mBeaconManager.bind(this);
        return true;
    }

    public void didEnterRegion(Region region) {
        Log.d(TAG, "I detected a beacon in the region with namespace id " + region.getId1() +
                " and instance id: " + region.getId2() + " and " + region.getBluetoothAddress());
    }

    public void didExitRegion(Region region) {
    }

    public void didDetermineStateForRegion(int state, Region region) {
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mPressure, 2000000);
        mSensorManager.registerListener(this, mLight, 2000000);
    }
    @Override
    public void onPause() {
        super.onPause();
        mBeaconManager.unbind(this);
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float value = event.values[0];
        Log.i(TAG, "onSensorChanged: " +event.sensor.getStringType());
        Log.i(TAG, "onSensorChanged: " +String.valueOf(value));
        if (event.sensor.getStringType().equals("android.sensor.pressure")) {
            mainActivity().publish(new PressureEvent(System.currentTimeMillis(), value));
        } else {
            mainActivity().publish(new LightEvent(System.currentTimeMillis(), value));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
