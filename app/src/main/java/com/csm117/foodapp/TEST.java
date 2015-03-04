package com.csm117.foodapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TEST extends ActionBarActivity implements View.OnClickListener {

    Button btnGetLocation;
    Button btnRunQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        btnGetLocation = (Button) findViewById(R.id.btnLocation);
        btnRunQuery = (Button) findViewById(R.id.btnQuery);

        btnGetLocation.setOnClickListener(this);
        btnRunQuery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.btnLocation:
                Toast.makeText(this, "Locating...", Toast.LENGTH_SHORT).show();
                intent = new Intent(TEST.this, LocationActivity.class);
                startActivity(intent);
                return;
            case R.id.btnQuery:
                Toast.makeText(this, "Querying...", Toast.LENGTH_SHORT).show();
                intent = new Intent(TEST.this, YelpActivity.class);
                startActivity(intent);
                return;
            default:
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
