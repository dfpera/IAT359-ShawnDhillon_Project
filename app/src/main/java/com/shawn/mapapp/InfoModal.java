package com.shawn.mapapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.shawn.mapapp.database.MyDatabase;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;


public class InfoModal extends DialogFragment implements View.OnClickListener {
    private EditText locationName;
    private ImageButton imageButton;
    private Button saveButton;
    private Button cancelButton;
    private MyDatabase db;

    private String imageString;

    private static final int RESULT_LOAD_IMG = 0;

    public InfoModal() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static InfoModal newInstance(String title) {
        InfoModal frag = new InfoModal();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_modal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        locationName = (EditText) view.findViewById(R.id.locationName);
        imageButton = (ImageButton) view.findViewById(R.id.imageButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);
        saveButton = (Button) view.findViewById(R.id.saveButton);

        imageButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        db = new MyDatabase(getContext());

        // Show soft keyboard automatically and request focus to field
        locationName.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        getDialog().getWindow().setLayout((int)(400 * metrics.density), (int)(300 * metrics.density));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imageButton) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
        } else if (view.getId() == R.id.saveButton) {
            Bundle bundle = this.getArguments();
            String username = "";
            double latitude = 0.0;
            double longitude = 0.0;
            if (bundle != null) {
                username = bundle.getString("username");
                latitude = bundle.getDouble("lat");
                longitude = bundle.getDouble("long");
            }
            long id = db.insertData(locationName.getText().toString(), latitude, longitude, 1.0f, username, imageString);
            if (id < 0) {
                Toast.makeText(getContext(), "fail", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        } else if (view.getId() == R.id.cancelButton) {
            dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMG) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri imageUri = data.getData();
                    imageString = imageUri.toString();
                    final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imageButton.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Something went wrong when loading image.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
