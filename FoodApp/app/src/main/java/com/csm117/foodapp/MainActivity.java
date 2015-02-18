package com.csm117.foodapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

    TextView mainTextView;
    TextView infoTextView;
    Button acceptButton;
    Button nextButton;
    JSONArray food_items;
    ImageView imageView;
    JSONHandler jsonHandler;
    Intent browserIntent;
    boolean urlAvailable = false;
    // For tests
    JSONObject steak_bowl = new JSONObject();
    JSONObject sushi = new JSONObject();
    JSONObject blank = new JSONObject();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Access the TextView defined in layout XML
        // and then set its text
        mainTextView = (TextView) findViewById(R.id.main_textview);
        mainTextView.setText("Find Local Food!");
        infoTextView = (TextView) findViewById(R.id.info_textview);
        infoTextView.setTextSize(20);

        // Access the Button defined in layout XML
        // and listen for it here
        imageView = (ImageView) findViewById(R.id.img_food);
        acceptButton = (Button) findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(this);
        nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);

        Drawable d = nextButton.getBackground();
        PorterDuffColorFilter filter = new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        d.setColorFilter(filter);

        d = acceptButton.getBackground();
        filter = new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
        d.setColorFilter(filter);

        // set button text colour to be blue
        nextButton.setTextColor(Color.parseColor("white"));
        acceptButton.setTextColor(Color.parseColor("white"));
        // Create placeholder food_items JSONArray

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
        food_items = new JSONArray();
        food_items.put(steak_bowl);
        food_items.put(sushi);
        food_items.put(blank);

        // Get first entry
        jsonHandler = new JSONHandler(food_items);
        JSONObject first = jsonHandler.getNext();
        Picasso.with(this).load(first.optString("img_url"))
                   .placeholder(R.drawable.no_image).into(imageView);
        infoTextView.setText(first.optString("restaurant") +
                ": " + first.optString("dish_name"));
        if (first.has("url")) {
            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(first.optString("url")));
            urlAvailable = true;
        }
        else
            urlAvailable = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        String buttonName = ((Button) v).getText().toString();
        mainTextView.setText(buttonName + " button pressed.");
        JSONObject next = jsonHandler.getNext();
        if(buttonName.equals("Next")){
            Picasso.with(this).load(next.optString("img_url"))
                    .placeholder(R.drawable.no_image).into(imageView);
            infoTextView.setText(next.optString("restaurant") +
                ": " + next.optString("dish_name"));
            if (next.has("url")) {
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(next.optString("url")));
                urlAvailable = true;
            }
            else
                urlAvailable = false;
        }
        if (buttonName.equals("Accept") && urlAvailable) {
            startActivity(browserIntent);
        }
    }
}
