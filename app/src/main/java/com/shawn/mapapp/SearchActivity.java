package com.shawn.mapapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

        searchAdapter = new SearchItemAdapter(result, this, intent.getExtras().getString("username"));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mapMenu:
                Intent oldIntent = getIntent();
                intent = new Intent(this, MapsActivity.class);
                intent.putExtra("username", oldIntent.getExtras().getString("username"));
                startActivity(intent);
                return true;
            case R.id.logoutMenu:
                intent = new Intent(this, MainActivity.class);
                // Stop user from going back in activity stack
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}