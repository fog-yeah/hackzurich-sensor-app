package org.streampipes.sensordemo.messaging;

import android.content.Context;
import android.util.Log;

import net.igenius.mqttservice.MQTTServiceReceiver;

public class MQTTReceiver extends MQTTServiceReceiver {

    private static final String TAG = "MQTTReceiver";

    @Override
    public void onPublishSuccessful(Context context, String requestId,
                                    String topic) {
        // called when a message has been successfully published
        Log.i(TAG, "onPublishSuccessful: ");
    }

    @Override
    public void onSubscriptionSuccessful(Context context, String requestId,
                                         String topic) {
        // called when a subscription is successful
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
    public void onMessageArrived(Context context, String topic,
                                 byte[] payload) {
        // called when a new message arrives on any topic
        Log.i(TAG, "onMessageArrived: ");
    }

    @Override
    public void onConnectionSuccessful(Context context, String requestId) {
        // called when the connection is successful
    }

    @Override
    public void onException(Context context, String requestId,
                            Exception exception) {
        // called when an error happens
    }

    @Override
    public void onConnectionStatus(Context context, boolean connected) {
        // called when connection status is requested or changes
    }
}
