package com.example.shikinami.diceroller;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {


    TextView result;
    TextView message;

    float accel;
    float accelCurrent;
    float accelLast;
    private SensorManager sensorManager;


    GestureDetectorCompat gestureDetector;
    private static final int SWIPE_MAX = 250;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (TextView) findViewById(R.id.text_view_result);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(mSensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        accel = 0.00f;
        accelLast = SensorManager.GRAVITY_EARTH;
        accelCurrent = SensorManager.GRAVITY_EARTH;

        //swiping
        gestureDetector = new GestureDetectorCompat(this, new MyGestureListener());
    }


    private final SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            //fancy math to get current acceleration
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            accelLast = accelCurrent;
            accelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = accelCurrent - accelLast;
            accel = accel * 0.9f + delta;

            //if enough force is here, it rolls the dice
            if (accel > 10) {
                onShake();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            //ignore this, it is not required to be filled, but must be present to work
        }
    };

    public void onShake() {
        //roll the dice!
        roll();
    }

    private void roll() {
        //random method used to generate a random number
        Random random = new Random();
        int roll = random.nextInt(20) + 1; //number between 1 - 20 inclusive

        //special messages
        if (roll == 20) {
            message.setText("Natural 20! You do what you wanna do!");
        } else {
            if (roll == 1) {
                message.setText("Natural 1! You fail with the utmost grace...");
            } else {
                result.setText(String.valueOf(roll));
                Toast.makeText(this, "May RNG-sus smile upon you!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(mSensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(mSensorEventListener);
        super.onPause();
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return true; //must be true to fling
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityx, float velocityy) {

            if (Math.abs(event1.getY() - event2.getY()) > SWIPE_MAX) {
                return false;
            }

            //swipe right to left
            if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityx) > SWIPE_THRESHOLD_VELOCITY) {
                Intent intent = new Intent(MainActivity.this.getBaseContext(), D12.class);
                startActivity(intent);
            }
            //swipe left to right
            else
            {
                if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityx) > SWIPE_THRESHOLD_VELOCITY)
                {
                    Intent intent = new Intent(MainActivity.this.getBaseContext(), D4.class);
                    startActivity(intent);
                }
            }
            return  false;
        }
    }
}

