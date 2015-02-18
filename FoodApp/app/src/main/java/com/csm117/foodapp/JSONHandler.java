package com.csm117.foodapp;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Jose Vega on 2/16/2015.
 */
public class JSONHandler {
    JSONArray jsonArray;
    int cursor;

    public JSONHandler(JSONArray jarray){
        jsonArray = jarray;
        cursor = 0;
    }

    public JSONObject getItem (int position){
        return jsonArray.optJSONObject(position);
    }

    public int getCount(){
        return jsonArray.length();
    }

    public JSONObject getNext(){
        if (cursor >= this.getCount()){
            cursor = 0;
        }
        JSONObject r = getItem(cursor);
        cursor ++;
        return r;
    }
}
