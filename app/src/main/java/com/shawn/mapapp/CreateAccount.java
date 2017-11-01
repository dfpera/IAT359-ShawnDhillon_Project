package com.shawn.mapapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateAccount extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    SharedPreferences sharedPrefs;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        usernameEditText = (EditText)findViewById(R.id.usernameEditText);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        registerButton = (Button) findViewById(R.id.registerButton);
    }

    public void register(View view){

        sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("username", usernameEditText.getText().toString());
        editor.putString("password", passwordEditText.getText().toString());
        Toast.makeText(this, "Username and password saved to Preferences", Toast.LENGTH_LONG).show();
        editor.commit();
        Intent intent= new Intent(this, MainActivity.class); // Send user back to home screen
        startActivity(intent);
    }
}
