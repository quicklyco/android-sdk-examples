package com.zowdow.direct_api.utils.location;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.zowdow.direct_api.utils.PermissionsUtils;

public enum LocationMgr {
    Instance;

    private int mClients = 0;

    private AndroidLocation mAndroidLocation;
    private GoogleLocation  mGoogleLocation;

    private boolean mGoogleExists;

    LocationMgr() {
        mAndroidLocation = AndroidLocation.getInstance();

        try {
            Class.forName("com.google.android.gms.common.api.GoogleApiClient");
            Class.forName("com.google.android.gms.location.LocationRequest");
            mGoogleExists = true;
        } catch (ClassNotFoundException e) {
            mGoogleExists = false;
        }

        if (mGoogleExists) {
            mGoogleLocation = GoogleLocation.getInstance();
        }
    }

    /**
     * Get instance of LocationMgr
     *
     * @return LocationMgr Instance
     */
    public static LocationMgr get() {
        return Instance;
    }

    /**
     * Check does we have location permissions
     *
     * @param context Android Context
     * @return true if an app has at least one of Fine or Coarse permissions
     */
    private boolean hasLocationPermissions(Context context) {
        return PermissionsUtils.checkFineLocationPermission(context)
                || PermissionsUtils.checkCoarseLocationPermission(context);
    }

    /**
     * Start receiving location updates
     *
     * @param context Android Context
     */
    public synchronized void start(Context context) {
        if (!hasLocationPermissions(context)) {
            return;
        }

        mClients++;

        mGoogleExists = mGoogleExists &&
                (ConnectionResult.SUCCESS == GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context));

        if (mGoogleExists) {
            mGoogleLocation.start(context);
        } else {
            mAndroidLocation.start(context);
        }

    }

    /**
     * Stop receiving location updates
     */
    public synchronized void stop() {
        if (--mClients == 0) {
            if (mGoogleExists) {
                mGoogleLocation.stop();
            } else {
                mAndroidLocation.stop();
            }
        }
    }

    /**
     * Get last known location
     *
     * @param context Android Context
     * @return Last known Location
     */
    @SuppressWarnings("MissingPermission")
    public Location getLocation(Context context) {
        if (!hasLocationPermissions(context)) {
            return null;
        }

        if (mGoogleExists) {
            return mGoogleLocation.getLocation(context);
        } else {
            return mAndroidLocation.getLocation(context);
        }
    }

    public static double encryptCoord(double coord) {
        return coord * 1000000 + 123456;
    }

    public static double decryptCoord(double coord) {
        return (coord - 123456) * 1000000;
    }

    public GoogleLocation getGoogleLocation() {
        return mGoogleLocation;
    }

    public String getDetectedActivity() {
        if (mGoogleLocation != null) {
            return mGoogleLocation.getDetectedActivity();
        } else {
            return GoogleLocation.STATIONARY;
        }
    }
}
