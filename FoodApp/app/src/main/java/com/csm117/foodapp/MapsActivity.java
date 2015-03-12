package com.csm117.foodapp;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends Activity {

    final String API_KEY = "AIzaSyB-XwRDBBbolc5Xo7HAz5tCTcWsqJAj_bg";
    private GoogleMap mMap; // null if Google Play not available.
    int PROXIMITY_RADIUS = 1000;
    private LatLng LOCATION_ME;
    String foodType = "restaurant";
    double latitude = 0;
    double longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }

        run();
    }

    public void run() {
        String type = foodType;
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=").append(type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + API_KEY);

        PlacesRead readPlaces = new PlacesRead();
        Object[] toPass = new Object[2];
        toPass[0] = mMap;
        toPass[1] = googlePlacesUrl.toString();
        readPlaces.execute(toPass);
    }

    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LOCATION_ME = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LOCATION_ME));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    public void onClick_City(View v) {
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_ME, 12);
        mMap.animateCamera(update);
    }
    public void onClick_Neighborhood(View v) {
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_ME, 14);
        mMap.animateCamera(update);
    }
    public void onClick_Street(View v) {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_ME, 16);
        mMap.animateCamera(update);
    }

    public class HTTP
    {
        public String read(String httpUrl) throws IOException {
            String strHTTPData = "";
            InputStream inStrm = null;
            HttpURLConnection conn = null;
            try {
                URL url = new URL(httpUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                inStrm = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStrm));
                StringBuilder strBuff = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    strBuff.append(line);
                }
                strHTTPData = strBuff.toString();
                bufferedReader.close();
            } catch (Exception e) {
                Log.d("Exception reading URL", e.toString());
            } finally {
                assert inStrm != null;
                inStrm.close();
                conn.disconnect();
            }
            return strHTTPData;
        }
    }

    public class PlacesRead extends AsyncTask<Object, Integer, String> {
        String strPlaces = null;
        GoogleMap googleMap;

        @Override
        protected String doInBackground(Object... inputObj) {
            try {
                googleMap = (GoogleMap) inputObj[0];
                String placesURL = (String) inputObj[1];
                HTTP http = new HTTP();
                strPlaces = http.read(placesURL);
            } catch (Exception e) {
                Log.d("Places Read", e.toString());
            }
            return strPlaces;
        }

        @Override
        protected void onPostExecute(String result) {
            DisplayOnMap displayPlacesOnMap = new DisplayOnMap();
            Object[] obj = new Object[2];
            obj[0] = googleMap;
            obj[1] = result;
            displayPlacesOnMap.execute(obj);
        }
    }

    public class PlacesMap {
        public List<HashMap<String, String>> parse(JSONObject jsonObject) {
            JSONArray arrJSON = null;
            try {
                arrJSON = jsonObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return getPlaces(arrJSON);
        }

        private List<HashMap<String, String>> getPlaces(JSONArray arrJSON) {
            int placesCnt = arrJSON.length();
            List<HashMap<String, String>> placesLst = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> placeMap = null;

            for (int i = 0; i < placesCnt; i++) {
                try {
                    placeMap = getPlace((JSONObject) arrJSON.get(i));
                    placesLst.add(placeMap);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return placesLst;
        }

        private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
            HashMap<String, String> placesMap = new HashMap<>();
            String placeName = "-NA-";
            String addr = "-NA-";
            String latitude;
            String longitude;
            String ref;

            try {
                if (!googlePlaceJson.isNull("name")) {
                    placeName = googlePlaceJson.getString("name");
                }
                if (!googlePlaceJson.isNull("vicinity")) {
                    addr = googlePlaceJson.getString("vicinity");
                }
                latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
                ref = googlePlaceJson.getString("reference");
                placesMap.put("place_name", placeName);
                placesMap.put("vicinity", addr);
                placesMap.put("lat", latitude);
                placesMap.put("lng", longitude);
                placesMap.put("reference", ref);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return placesMap;
        }
    }

    public class DisplayOnMap
            extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {
        JSONObject placesJSON;
        GoogleMap googleMap;

        @Override
        protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

            List<HashMap<String, String>> placesLst = null;
            PlacesMap placeJsonParser = new PlacesMap();

            try {
                googleMap = (GoogleMap) inputObj[0];
                placesJSON = new JSONObject((String) inputObj[1]);
                placesLst = placeJsonParser.parse(placesJSON);
            } catch (Exception e) {
                Log.d("Exception DisplayOnMap", e.toString());
            }
            return placesLst;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {
            googleMap.clear();
            for (int i = 0; i < list.size(); i++) {
                MarkerOptions markerOpt = new MarkerOptions();
                HashMap<String, String> googlePlace = list.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String restaurantName = googlePlace.get("place_name");
                String addr = googlePlace.get("vicinity");
                LatLng latLng = new LatLng(lat, lng);
                markerOpt.position(latLng);
                markerOpt.title(restaurantName + " : " + addr);
                googleMap.addMarker(markerOpt);
            }
        }
    }

}

