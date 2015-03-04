package com.csm117.foodapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    Intent browserIntent;
    Intent callIntent;
    boolean urlAvailable;
    boolean phoneAvailable;
    TextView infoTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        steakImgs.put("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBKJ2dWOpjxA4q-yZHulrmqWLWkceCcOyrwxSDCNAF54UsYF_zug");
        steakImgs.put("http://images.laweekly.com/imager/b/original/2377213/7a5d/Kalbi_plate_Gushi.jpg");
        steakImgs.put("http://s3-media4.fl.yelpcdn.com/bphoto/-Qt7jSghOrJu9rRTOPNb9w/348s.jpg");
        sushiImgs.put("http://s3-media3.fl.yelpcdn.com/bphoto/1XoBXYs2X0MqtWzpsN3HDg/l.jpg");
        sushiImgs.put("http://unvegan.com/wp-content/uploads/2010/01/IMG_2784.jpg");
        blankImgs.put("http://img2.wikia.nocookie.net/__cb20130511180903/legendmarielu/images/b/b4/No_image_available.jpg");


        try {
            steak_bowl.put("img_urls", steakImgs);
           // steak_bowl.put("dish_name", "Teriyaki steak bowl");
            steak_bowl.put("restaurant", "Gushi");
            steak_bowl.put("url", "http://gushi.menu");
            steak_bowl.put("phone", "310-208-4038");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            sushi.put("img_urls", sushiImgs);
            //sushi.put("dish_name", "Sandra Roll");
            sushi.put("restaurant", "Yamato");
            sushi.put("url", "http://www.yelp.com/biz/yamato-restaurant-los-angeles");
            sushi.put("phone", "310-208-0100");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            blank.put("img_urls", blankImgs);
            //blank.put("dish_name", "Blank entry (test)");
            blank.put("restaurant", "Blank restaurant (test)");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GalImages = new JSONArray();
        GalImages.put(steak_bowl);
        GalImages.put(sushi);
        GalImages.put(blank);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);


        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                // Updates buttons when you change to a different restaurant view
                JSONObject json = GalImages.optJSONObject(position);
                TextView textView = (TextView) findViewById(R.id.info_textview);
                textView.setText("Restaurant: " + json.optString("restaurant"));
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
        Intent intent;

        switch (v.getId()) {
            case R.id.accept_button:
                Toast.makeText(this, "Accepting...", Toast.LENGTH_SHORT).show();
                startActivity(browserIntent);
                return;
            case R.id.call_button:
                Toast.makeText(this, "Calling...", Toast.LENGTH_SHORT).show();
                startActivity(callIntent);
                return;
            case R.id.btnLocation:
                Toast.makeText(this, "Locating...", Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, LocationActivity.class);
                startActivity(intent);
                return;
            case R.id.btnQuery:
                Toast.makeText(this, "Querying...", Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, YelpActivity.class);
                startActivity(intent);
                return;
            default:
        }
    }
/*    @Override
    public void onClick(View v) {
        String buttonName = ((Button) v).getText().toString();
        if(buttonName.equals("More Info") && urlAvailable){
            startActivity(browserIntent);
        }
        if(buttonName.equals("Call") && phoneAvailable){
            startActivity(callIntent);
        }
    }*/

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
            case R.id.action_test:
                Toast.makeText(this, "Testing time...", Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, TEST.class);
                startActivity(intent);
                return true;
            case R.id.action_search:
                Toast.makeText(this, "Opening Search Dialog...", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_locate:
                Toast.makeText(this, "Acquiring Location...", Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, LocationActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_map:
                Toast.makeText(this, "Opening Map...", Toast.LENGTH_SHORT).show();
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
