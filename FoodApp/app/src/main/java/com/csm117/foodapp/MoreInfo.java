package com.csm117.foodapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class MoreInfo extends ActionBarActivity {
    ListView mainListView;
    ArrayAdapter mArrayAdapter;
    ArrayList mNameList = new ArrayList();
    JSONObject restJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        Intent intent = getIntent();
        String placeQueryJSON = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = (TextView) findViewById(R.id.main_textview);
        textView.setText("Additional Restaurant Information");
        //setContentView(textView);
        mainListView = (ListView) findViewById(R.id.main_listview);
        mArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                mNameList);
        mainListView.setAdapter(mArrayAdapter);

        try {
            restJson = new JSONObject(placeQueryJSON);
        }
        catch (Throwable t){
            Log.e("Food App", "Could not parse malformed JSON: \"" + placeQueryJSON + "\"");
        }

        mNameList.add("Restaurant Name: " + restJson.optString("name"));
        String open = restJson.optString("open?");
        if(open.equals("true")){
            mNameList.add("Currently open for business.");
        }
        else
            mNameList.add("Currently closed.");
        mNameList.add("Address: " + restJson.optString("address"));
        mNameList.add("Phone Number: " + restJson.optString("phone_number"));

        mArrayAdapter.notifyDataSetChanged();

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
