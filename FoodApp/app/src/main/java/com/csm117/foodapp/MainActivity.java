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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    Button acceptButton;
    Button callButton;


    JSONArray GalImages;
    Intent browserIntent;
    Intent callIntent;
    Intent moreInfoIntent;
    boolean urlAvailable;
    boolean phoneAvailable;
    TextView infoTextView;
    public final static String EXTRA_MESSAGE = "com.csm117.foodapp.MESSAGE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent placeIntent = getIntent();
        moreInfoIntent = new Intent(this, MoreInfo.class);
        /*
        Query JSON Format:
        Array of JSON objects, representing individual restaurants
        Each Object has..
        "icon" - icon for restaurant
        "name" - name of restaurant
        "open?" - (bool) is this restaurant open
        "location" - json object containing "lat" and "long" keys
        "photoUrls" - array of urls (string) to restaurant photos
         */
        String placesQueryJSON = placeIntent.getStringExtra(PlacesActivity.EXTRA_MESSAGE);
        //placesQueryJSON = placesQueryJSON.replace("\\", "");
        Log.d("JSON String:", placesQueryJSON);

        acceptButton = (Button) findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(this);
        callButton = (Button) findViewById(R.id.call_button);
        callButton.setOnClickListener(this);
        infoTextView = (TextView) findViewById(R.id.info_textview);
        infoTextView.setTextSize(20);

        Drawable d = acceptButton.getBackground();
        PorterDuffColorFilter filter = new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
        d.setColorFilter(filter);
        acceptButton.setTextColor(Color.parseColor("white"));

        Drawable dc = callButton.getBackground();
        PorterDuffColorFilter filterDc = new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        dc.setColorFilter(filterDc);
        callButton.setTextColor(Color.parseColor("white"));

        try {
            GalImages = new JSONArray(placesQueryJSON);
        }
        catch (Throwable t){
            Log.e("Food App", "Could not parse malformed JSON: \"" + placesQueryJSON + "\"");
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);


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
                    else
                        openStatus = "Currently closed.";
                }
                else{
                    openStatus = "";
                }

                TextView textView = (TextView) findViewById(R.id.info_textview);
                textView.setText("Restaurant: " + json.optString("name") + ".\n" + openStatus);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
