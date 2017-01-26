package com.zowdow.direct_api.utils.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import com.zowdow.direct_api.utils.PermissionsUtils;

class AndroidLocation {
    private static final int MIN_UPDATE_TIME = 10 * 60 * 1000; // 10 minutes
    private static final int MIN_UPDATE_DIST = 250; // 250 meters

    private static AndroidLocation sInstance;

    private LocationManager mLocationManager;

    private LocationListener mAndroidLocationListener;

    private Location mLocation;

    private AndroidLocation() {
        mAndroidLocationListener = createAndroidLocationListener();
    }

    public static AndroidLocation getInstance() {
        synchronized (AndroidLocation.class) {
            if (sInstance == null) {
                sInstance = new AndroidLocation();
            }
            return sInstance;
        }
    }

    private LocationListener createAndroidLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
    }

    /**
     * Stop receiving locations with Android framework
     */
    @SuppressWarnings("MissingPermission")
    void stop() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mAndroidLocationListener);
        }
    }

    /**
     * Get last known location
     *
     * @param context Android Context
     * @return Last known Location
     */
    @SuppressWarnings("MissingPermission")
    private Location getLastKnownLocation(Context context) {
        if (mLocationManager != null) {
            // Android location
            boolean mIsGPSEnabled = PermissionsUtils.checkFineLocationPermission(context) && mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean mIsNetworkEnabled = PermissionsUtils.checkCoarseLocationPermission(context) && mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Location location = null;
            if (mIsGPSEnabled) {
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (location == null && mIsNetworkEnabled) {
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            return location;
        }
        return null;
    }

    /**
     * Start receiving locations using Android framework
     *
     * @param context Android Context
     */
    @SuppressWarnings("MissingPermission")
    void start(Context context) {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        if (PermissionsUtils.checkCoarseLocationPermission(context)
                && mLocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_UPDATE_TIME, MIN_UPDATE_DIST, mAndroidLocationListener, Looper.getMainLooper());
        } else if (PermissionsUtils.checkFineLocationPermission(context)
                && mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_UPDATE_TIME, MIN_UPDATE_DIST, mAndroidLocationListener, Looper.getMainLooper());
        }
    }

    /**
     * Get last received location or last known Location
     *
     * @param context Android Context
     * @return Last received or last known Location
     */
    Location getLocation(Context context) {
        return mLocation == null ? getLastKnownLocation(context) : mLocation;
    }
}
