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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    Button acceptButton;

    // For test
    JSONObject steak_bowl = new JSONObject();
    JSONObject sushi = new JSONObject();
    JSONObject blank = new JSONObject();
    JSONArray GalImages;
    Intent browserIntent;
    boolean urlAvailable;
    TextView infoTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        acceptButton = (Button) findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(this);
        infoTextView = (TextView) findViewById(R.id.info_textview);
        infoTextView.setTextSize(20);

        Drawable d = acceptButton.getBackground();
        PorterDuffColorFilter filter = new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
        d.setColorFilter(filter);
        acceptButton.setTextColor(Color.parseColor("white"));
        // Test data setup

        try {
            steak_bowl.put("img_url", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBKJ2dWOpjxA4q-yZHulrmqWLWkceCcOyrwxSDCNAF54UsYF_zug");
            steak_bowl.put("dish_name", "Teriyaki steak bowl");
            steak_bowl.put("restaurant", "Gushi");
            steak_bowl.put("url", "http://gushi.menu");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            sushi.put("img_url", "http://s3-media3.fl.yelpcdn.com/bphoto/1XoBXYs2X0MqtWzpsN3HDg/l.jpg");
            sushi.put("dish_name", "Sandra Roll");
            sushi.put("restaurant", "Yamato");
            sushi.put("url", "http://www.yelp.com/biz/yamato-restaurant-los-angeles");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            blank.put("img_url", "http://img2.wikia.nocookie.net/__cb20130511180903/legendmarielu/images/b/b4/No_image_available.jpg");
            blank.put("dish_name", "Blank entry (test)");
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
                // Check if this is the page you want.
                JSONObject json = GalImages.optJSONObject(position);
                TextView textView = (TextView) findViewById(R.id.info_textview);
                textView.setText(json.optString("dish_name")
                        + " - " + json.optString("restaurant"));
                if (GalImages.optJSONObject(position).has("url")) {
                    browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(json.optString("url")));
                    urlAvailable = true;
                }
                else
                    urlAvailable = false;
            }
        });

        TextView infoTextView = (TextView) findViewById(R.id.info_textview);
        ImageAdapter adapter = new ImageAdapter(this, GalImages, infoTextView);
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        if(urlAvailable){
            startActivity(browserIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
