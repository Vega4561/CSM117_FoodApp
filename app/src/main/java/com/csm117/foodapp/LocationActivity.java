package com.csm117.foodapp;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


public class LocationActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LocationActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient = null;
    private Location mLastLocation = null;
    private TextView mLongitudeText = null;
    private TextView mLatitudeText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mLongitudeText = (TextView) findViewById(R.id.mLongitudeText);
        mLatitudeText = (TextView) findViewById(R.id.mLatitudeText);

        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            Log.d(TAG, "comeonnn");
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location, menu);
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

    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        // The good stuff goes here.
        Log.d(TAG, "i actually connected");


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            String a = String.valueOf(mLastLocation.getLatitude());
            String b = String.valueOf(mLastLocation.getLongitude());
            Log.d(TAG, "Latitude: " + a);
            Log.d(TAG, "Longitude: " + b);
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
        else{
            mLatitudeText.setText("I suck");
            Log.d(TAG, "I'm null :(");
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        // More about this in the next section.
        Log.d(TAG, "I failed");
    }
}
