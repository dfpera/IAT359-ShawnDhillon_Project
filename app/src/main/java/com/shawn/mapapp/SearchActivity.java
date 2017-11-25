package com.shawn.mapapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.shawn.mapapp.database.Constants;
import com.shawn.mapapp.database.MyDatabase;
import com.shawn.mapapp.database.MyHelper;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView searchView;
    private MyDatabase db;
    private SearchItemAdapter searchAdapter;
    private Cursor result;

    private EditText searchBar;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db = new MyDatabase(this);

        searchView = (RecyclerView) findViewById(R.id.rvItems);

        intent = getIntent();
        if (intent.hasExtra("username")) {
            result = db.getData(intent.getExtras().getString("username"));
            if (result.getCount() == 0) {
                Toast.makeText(this, "There are no locations saved.", Toast.LENGTH_SHORT).show();
            }
        }

        searchAdapter = new SearchItemAdapter(result);
        searchView.setAdapter(searchAdapter);

        searchBar = (EditText) findViewById(R.id.searchEditText);
    }

    public void search(View view) {
        String location = searchBar.getText().toString();
        if (location.length() > 0) {
            result = db.getSelectedData(intent.getExtras().getString("username"), location);
            if (result.getCount() == 0) {
                Toast.makeText(this, "No results found.", Toast.LENGTH_SHORT).show();
            }
            searchAdapter.cursor = result;
            searchAdapter.notifyDataSetChanged();
        }
    }

    // TODO: Add on click listener to recycler view items (goes back to map)
    //    @Override
    //    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    //        LinearLayout clickedRow = (LinearLayout) view;
    //        TextView plantNameTextView = (TextView) view.findViewById(R.id.plantNameEntry);
    //        TextView plantTypeTextView = (TextView) view.findViewById(R.id.plantTypeEntry);
    //        Toast.makeText(this, "row " + (1+position) + ":  " + plantNameTextView.getText() +" "+plantTypeTextView.getText(), Toast.LENGTH_LONG).show();
    //    }
}