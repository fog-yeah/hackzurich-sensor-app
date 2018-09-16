package org.streampipes.sensordemo.fragment;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import org.streampipes.sensordemo.R;
import org.streampipes.sensordemo.model.PictureEvent;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;

public class CameraFragment extends BaseFragment {

    private CameraView mCameraView;
    private static final String TAG = "CameraFragment";

    @Override
    protected int getLayout() {
        return R.layout.fragment_camera;
    }

    @Override
    protected void performFragmentLogic(View view) {
        mCameraView = getViewById(view, R.id.camera, CameraView.class);
        //mqttPublisher = new MqttPublisher();

        mCameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                Log.i(TAG, "onImage: ");
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, mCameraView.getWidth()/4, mCameraView.getHeight()/4, false);

                byte[] imageBytes = getImageBytes(bitmap);
                String imageString = toBase64(imageBytes);

                PictureEvent pictureEvent = new PictureEvent(System.currentTimeMillis(), imageString);

                mainActivity().publish(pictureEvent);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });



        final Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                mCameraView.captureImage();
                handler.postDelayed(this, 10000);
            }
        };
        handler.postDelayed(r, 10000);
    }

    private byte[] getImageBytes(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private String toBase64(byte[] imageBytes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(imageBytes);
        } else {
            return android.util.Base64.encodeToString(imageBytes, android.util.Base64.URL_SAFE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    public void onPause() {
        mCameraView.stop();
        super.onPause();
    }


}
