package com.mj.minhajlib.trackme.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.mj.minhajlib.trackme.utils.Utils;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Minhaj lib on 8/3/2017.
 */

public class MyService extends Service implements LocationListener {

    private final int MIN_TIME = 1000 * 3;
    private final int MIN_DISTANCE = 1;
    private LocationManager mLocationManager;
    private String mGpsProvider;
    private Utils mUtils;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("tracker","service started");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        mLocationManager.requestLocationUpdates(mGpsProvider, MIN_TIME, MIN_DISTANCE, this);
        mUtils.setPref(true);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("tracker","service created");
        mUtils = new Utils(getApplicationContext());
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            mGpsProvider = LocationManager.NETWORK_PROVIDER;
        }
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            mGpsProvider = LocationManager.GPS_PROVIDER;
        }
        Log.d("tracker",mGpsProvider);
    }

    @Override
    public void onDestroy() {
        Log.d("tracker","service destroyed");
        mLocationManager.removeUpdates(this);
        mUtils.setPref(false);
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("tracker","location changed");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d("tracker",s+" enabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d("tracker",s+" disabled");
    }
}