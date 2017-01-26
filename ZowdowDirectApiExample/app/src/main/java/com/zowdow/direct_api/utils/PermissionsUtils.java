package com.zowdow.direct_api.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

public class PermissionsUtils {
    /**
     * Check ACCESS_FINE_LOCATION permission
     *
     * @param context
     * @return
     */
    public static boolean checkFineLocationPermission(Context context) {
        return checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * Check ACCESS_COARSE_LOCATION permission
     *
     * @param context
     * @return
     */
    public static boolean checkCoarseLocationPermission(Context context) {
        return checkPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    private static boolean checkPermission(final Context context, final String permission) {
        try {
            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
