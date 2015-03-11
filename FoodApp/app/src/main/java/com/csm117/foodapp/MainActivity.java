package com.csm117.foodapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;

import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    public final static String TAG = "MainActivity";

    Button acceptButton;
    Button callButton;
	Button navButton;

    JSONArray GalImages;
    Intent browserIntent;
    Intent callIntent;
    Intent moreInfoIntent;
	Intent mapIntent;
    boolean urlAvailable;
    boolean phoneAvailable;
	boolean coordAvailable;
    TextView infoTextView;
    public final static String EXTRA_MESSAGE = "com.csm117.foodapp.MESSAGE";

    String DEFAULT_LOCATION = "34.666,-118.666";
    private String lat;
    private String lng;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle coords = getIntent().getExtras();
        lat = coords.getString("LATITUDE");
        lng = coords.getString("LONGITUDE");
        //DEFAULT_LOCATION = coords.getString("DEFAULT_LOCATION");
        DEFAULT_LOCATION = lat + "," + lng;
        DEFAULT_LOCATION = coords.getString("DEFAULT_LOCATION");
        //Toast.makeText(
         //   getApplicationContext(), "Obtained\n\t" + DEFAULT_LOCATION, Toast.LENGTH_SHORT).show();

        Log.i(TAG, "In MainActivity \nDEFAULT_LOCATION: " + DEFAULT_LOCATION);

        Intent placeIntent = getIntent();
        moreInfoIntent = new Intent(this, MoreInfo.class);

        String placesQueryJSON = placeIntent.getStringExtra(PlacesActivity.EXTRA_MESSAGE);
        //placesQueryJSON = placesQueryJSON.replace("\\", "");
        Log.d("JSON String:", placesQueryJSON);

        acceptButton = (Button) findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(this);
        callButton = (Button) findViewById(R.id.call_button);
        callButton.setOnClickListener(this);
		navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        infoTextView = (TextView) findViewById(R.id.info_textview);
        infoTextView.setTextSize(20);

        Drawable d = acceptButton.getBackground();
        PorterDuffColorFilter filter = new PorterDuffColorFilter(0xffB30086, PorterDuff.Mode.SRC_ATOP);
        d.setColorFilter(filter);
        acceptButton.setTextColor(Color.parseColor("white"));

        Drawable dc = callButton.getBackground();
        PorterDuffColorFilter filterDc = new PorterDuffColorFilter(0xffB3002D, PorterDuff.Mode.SRC_ATOP);
        dc.setColorFilter(filterDc);
        callButton.setTextColor(Color.parseColor("white"));

        try {
            GalImages = new JSONArray(placesQueryJSON);
        }
        catch (Throwable t){
            Log.e("Food App", "Could not parse malformed JSON: \"" + placesQueryJSON + "\"");
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setBackgroundColor(Color.DKGRAY);

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                // Updates buttons when you change to a different restaurant view
                String openStatus;
                JSONObject json = GalImages.optJSONObject(position);
                moreInfoIntent.putExtra(EXTRA_MESSAGE, json.toString());
                if(json.has("open?")){
                    if(json.optString("open?").equals("true"))
                        openStatus = "Currently open.";
                    else if(json.optString("open?").equals("false"))
                        openStatus = "Currently closed.";
                    else openStatus = "";
                }
                else
                    openStatus = "";

                TextView textView = (TextView) findViewById(R.id.info_textview);
                textView.setText(json.optString("name") + "\n" + openStatus);
                if(json.has("location")){
                    // Get coordinates
                    lat = json.optJSONObject("location").optString("lat");
                    lng = json.optJSONObject("location").optString("lng");
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lng);
                    mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    coordAvailable = true;
                }
                else
                    coordAvailable = false;
                /*
                if (json.has("url")) {
                    browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(json.optString("url")));
                    urlAvailable = true;
                }
                else
                    urlAvailable = false;*/
                if (json.has("phone_number")) {
                    callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + json.optString("phone_number").trim()));
                    phoneAvailable = true;
                }
                else
                    phoneAvailable = false;
            }
        });

        TextView infoTextView = (TextView) findViewById(R.id.info_textview);
        ImageAdapter adapter = new ImageAdapter(this, GalImages, infoTextView);
        viewPager.setAdapter(adapter);

        // Initial button setup
        JSONObject json = GalImages.optJSONObject(0);

        moreInfoIntent.putExtra(EXTRA_MESSAGE, json.toString());
        if(json.has("location")){
            // Get coordinates
            lat = json.optJSONObject("location").optString("lat");
            lng = json.optJSONObject("location").optString("lng");
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lng);
                    mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    coordAvailable = true;
                }
                else
                    coordAvailable = false;
        /*
        if (json.has("url")) {
            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(json.optString("url")));
            urlAvailable = true;
        }
        else
            urlAvailable = false;*/
        if (json.has("phone_number")) {
            callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + json.optString("phone_number").trim()));
            phoneAvailable = true;
        }
        else
            phoneAvailable = false;

    }

    @Override
    public void onClick(View v) {
        String buttonName = ((Button) v).getText().toString();
        if(buttonName.equals("More Info") /*&& urlAvailable*/){
            //startActivity(browserIntent);
            startActivity(moreInfoIntent);
        }
        if(buttonName.equals("Call") && phoneAvailable){
            startActivity(callIntent);
        }
		if(buttonName.equals("Navigation") && coordAvailable){
            startActivity(mapIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;

        switch (item.getItemId())
        {
            case R.id.action_search:
                Toast.makeText(this, "Opening Search Dialog...", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_locate:
                Toast.makeText(this, "Acquiring Location...", Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, LocationActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_map:
                Toast.makeText(this, "Opening Map...", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, MapsActivity.class);
                intent.putExtra("LATITUDE", lat);
                intent.putExtra("LONGITUDE", lng);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                Toast.makeText(this, "Opening Settings...", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_about:
                Toast.makeText(this, "About the application...", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
