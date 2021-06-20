package com.example.gruppe9_se2.game;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ShakeDetector implements SensorEventListener {

    private static final float SHAKE_THRESHOLD_GRAVITY = 1.1f; // change when using physical device //2.7f;
    private static final int SHAKE_GAP_TIME_MS = 50000;

    // The following are used for the shake detection
    private long mShakeTimestamp;
    private ShakeDetectorCallback callback;


    public interface ShakeDetectorCallback {
        void shakeDetected();
    }

    public void setCallback(ShakeDetectorCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        // gForce will be close to 1 when there is no movement.
        double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            Log.i("Sensor change", "gForce: "+gForce);

            final long now = System.currentTimeMillis();
            // ignore shake events too close to each other (500ms)
            if (mShakeTimestamp + SHAKE_GAP_TIME_MS > now) {
                return;
            }

            mShakeTimestamp = now;

            if (callback != null) {
                callback.shakeDetected();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // ignore
    }
}
