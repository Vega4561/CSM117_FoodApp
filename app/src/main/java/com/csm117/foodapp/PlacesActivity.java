package com.csm117.foodapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class PlacesActivity extends ActionBarActivity {

    public final static String TAG = "PlacesActivity";

    private final String API_KEY = "AIzaSyApzOGdZ7s7U-BMdc90aZqMnwrZWxctMwU";
    private String DEFAULT_LOCATION = "34.0722, -118.4441";
    private final String DEFAULT_RADIUS = "1000";
    private final String PREFERED_IMG_WIDTH = "200";
    public final static String EXTRA_MESSAGE = "com.csm117.foodapp.MESSAGE";

    public void startDisplayActivity(JSONArray ja){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MESSAGE, ja.toString());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        Bundle coords = getIntent().getExtras();
        String lat = coords.getString("LATITUDE");
        String lng = coords.getString("LONGITUDE");
        Toast.makeText(
                getApplicationContext(), "Obtained\n\tLatitude: " + lat
                        + "\n\tLongitude: " + lng, Toast.LENGTH_SHORT).show();
        DEFAULT_LOCATION = lat + "," + lng;

        Log.i(TAG, "in PlacesActivity \nDEFAULT_LOCATION: " + DEFAULT_LOCATION);

        new Thread(new Runnable() {
            public void run() {
                Uri b = null;
                HttpURLConnection conn = null;
                try{
                    b = new Uri.Builder()
                            .scheme("https")
                            .authority("maps.googleapis.com")
                            .path("/maps/api/place/nearbysearch/json")
                            .appendQueryParameter("location", DEFAULT_LOCATION)
                            .appendQueryParameter("radius", DEFAULT_RADIUS)
                            .appendQueryParameter("types", "food")
                            .appendQueryParameter("key", API_KEY)
                            .build();
                    URL url = new URL(b.toString());

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept-Charset", "UTF-8");
                    InputStream is = conn.getInputStream();
                    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                    String response =  s.hasNext() ? s.next() : "";

                    JSONArray restaurants = new JSONArray();
                    JSONObject respJson = new JSONObject(response);
                    if(((String)respJson.get("status")).equals("OK")){
                        JSONArray results = respJson.getJSONArray("results");
                        for(int i = 0; i < results.length(); i++){
                            //extract relevant data from the places API's json
                            JSONObject rest  = results.getJSONObject(i);
                            JSONObject myRestData = new JSONObject();
                            myRestData.put("icon", rest.get("icon"));
                            myRestData.put("name", rest.get("name"));
                            myRestData.put("open?", ((JSONObject)rest.get("opening_hours")).get("open_now"));
                            myRestData.put("location", ((JSONObject) rest.get("geometry")).get("location"));
                            JSONArray photos = null;
                            ArrayList<String> photoUrls = new ArrayList<String>();
                            try {
                                photos = rest.getJSONArray("photos");

                                for(int j = 0; j < photos.length(); j++){
                                    String ref = constructPhotoUrl((String) photos.getJSONObject(j).get("photo_reference"));
                                    photoUrls.add(ref);
                                }
                            }
                            catch(org.json.JSONException e){
                            }
                            myRestData.put("photoUrls", photoUrls);
                            restaurants.put(myRestData);
                        }
                    }
                    else{
                        //error handle here
                    }
                    //send data to mainactivity


                    Log.i(TAG, "in PlacesActivity \nrestaurants: " + restaurants);

                    startDisplayActivity(restaurants);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private String constructPhotoUrl(String photoref){
        //URI build : api key + photo ref + max width
        Uri photo_uri = new Uri.Builder()
                .scheme("https")
                .authority("maps.googleapis.com")
                .path("/maps/api/place/photo")
                .appendQueryParameter("key", API_KEY)
                .appendQueryParameter("photoreference", photoref)
                .appendQueryParameter("maxwidth", PREFERED_IMG_WIDTH)
                .build();
        return photo_uri.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_places, menu);
        return true;
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
