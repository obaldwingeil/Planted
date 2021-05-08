package com.example.afinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CheckLightActivity extends AppCompatActivity implements SensorEventListener {

    private Button button_check;

    private SensorManager sensorManager;
    private Sensor lightSensor;

    protected String light;
    protected String checkLight;
    protected String buyURL;
    protected String plantName;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        Intent intent = getIntent();
        light = intent.getStringExtra("light");
        buyURL = intent.getStringExtra("buyURL");
        plantName = intent.getStringExtra("plantName");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        checkLight = "";

        loadFragment(new CheckFragment(), R.id.fragContainer_check);

        button_check = findViewById(R.id.button_check);
        button_check.setOnClickListener(v -> checkLight());
    }

    public void loadFragment(Fragment fragment, int id){
        FragmentManager fragmentManager = getSupportFragmentManager();
        // create a fragment transaction to begin the transaction and replace the fragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //replacing the placeholder - fragmentContainterView with the fragment that is passed as parameter
        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.commit();
    }

    public void checkLight(){
        if(checkLight.equals(light)){
            loadFragment(new MatchLightFragment(), R.id.fragContainer_check);
        }
        else if (checkLight.length() != 0){
            loadFragment(new MisMatchLightFragment(), R.id.fragContainer_check);
        }
        else{
            Toast.makeText(this, "No Sensor Data", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // register our sensor listeners
        if(lightSensor != null){
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // unregister all the listeners so that they do not gather information when the app is paused
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        int sensorType = event.sensor.getType();
        // grab the new data of the sensor
        if(sensorType == Sensor.TYPE_LIGHT){
            float currentValue = event.values[0];
            Log.d("sensor value", String.valueOf(currentValue));

            if(currentValue <= lightSensor.getMaximumRange()/3){
                checkLight = "low light";
            }
            else if(currentValue <= (lightSensor.getMaximumRange()/3)*2){
                checkLight = "indirect bright light";
            }
            else{
                checkLight = "bright light";
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
