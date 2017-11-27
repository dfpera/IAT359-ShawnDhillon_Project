package com.shawn.mapapp;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.shawn.mapapp.database.MyDatabase;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        SensorEventListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMarkerClickListener {

    //Start Sensor Manager
    SensorManager mSensorManager;
    Sensor myLightSensor;
    MyDatabase db;
    ActionMode mActionMode;

    boolean isDay = true;
    boolean isMapReady = false;

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int RESULT_LOAD_IMG = 0;
    private static final int RESULT_LOAD_IMG_2 = 1;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // permission to access the location is missing

            // request permission at runtime
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mLocationPermissionGranted = true;
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLocationUI();
                } else {
                    Toast.makeText(this, "Permission NOT granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
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

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();


        // Load marker location if marker was selected
        Intent oldIntent = getIntent();
        if (oldIntent.hasExtra("lat")) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(oldIntent.getExtras().getString("lat")), Double.parseDouble(oldIntent.getExtras().getString("long"))))
                    .title(oldIntent.getExtras().getString("locationname"))
                    .draggable(true));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(Double.parseDouble(oldIntent.getExtras().getString("lat")), Double.parseDouble(oldIntent.getExtras().getString("long"))), DEFAULT_ZOOM));
        } else {
            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
        }

        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);

        isMapReady = true;

    }

    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Check if light has data. Show data
        if(sensorEvent.sensor.getType()==Sensor.TYPE_LIGHT) {
            //If light sensor value is less than 2, change theme to night mode.
            if(sensorEvent.values[0] < 3 && isDay) {
                Toast.makeText(this, "Night Mode Activated.", Toast.LENGTH_SHORT).show();
                if (isMapReady) {

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
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }
    public void saveMarker2(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG_2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMG) {
            if (resultCode == RESULT_OK) {
//                try {
                    Uri imageUri = data.getData();
                    // TODO: Move to search recycler view and info window
//                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
//                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
//                }
                Intent intent = getIntent();
                long id = db.insertData("name", 10.203942, 9.94930, 1.0f, intent.getStringExtra("username"), imageUri.toString());
                if (id < 0) {
                    Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == RESULT_LOAD_IMG_2) {
            if (resultCode == RESULT_OK) {
//                try {
                Uri imageUri = data.getData();
                // TODO: Move to search recycler view
//                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
//                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
//                }
                Intent intent = getIntent();
                long id = db.insertData("green", 10.203942, 9.94930, 1.0f, intent.getStringExtra("username"), imageUri.toString());
                if (id < 0) {
                    Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    @Override
//    public void onMapLongClick(LatLng point) {
//        Toast.makeText(this, "Point pressed: " + point, Toast.LENGTH_SHORT).show();
//        mMap.addMarker(new MarkerOptions()
//                .position(point)
//                .title("Our Custom Marker")
//                .draggable(true));
//    }

    @Override
    public void onMapClick(LatLng point) {
        Toast.makeText(this, "Point pressed: " + point, Toast.LENGTH_SHORT).show();
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title("Our Custom Marker")
                .draggable(true));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Toast.makeText(this, "Marker \"" + marker.getTitle() + "\" Picked Up", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Toast.makeText(this, "Marker Dropped at: " + marker.getPosition().latitude + " " + marker.getPosition().longitude, Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        showEditDialog(marker);
        return false;
    }


    private void showEditDialog(Marker marker) {
        FragmentManager fm = getSupportFragmentManager();
        InfoModal infoModal = InfoModal.newInstance("Some Title");

        Intent oldIntent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putString("username", oldIntent.getStringExtra("username"));
        bundle.putDouble("lat", marker.getPosition().latitude);
        bundle.putDouble("long", marker.getPosition().longitude);
        infoModal.setArguments(bundle);

        infoModal.show(fm, "fragment_edit_name");
    }


    // TODO: Add ability to clear markers
}
