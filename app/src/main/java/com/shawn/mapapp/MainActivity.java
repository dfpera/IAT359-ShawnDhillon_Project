package com.shawn.mapapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class


MainActivity extends AppCompatActivity {
    EditText usernameEditText, passwordEditText;
    private static final String DEFAULT = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameEditText = (EditText)findViewById(R.id.editTextUsername);
        passwordEditText = (EditText)findViewById(R.id.editTextPassword);
    }

    public void toLogin (View view){
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        // If username/password is correct send user to settings
        if(     sharedPrefs.getString("username", DEFAULT).equals(usernameEditText.getText().toString())
                && sharedPrefs.getString("password", DEFAULT).equals(passwordEditText.getText().toString())) {

            Toast.makeText(this, "Welcome Back " + sharedPrefs.getString("username", DEFAULT), Toast.LENGTH_LONG).show();
            Intent intent= new Intent(this, MapsActivity.class); // Send user to settings activity
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
        startActivity(intent);
    }

}
