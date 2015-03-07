package com.csm117.foodapp;

import android.location.Location;

public class LocationClient {
    private Location lastLocation;

    public LocationClient() {}

    public Location getLastLocation() {
        return lastLocation;
    }

    public void connect() {}

    public void disconnect() {}
}