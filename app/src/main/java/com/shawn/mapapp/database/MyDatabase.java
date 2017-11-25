package com.shawn.mapapp.database;

/**
 * Created by shawndhillon on 2017-11-05.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class MyDatabase {
    private SQLiteDatabase db;
    private Context context;
    private final MyHelper helper;

    public MyDatabase (Context c){
        context = c;
        helper = new MyHelper(context);
    }

    public long insertData (String name, double longitude, double latitude, float zIndex, String username, String imgPath)
    {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.NAME, name);
        contentValues.put(Constants.LONG, longitude);
        contentValues.put(Constants.LAT, latitude);
        contentValues.put(Constants.Z_INDEX, zIndex);
        contentValues.put(Constants.USERNAME, username);
        contentValues.put(Constants.IMG_PATH, imgPath);
        long id = db.insert(Constants.TABLE_NAME, null, contentValues);
        return id;
    }

    public Cursor getData(String username)
    {
        // Select the markers that are owned by a username
        SQLiteDatabase db = helper.getWritableDatabase();
        // Returns these columns in the query
        String[] columns = {Constants.NAME, Constants.LONG, Constants.LAT, Constants.Z_INDEX, Constants.IMG_PATH};

        String selection = Constants.USERNAME + "='" + username + "'";  //Constants.USERNAME = 'username'
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);

        return cursor;
    }

    // TODO: Search DB based on whether name CONTAINS string
    public Cursor getSelectedData(String username, String name)
    {
        // Select the markers that are owned by a username
        SQLiteDatabase db = helper.getWritableDatabase();
        // Returns these columns in the query
        String[] columns = {Constants.NAME, Constants.LONG, Constants.LAT, Constants.Z_INDEX, Constants.IMG_PATH};

        String selection = Constants.USERNAME + "='" + username + "' AND " + Constants.NAME + "='" + name + "'";
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);
        return cursor;
    }

}
