package com.example.shikinami.diceroller;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class D6 extends AppCompatActivity {

    TextView result;
    TextView message;

    float accel;
    float accelCurrent;
    float accelLast;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d6);
        result = (TextView) findViewById(R.id.text_view_result);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(mSensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        accel = 0.00f;
        accelLast = SensorManager.GRAVITY_EARTH;
        accelCurrent = SensorManager.GRAVITY_EARTH;
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
        int roll = random.nextInt(6) + 1; //number between 1 - 6 inclusive

        //special messages
        if (roll == 9) {
            message.setText("6 is an upside down 9!");
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
}
