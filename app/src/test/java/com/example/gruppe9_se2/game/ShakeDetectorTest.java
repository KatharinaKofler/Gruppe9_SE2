package com.example.gruppe9_se2.game;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ShakeDetectorTest {

    boolean called;

    @Before
    public void setup() {
        called = false;
    }

    @Test
    public void noShake() throws NoSuchFieldException, IllegalAccessException {
        ShakeDetector sd = new ShakeDetector();
        sd.setCallback(this::sensorCallback);
        SensorEvent se = getAccelerometerSensorEvent(new float[] {0.0f, 0.0f, SensorManager.GRAVITY_EARTH});
        sd.onSensorChanged(se);
        assertFalse(called);
    }

    @Test
    public void shake() throws NoSuchFieldException, IllegalAccessException {
        ShakeDetector sd = new ShakeDetector();
        sd.setCallback(this::sensorCallback);
        // set value a little bigger than the threshold
        SensorEvent se = getAccelerometerSensorEvent(new float[] {0.0f, 0.0f, 1.11f * SensorManager.GRAVITY_EARTH});
        sd.onSensorChanged(se);
        assertTrue(called);
    }

    // see https://stackoverflow.com/questions/34530865/how-to-mock-motionevent-and-sensorevent-for-unit-testing-in-android
    private SensorEvent getAccelerometerSensorEvent(float[] values) throws NoSuchFieldException, IllegalAccessException {
        // Create the SensorEvent to eventually return.
        SensorEvent sensorEvent = Mockito.mock(SensorEvent.class);

        // Get the 'sensor' field in order to set it.
        Field sensorField = SensorEvent.class.getField("sensor");
        sensorField.setAccessible(true);
        // Create the value we want for the 'sensor' field.
        Sensor sensor = Mockito.mock(Sensor.class);
        when(sensor.getType()).thenReturn(Sensor.TYPE_ACCELEROMETER);
        // Set the 'sensor' field.
        sensorField.set(sensorEvent, sensor);

        // Get the 'values' field in order to set it.
        Field valuesField = SensorEvent.class.getField("values");
        valuesField.setAccessible(true);
        // Create the values we want to return for the 'values' field.
        valuesField.set(sensorEvent, values);

        return sensorEvent;
    }

    private void sensorCallback() {
        called = true;
    }
}