package org.streampipes.sensordemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;

import net.igenius.mqttservice.MQTTService;
import net.igenius.mqttservice.MQTTServiceCommand;
import net.igenius.mqttservice.MQTTServiceReceiver;

import org.streampipes.sensordemo.fragment.CameraFragment;
import org.streampipes.sensordemo.fragment.SensorsFragment;
import org.streampipes.sensordemo.fragment.SettingsFragment;
import org.streampipes.sensordemo.model.AbstractEvent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String TAG = "MainActivity";

    private MQTTServiceReceiver receiver = new MQTTServiceReceiver() {
        @Override
        public void onSubscriptionSuccessful(Context context,
                                             String requestId, String topic) {
            // called when a message has been successfully published
            Log.i(TAG, "onSubscriptionSuccessful: ");
        }

        @Override
        public void onSubscriptionError(Context context, String requestId,
                                        String topic, Exception exception) {
            // called when a subscription is not successful.
            // This usually happens when the broker does not give permissions
            // for the requested topic
            Log.i(TAG, "onSubscriptionError: ");
        }

        @Override
        public void onPublishSuccessful(Context context, String requestId, String topic) {
            // called when a subscription is successful
            Log.i(TAG, "onPublishSuccessful: ");
        }

        @Override
        public void onMessageArrived(Context context, String topic,
                                     byte[] payload) {
            // called when a new message arrives on any topic
            Log.i(TAG, "onMessageArrived: ");
        }

        @Override
        public void onConnectionSuccessful(Context context, String requestId) {
            // called when the connection is successful
            Log.i(TAG, "onConnectionSuccessful: ");
        }

        @Override
        public void onException(Context context, String requestId,
                                Exception exception) {
            // called when an error happens
            Log.i(TAG, "onException: ");
            exception.printStackTrace();
            Log.i(TAG, "onException: " + exception.getMessage());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            Log.i(TAG, "onException: " +sStackTrace);


        }

        @Override
        public void onConnectionStatus(Context context, boolean connected) {
            // called when connection status is requested or changes
            Log.i(TAG, "onConnectionStatus: ");
        }
    };


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchFragment(new SensorsFragment());
                    return true;
                case R.id.navigation_dashboard:
                    switchFragment(new CameraFragment());
                    return true;
                case R.id.navigation_notifications:
                    switchFragment(new SettingsFragment());
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        receiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        receiver.unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MQTTService.NAMESPACE = "org.streampipes.test"; //or BuildConfig.APPLICATION_ID;
        MQTTService.KEEP_ALIVE_INTERVAL = 60; //in seconds
        MQTTService.CONNECT_TIMEOUT = 30; //in seconds
        MQTTServiceCommand.connect(this, "tcp://streampipes5270.cloudapp.net:1883", "asd", null, null);


        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons in the background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }

                });
                builder.show();
            }
        }

        switchFragment(new SensorsFragment());



    }

    private Properties getProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "ipe-koi04.fzi.de");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("message.max.bytes", 5000012);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        return props;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           switchFragment(new SensorsFragment());
                        }
                    });
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    public void switchFragment(Fragment fragment) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(fragment.getClass().getName());
        if (currentFragment == null || !currentFragment.isVisible()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right)
                    .replace(R.id.content_frame, fragment, fragment.getClass().getName())
                    .addToBackStack(fragment.getClass().getName())
                    .commit();
        }
    }


    public void publish(String topic, String payload) {
        MQTTServiceCommand.publish(this, topic,
        payload.getBytes(), 0);
    }

    public void publish(AbstractEvent event) {
        publish(event.getTopic(), new Gson().toJson(event));
    }

}
