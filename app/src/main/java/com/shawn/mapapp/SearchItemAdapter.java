package com.shawn.mapapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shawn.mapapp.database.Constants;

import java.util.ArrayList;

public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.MyViewHolder> {

    public Cursor cursor;
    Context context;

    public SearchItemAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public SearchItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        cursor.moveToFirst();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchItemAdapter.MyViewHolder holder, int position) {
        if (!cursor.isAfterLast()) {
            holder.locationName.setText(cursor.getString(cursor.getColumnIndex(Constants.NAME)));
            holder.longitude.setText(cursor.getString(cursor.getColumnIndex(Constants.LONG)));
            holder.latitude.setText(cursor.getString(cursor.getColumnIndex(Constants.LAT)));
            // TODO: Implement image from path
            // holder.locationImg.setText(cursor.getString(cursor.getColumnIndex(Constants.imgPath)));
            cursor.moveToNext();
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

        Context context;

        public MyViewHolder(View itemView) {
            super(itemView);
            myLayout = (LinearLayout) itemView;

            locationName = (TextView) itemView.findViewById(R.id.locationName);
            longitude = (TextView) itemView.findViewById(R.id.longitude);
            latitude = (TextView) itemView.findViewById(R.id.latitude);
            locationImg = (ImageView) itemView.findViewById(R.id.locationImg);

            itemView.setOnClickListener(this);
            context = itemView.getContext();

        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context,
                    "You have clicked " + (locationName).getText().toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}