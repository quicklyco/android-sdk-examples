package com.zowdow.direct_api.utils;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

import com.zowdow.direct_api.utils.constants.QueryKeys;
import com.zowdow.direct_api.utils.location.LocationManager;

import static com.zowdow.direct_api.utils.constants.QueryKeys.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class QueryUtils {
    private static final float DENSITY_M = 160.0f;

    private static final int DEFAULT_SUGGESTIONS_LIMIT = 10;
    private static final int DEFAULT_CARDS_LIMIT = 15;

    private static final String MOCK_PACKAGE_NAME       = "com.searchmaster.searchapp";
    private static final String MOCK_SDK_VERSION        = "2.0.105";
    private static final String MOCK_APP_VERSION        = "1.0.218";
    private static final int    MOCK_APP_CODE           = 218;

    public static final Map<String, Object> sQueryMap = Collections.synchronizedMap(new HashMap<>());

    private QueryUtils() {}

    /**
     * Create and return a map of Zowdow request parameters
     *
     * @param context
     * @return
     */
    public static Map<String, Object> createQueryMap(Context context) {
        if (sQueryMap.isEmpty()) {
            final String os = "Android";
            final String deviceModel = Build.MANUFACTURER + " " + Build.MODEL;
            final String systemVersion = Build.VERSION.RELEASE;
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            final int screenWidth = displayMetrics.widthPixels;
            final int screenHeight = displayMetrics.heightPixels;
            final float screenDensity = displayMetrics.densityDpi / DENSITY_M;

            sQueryMap.put(APP_VER, MOCK_APP_VERSION);
            sQueryMap.put(APP_BUILD, MOCK_APP_CODE);
            sQueryMap.put(APP_ID, MOCK_PACKAGE_NAME);
            sQueryMap.put(DEVICE_MODEL, deviceModel);
            sQueryMap.put(ANDROID_ID, Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            sQueryMap.put(OS, os);
            sQueryMap.put(TRACKING, getIntFromBooleanValue(true));
            sQueryMap.put(SCREEN_SCALE, screenDensity);
            sQueryMap.put(SCREEN_WIDTH, screenWidth);
            sQueryMap.put(SCREEN_HEIGHT, screenHeight);
            sQueryMap.put(SYSTEM_VER, systemVersion);
            sQueryMap.put(SDK_VER, MOCK_SDK_VERSION);
        }

        Location location = LocationManager.get().getLocation(context);
        Log.d("QueryUtils", "Location: " + location);
        if (location != null) {
            sQueryMap.put(LAT, location.getLatitude());
            sQueryMap.put(LONG, location.getLongitude());
            float accuracy = location.getAccuracy();
            if (accuracy != 0.0f) {
                sQueryMap.put(LOCATION_ACCURACY, accuracy);
            } else {
                sQueryMap.remove(LOCATION_ACCURACY);
            }
        } else {
            sQueryMap.put(LAT, 0);
            sQueryMap.put(LONG, 0);
        }

        sQueryMap.put(NETWORK, ConnectivityUtils.getConnectionType(context));
        sQueryMap.put(LOCALE, Locale.getDefault());
        sQueryMap.put(TIMEZONE, TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT));
        sQueryMap.put(DEVICE_ID, getDeviceId(context));

        return new HashMap<>(sQueryMap);
    }

    public static Map<String, Object> createQueryMapForUnifiedApi(Context context, String searchQuery, String currentCardFormat) {
        Map<String, Object> unifiedQueryMap = createQueryMap(context);
        try {
            unifiedQueryMap.put("s_limit", DEFAULT_SUGGESTIONS_LIMIT);
            unifiedQueryMap.put("c_limit", DEFAULT_CARDS_LIMIT);
            unifiedQueryMap.put(QueryKeys.CARD_FORMAT, currentCardFormat);
            unifiedQueryMap.put(QueryKeys.DEVICE_ID, QueryUtils.getDeviceId(context.getApplicationContext()));
            unifiedQueryMap.put("q", URLEncoder.encode(searchQuery, "UTF-8").replace("+", " "));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return unifiedQueryMap;
        }
        return unifiedQueryMap;
    }

    /**
     * Get Advertising Id as device id, or, if it's not available, get ANDROID_ID.
     * Call this method from background thread.
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Returns 0 for disabled ads and 1 for enabled.
     * @param enabled defines ads availability.
     * @return 0 for disabled ads and 1 for enabled.
     */
    private static int getIntFromBooleanValue(boolean enabled) {
        return enabled ? 1 : 0;
    }
}
