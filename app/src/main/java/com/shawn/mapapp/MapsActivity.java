package com.shawn.mapapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shawn.mapapp.database.MyDatabase;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {
    //Start Sensor Manager
    SensorManager mSensorManager;
    Sensor myLightSensor;
    MyDatabase db;

    boolean isDay = true;

    private GoogleMap mMap;

    private static final String TAG = MapsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Get sensor services, along with light sensor
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        myLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        db = new MyDatabase(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Check if light sensor is present or not, if it is get the name and show it.
        if (myLightSensor == null) {
            Toast.makeText(this, "No Light Sensor!", Toast.LENGTH_SHORT).show();
        } else {
            mSensorManager.registerListener(this,
                    myLightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Check if light has data. Show data
        if(sensorEvent.sensor.getType()==Sensor.TYPE_LIGHT) {
            //If light sensor value is less than 2, change theme to night mode.
            if(sensorEvent.values[0] < 3 && isDay) {
                Toast.makeText(this, "Night Mode Activated.", Toast.LENGTH_SHORT).show();

                isDay = false;

                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    boolean success = mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this, R.raw.night_mode));

                    if (!success) {
                        Log.e(TAG, "Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "Can't find style. Error: ", e);
                }

            // If light sensor value is greater than 3, change theme to day mode.
            } else if (sensorEvent.values[0] > 5 && !isDay) {
                Toast.makeText(this, "Day Mode Activated.", Toast.LENGTH_SHORT).show();

                isDay = true;

                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    boolean success = mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this, R.raw.day_mode));

                    if (!success) {
                        Log.e(TAG, "Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "Can't find style. Error: ", e);
                }

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void search(View view) {
        Intent oldIntent = getIntent();
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("username", oldIntent.getStringExtra("username"));
        startActivity(intent);
    }

    public void saveMarker(View view) {
        Intent intent = getIntent();
        long id = db.insertData("name", 10.203942, 9.94930, 1.0f, intent.getStringExtra("username"), "");
        if (id < 0) {
            Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        }
    }
}
