package com.csm117.foodapp;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jose Vega on 2/21/2015.
 */
public class ImageAdapter extends PagerAdapter {
    Context context;
    private JSONArray GalImages = new JSONArray ();
    TextView subText;
    JSONObject blank = new JSONObject();


    ImageAdapter(Context context, JSONArray URLs, TextView textView){
        this.context = context;
        subText = textView;
        if(URLs.length() > 0)
            GalImages = URLs;

        else {
            JSONArray blankImgs = new JSONArray();
            blankImgs.put("http://img2.wikia.nocookie.net/__cb20130511180903/legendmarielu/images/b/b4/No_image_available.jpg");
            try {
                blank.put("photoUrls", blankImgs);
                //blank.put("dish_name", "Blank entry (test)");
                blank.put("name", "Blank restaurant (test)");
                GalImages.put(blank);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String openStatus;
        JSONObject json = GalImages.optJSONObject(0);

        if(json.has("open?")){
            if(json.optString("open?").equals("true"))
                openStatus = "Currently open.";
            else if(json.optString("open?").equals("false"))
                openStatus = "Currently closed.";
            else openStatus = "";
        }
        else{
            openStatus = "";
        }

        textView.setText(json.optString("name") + "\n" + openStatus);
    }
    @Override
    public int getCount() {
        return GalImages.length();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.padding_medium);
        imageView.setPadding(padding, padding, padding, padding);
        String imgURL;
        if(GalImages.optJSONObject(position).has("photoUrls") && GalImages.optJSONObject(position).optJSONArray("photoUrls") != null){
            if (GalImages.optJSONObject(position).optJSONArray("photoUrls").length() > 0) {
                imgURL = GalImages.optJSONObject(position).optJSONArray("photoUrls").optString(0);
                // Swipe detector for additional images
                ImageViewTracker ivt = new ImageViewTracker(imageView, GalImages.optJSONObject(position).optJSONArray("photoUrls"));
                TouchListener touchListener = new TouchListener(context, ivt);
                imageView.setOnTouchListener(touchListener);
            }
            else
                imgURL = "http://img2.wikia.nocookie.net/__cb20130511180903/legendmarielu/images/b/b4/No_image_available.jpg";
        }
        else
            imgURL = "http://img2.wikia.nocookie.net/__cb20130511180903/legendmarielu/images/b/b4/No_image_available.jpg";
        Picasso.with(imageView.getContext()).load(imgURL)
                .placeholder(R.drawable.loading).into(imageView);

        ((ViewPager) container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }

}