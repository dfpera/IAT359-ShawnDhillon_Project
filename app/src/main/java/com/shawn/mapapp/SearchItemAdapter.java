package com.shawn.mapapp;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shawn.mapapp.database.Constants;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.MyViewHolder> {

    public Cursor cursor;
    Context context;
    String username;


    public SearchItemAdapter(Cursor cursor, Context context, String username) {
        this.context = context;
        this.cursor = cursor;
        this.username = username;
    }

    @Override
    public SearchItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v, username);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchItemAdapter.MyViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.locationName.setText(cursor.getString(cursor.getColumnIndex(Constants.NAME)));
        holder.longitude.setText(cursor.getString(cursor.getColumnIndex(Constants.LONG)));
        holder.latitude.setText(cursor.getString(cursor.getColumnIndex(Constants.LAT)));

        // Set image
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            String imageString = cursor.getString(cursor.getColumnIndex(Constants.IMG_PATH));
            if (imageString != "") {
                try {
                    Uri imageUri = Uri.parse(imageString);
                    final InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    holder.locationImg.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Something went wrong when loading image.", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(context, "Cannot load image without persmissions granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView locationName;
        public TextView longitude;
        public TextView latitude;
        public ImageView locationImg;
        public LinearLayout myLayout;
        public String username;

        Context context;

        public MyViewHolder(View itemView, String username) {
            super(itemView);
            myLayout = (LinearLayout) itemView;

            this.username = username;
            locationName = (TextView) itemView.findViewById(R.id.locationName);
            longitude = (TextView) itemView.findViewById(R.id.longitude);
            latitude = (TextView) itemView.findViewById(R.id.latitude);
            locationImg = (ImageView) itemView.findViewById(R.id.locationImg);

            itemView.setOnClickListener(this);
            context = itemView.getContext();

        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtra("lat", latitude.getText());
            intent.putExtra("long", longitude.getText());
            intent.putExtra("locationname", locationName.getText());
            intent.putExtra("username", username);
            context.startActivity(intent);
        }
    }
}