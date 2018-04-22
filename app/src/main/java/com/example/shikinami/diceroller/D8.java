package com.example.shikinami.diceroller;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class D8 extends AppCompatActivity implements GestureDetector.OnGestureListener {

    TextView result;
    TextView message;

    float accel;
    float accelCurrent;
    float accelLast;
    private SensorManager sensorManager;

    private GestureDetector gesture;
    private static final int SWIPE_MIN_DISTANCE = 120; //minimum
    private static final int SWIPE_MAX_OFF_PATH = 250; //to here
    private static final int SWIPE_THRESHOLD_VELOCITY = 200; //minimum speed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d8);

        result = (TextView) findViewById(R.id.text_view_result);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(mSensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        accel = 0.00f;
        accelLast = SensorManager.GRAVITY_EARTH;
        accelCurrent = SensorManager.GRAVITY_EARTH;

        gesture = new GestureDetector(this);
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
        int roll = random.nextInt(8) + 1; //number between 1 - 12 inclusive

        //special messages
        if (roll == 8) {
            message.setText("8 is a symmetrical number!");
        } else {
            if (roll == 1) {
                message.setText("*Price is Right Fail Sounds*");
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

    //GESTURES
    //when touch event is detected use the gesturelistener
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gesture.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        //required to function
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float ve1ocityY) {
        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
            return false;

        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            //right to left
            Intent intent = new Intent(this, D6.class);
            startActivity(intent);
        } else {
            if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //left to right
                Intent intent = new Intent(this, D10.class);
                startActivity(intent);
            }
        }
        return false;
    }


    @Override
    public void onShowPress(MotionEvent motionEvent) {
        //auto generated
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        //auto generated
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        //auto generated
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        //auto generated
    }
}
