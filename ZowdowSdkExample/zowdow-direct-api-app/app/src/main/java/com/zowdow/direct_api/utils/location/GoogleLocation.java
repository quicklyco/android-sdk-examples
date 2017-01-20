package com.zowdow.direct_api.utils.location;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.zowdow.direct_api.utils.PermissionsUtils;

public class GoogleLocation {
    private static final int MIN_UPDATE_TIME = 10 * 60 * 1000; // 10 minutes
    private static final int MIN_UPDATE_DIST = 250; // 250 meters

    public static final String STATIONARY = "Stationary";
    public static final String WALKING    = "Walking";
    public static final String DRIVING    = "Driving";

    private static GoogleLocation sInstance;

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;
    private LocationListener mGoogleLocationListener;
    private Location mLocation;
    private DetectedActivity mDetectedActivity;

    private GoogleLocation() {
        mLocationRequest = createLocationRequest();
        mGoogleLocationListener = createGoogleLocationListener();
    }

    public static GoogleLocation getInstance() {
        synchronized (GoogleLocation.class) {
            if (sInstance == null) {
                sInstance = new GoogleLocation();
            }
            return sInstance;
        }
    }

    private synchronized GoogleApiClient createGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mGoogleLocationListener);

                        if (PermissionsUtils.checkPermission(context, "com.google.android.gms.permission.ACTIVITY_RECOGNITION")) {
                            PendingIntent activityRecognitionPendingIntent = PendingIntent.getService(context, 0, new Intent(context, ActivityRecognitionIntentService.class), PendingIntent.FLAG_UPDATE_CURRENT);
                            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, MIN_UPDATE_TIME, activityRecognitionPendingIntent);
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(MIN_UPDATE_TIME);
        locationRequest.setFastestInterval(MIN_UPDATE_TIME);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        return locationRequest;
    }

    private LocationListener createGoogleLocationListener() {
        return location -> mLocation = location;
    }

    void start(Context context) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = createGoogleApiClient(context);
        }
        if (!(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())) {
            mGoogleApiClient.connect();
        }
    }

    void stop() {
        if (mGoogleApiClient != null && (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mGoogleLocationListener);
            mGoogleApiClient.disconnect();
        }
    }

    public synchronized void setDetectedActivity(DetectedActivity detectedActivity) {
        mDetectedActivity = detectedActivity;
    }

    public synchronized String getDetectedActivity() {
        if (mDetectedActivity != null) {
            switch (mDetectedActivity.getType()) {
                case DetectedActivity.IN_VEHICLE:
                case DetectedActivity.ON_BICYCLE:
                    return DRIVING;
                case DetectedActivity.ON_FOOT:
                case DetectedActivity.RUNNING:
                case DetectedActivity.WALKING:
                    return WALKING;
                case DetectedActivity.STILL:
                default:
                    return STATIONARY;
            }
        } else {
            return STATIONARY;
        }
    }

    /**
     * Get last received location or last known Location
     *
     * @param context Android Context
     * @return Last received or last known Location
     */
    public Location getLocation(Context context) {
        return mLocation == null ? getLastKnownLocation(context) : mLocation;
    }

    private Location getLastKnownLocation(Context context) {
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }
}
