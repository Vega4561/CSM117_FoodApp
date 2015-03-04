package com.csm117.foodapp;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

/**
 * Created by Jose Vega on 2/28/2015.
 */
public class ImageViewTracker {

    ImageView imageView;
    JSONArray images;
    int index;

    ImageViewTracker(ImageView imgView, JSONArray imgs){
        imageView = imgView;
        images = imgs;
        index = 0;
    }

    public int getIndex(){
        return index;
    }

    public boolean incIndex(){
        if(getIndex() + 1 >= images.length())
            return false;
        else{
            index ++;
            return true;
        }
    }

    public boolean decIndex(){
        if(getIndex() - 1 < 0)
            return false;
        else{
            index --;
            return true;
        }
    }

    public boolean imageUp(){
        if(incIndex()){
            Picasso.with(imageView.getContext()).load(images.optString(getIndex()))
                    .placeholder(R.drawable.loading).into(imageView);
            return true;
        }
        return false;
    }

    public boolean imageDown(){
        if(decIndex()){
            Picasso.with(imageView.getContext()).load(images.optString(getIndex()))
                    .placeholder(R.drawable.loading).into(imageView);
            return true;
        }
        return false;
    }
}
