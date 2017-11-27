package com.shawn.mapapp;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class


MainActivity extends AppCompatActivity {
    EditText usernameEditText, passwordEditText;
    private static final String DEFAULT = "0";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameEditText = (EditText)findViewById(R.id.editTextUsername);
        passwordEditText = (EditText)findViewById(R.id.editTextPassword);
    }

    @Override
    protected void onResume() {
        super.onResume();
        enablePermissions();
    }

    // enables the My Location layer if the fine location permission has been granted.
    private void enablePermissions() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // permission to access the location is missing

            // request permission at runtime
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE},
                    LOCATION_PERMISSION_REQUEST_CODE);
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
                    Toast.makeText(this, "Permission(s) granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission NOT granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void toLogin (View view){
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        // If username/password is correct send user to settings
        if(     sharedPrefs.getString("username", DEFAULT).equals(usernameEditText.getText().toString())
                && sharedPrefs.getString("password", DEFAULT).equals(passwordEditText.getText().toString())) {

            Toast.makeText(this, "Welcome Back " + sharedPrefs.getString("username", DEFAULT), Toast.LENGTH_LONG).show();
            Intent intent= new Intent(this, MapsActivity.class); // Send user to map activity
            intent.putExtra("username", sharedPrefs.getString("username", DEFAULT));
            startActivity(intent);

            // If username/password is NOT correct send user to register activity
        } else {
            Toast.makeText(this, "Incorrect user. Register as a new user.", Toast.LENGTH_LONG).show();
            Intent intent= new Intent(this, CreateAccount.class); // Send user to register activity
            startActivity(intent);

        }
    }

    public void toCreateAccount(View view) {
        Intent intent = new Intent(this, CreateAccount.class);
        startActivity(intent);
    }

    public void toMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("username", "TestUser");
        startActivity(intent);
    }

}
