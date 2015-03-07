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

    // For test
    JSONObject steak_bowl = new JSONObject();
    JSONObject sushi = new JSONObject();
    JSONObject blank = new JSONObject();
    JSONArray steakImgs = new JSONArray();
    JSONArray sushiImgs = new JSONArray();
    JSONArray blankImgs = new JSONArray();

    JSONArray GalImages;
    //JSONArray TestImages;
    Intent browserIntent;
    Intent callIntent;
    boolean urlAvailable;
    boolean phoneAvailable;
    TextView infoTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
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
        String placesQueryJSON = intent.getStringExtra(PlacesActivity.EXTRA_MESSAGE);
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
        // Test data setup
        /*
        steakImgs.put("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBKJ2dWOpjxA4q-yZHulrmqWLWkceCcOyrwxSDCNAF54UsYF_zug");
        steakImgs.put("https://maps.googleapis.com/maps/api/place/photo?key=AIzaSyApzOGdZ7s7U-BMdc90aZqMnwrZWxctMwU&photoreference=CnRnAAAA_10uiYflyBTq4vkcouxtEFBgEs_DU" +
                "lneTogs1cjnA9SWajnLaNErpHEjIOV9IpBE2ZbJ_4ppIvJuUFDi8IW5D9S7AdpeSUYJLHsqV24oHOpIU1DEGecur9PBOWdEUxIAMYVicl5Gx364CC7htC_rLBIQhKfbm0LMPRnaFdAx2WZKZRoUslNK2" +
                "FWctkdw-npqJ-joJdBtMnM&maxwidth=200");
        steakImgs.put("http://s3-media4.fl.yelpcdn.com/bphoto/-Qt7jSghOrJu9rRTOPNb9w/348s.jpg");
        sushiImgs.put("http://s3-media3.fl.yelpcdn.com/bphoto/1XoBXYs2X0MqtWzpsN3HDg/l.jpg");
        sushiImgs.put("http://unvegan.com/wp-content/uploads/2010/01/IMG_2784.jpg");
        //blankImgs.put("http://img2.wikia.nocookie.net/__cb20130511180903/legendmarielu/images/b/b4/No_image_available.jpg");


        try {
            steak_bowl.put("photoUrls", steakImgs);
           // steak_bowl.put("dish_name", "Teriyaki steak bowl");
            steak_bowl.put("name", "Gushi");
            steak_bowl.put("url", "http://gushi.menu");
            steak_bowl.put("phone", "310-208-4038");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            sushi.put("photoUrls", sushiImgs);
            //sushi.put("dish_name", "Sandra Roll");
            sushi.put("name", "Yamato");
            sushi.put("url", "http://www.yelp.com/biz/yamato-restaurant-los-angeles");
            sushi.put("phone", "310-208-0100");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            blank.put("photoUrls", blankImgs);
            //blank.put("dish_name", "Blank entry (test)");
            blank.put("name", "Blank restaurant (test)");
        } catch (JSONException e) {
            e.printStackTrace();
        }/*
        GalImages = new JSONArray();
        GalImages.put(steak_bowl);
        GalImages.put(sushi);
        GalImages.put(blank);*/
        //JSONArray jsonImages = new JSONArray();
        try {
            GalImages = new JSONArray(placesQueryJSON);
        }
        catch (Throwable t){
            Log.e("Food App", "Could not parse malformed JSON: \"" + placesQueryJSON + "\"");
        }/*
        if(jsonImages.optJSONObject(0).has("photoUrls") && jsonImages.optJSONObject(0).optJSONArray("photoUrls") != null){
        Log.d("Food app", String.valueOf(jsonImages.optJSONObject(0).optJSONArray("photoUrls").length()));}
        if(jsonImages.optJSONObject(1).has("photoUrls") && jsonImages.optJSONObject(1).optJSONArray("photoUrls") != null){
            Log.d("Food app", String.valueOf(jsonImages.optJSONObject(1).optJSONArray("photoUrls").length()));}
        if(jsonImages.optJSONObject(2).has("photoUrls") && jsonImages.optJSONObject(2).optJSONArray("photoUrls") != null){
            Log.d("Food app", String.valueOf(jsonImages.optJSONObject(2).optJSONArray("photoUrls").length()));}
*/
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);


        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                // Updates buttons when you change to a different restaurant view
                String openStatus;
                JSONObject json = GalImages.optJSONObject(position);

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

                if (json.has("url")) {
                    browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(json.optString("url")));
                    urlAvailable = true;
                }
                else
                    urlAvailable = false;
                if (json.has("phone")) {
                    callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + json.optString("phone").trim()));
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

        if (json.has("url")) {
            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(json.optString("url")));
            urlAvailable = true;
        }
        else
            urlAvailable = false;
        if (json.has("phone")) {
            callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + json.optString("phone").trim()));
            phoneAvailable = true;
        }
        else
            phoneAvailable = false;

    }

    @Override
    public void onClick(View v) {
        String buttonName = ((Button) v).getText().toString();
        if(buttonName.equals("More Info") && urlAvailable){
            startActivity(browserIntent);
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
