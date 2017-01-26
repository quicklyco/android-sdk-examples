package com.zowdow.direct_api.utils.location;

import android.content.Context;
import android.location.Location;

import com.zowdow.direct_api.utils.PermissionsUtils;

public class LocationManager {
    private static LocationManager locationManagerInstance;

    private int mClients = 0;

    private AndroidLocation mAndroidLocation;

    private LocationManager() {
        mAndroidLocation = AndroidLocation.getInstance();
    }

    public static synchronized LocationManager get() {
        if (locationManagerInstance == null) {
            locationManagerInstance = new LocationManager();
        }
        return locationManagerInstance;
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
        mAndroidLocation.start(context);

    }

    /**
     * Stop receiving location updates
     */
    public synchronized void stop() {
        if (--mClients == 0) {
            mAndroidLocation.stop();
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

        return mAndroidLocation.getLocation(context);
    }
}
