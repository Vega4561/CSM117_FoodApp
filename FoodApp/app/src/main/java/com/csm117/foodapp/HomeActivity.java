package com.csm117.foodapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.maps.model.LatLng;

public class HomeActivity extends ActionBarActivity
        implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public LocationClient mLocationClient;
    public Location mCurrentLocation;
    public LocationManager locationManager;
    public LocationListener locationListener;
    public Location lastGpsLocation;

    private TextView mAddress;
    private TextView tvAddr;
    private ProgressBar progressBar;

    double latitude;
    double longitude;

    boolean connectedSuccessfully = false; //used to start PlacesActivity instead of clicking

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAddress = (TextView) findViewById(R.id.address);
        mAddress.setText("Attempting to acquire coordinates...");

        tvAddr = (TextView) findViewById(R.id.tv_gettingLocation);
        progressBar = (ProgressBar) findViewById(R.id.progBar);

        mAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLocation();
            }
        });
        mLocationClient = new LocationClient();

        //progressBar.setVisibility(View.VISIBLE);

        View.OnClickListener fabClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLocation == null) {
                    //Toast.makeText(getApplicationContext(), "Unable to get location.", Toast.LENGTH_LONG).show();
                    mAddress.setText("Unable to get location.");
                    //progressBar.setVisibility(View.VISIBLE);

                    //refreshLocation(true);
                } else {
                    tvAddr.setVisibility(View.VISIBLE);
                    tvAddr.setText("Coordinates:\n" + mCurrentLocation.getLatitude() + "\n" + mCurrentLocation.getLongitude());
                    mAddress.setText("Coordinates:\n" + mCurrentLocation.getLatitude() + "\n" + mCurrentLocation.getLongitude());
                    //progressBar.setVisibility(View.INVISIBLE);
                    //onDisconnected();
                }
            }
        };

        //Button btnGetSome = (Button) findViewById(R.id.btnSendQuery);
        //btnGetSome.setOnClickListener(fabClickListener);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mCurrentLocation = location;
                lastGpsLocation = location;
                mAddress.setText("New Location: " + mCurrentLocation);
                refreshLocation();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mLocationClient.connect();
        mAddress.setText("Connecting...");
        System.out.println("Connecting...");
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        mLocationClient.disconnect();
        locationManager.removeUpdates(locationListener);

        mAddress.setText("Disconnecting...");
        System.out.println("Disconnecting...");
        progressBar.setVisibility(View.INVISIBLE);
        super.onStop();
    }

    public void refreshLocation() {

        System.out.println("Refresh...");

        if (mCurrentLocation == null) {
            System.out.println("CurrentLocation = null...");

            if (mLocationClient.getLastLocation() == null) {
                System.out.println("LastLocation = null...");
                EnableLocationDialogFragment frag = new EnableLocationDialogFragment();
                frag.show(getFragmentManager(), "EnableLocationDialogFragment");

                mAddress.setText("Error");

            } else {
                System.out.println("Using LastLocation..." + mLocationClient.getLastLocation());
                mCurrentLocation = mLocationClient.getLastLocation();
                mAddress.setText("Currently: " + mLocationClient.getLastLocation());
                refreshLocation();
            }

        } else {
            System.out.println("Check if Geocoder is available...");

            if (Geocoder.isPresent()) {

                System.out.println("Geocoder is available...");
                System.out.println("TEEHEEE " + mCurrentLocation);
            }

            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();

            LatLng position = new LatLng(latitude, longitude);
            mAddress.setText("Current Position: " + position);
            System.out.println("Current Position: " + position);

            if ((latitude == 0.0) || (longitude == 0.0)) {
                Toast.makeText(
                        getApplicationContext(), "Unable to get accurate coordinates.\n\tPlease, try again.",
                        Toast.LENGTH_LONG).show();
            }
            else {
                //Toast.makeText(
                //        getApplicationContext(), "Acquired coordinates\n\tLatitude: " + latitude
                //                + "\n\tLongitude: " + longitude,
                //        Toast.LENGTH_LONG).show();

                startYelpQuery();
            }
        }
    }

    public static class EnableLocationDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            System.out.println("Creating EnableLocation Dialog...");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Location update failed, please enable GPS.")
                    .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.out.println("Clicked Enable GPS...");
                            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            System.out.println("Clicked Dismiss...");
                            dialog.dismiss();
                        }
                    });
            return builder.create();
        }
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        System.out.println("onConnected...LastLocation = " + mLocationClient.getLastLocation());
        mCurrentLocation = mLocationClient.getLastLocation();
        mAddress.setText("Connected from: " + mCurrentLocation);
        refreshLocation();

        connectedSuccessfully = true;
    }

    @Override
    public void onDisconnected() {
        // Display the connection status
        System.out.println("Disconnected again...");
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
        mAddress.setText("Disconnected!!");
        connectedSuccessfully = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("Connection Failed, dog....");

        if (connectionResult.hasResolution()) {
            System.out.println("Check if failure is resolvable...");
            try {
                connectionResult.startResolutionForResult(
                        this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Not resolvable.. booo.");
            Toast.makeText(this, "Failed to connect", Toast.LENGTH_SHORT).show();
            mAddress.setText("Connection failed. Unresolvable.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    public void startYelpQuery(){
        Intent intent = new Intent(this, PlacesActivity.class);
        intent.putExtra("LATITUDE", Double.toString(latitude));
        intent.putExtra("LONGITUDE", Double.toString(longitude));
        startActivity(intent);
        finish();
    }
}