package com.zowdow.direct_api.utils;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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

import rx.Observable;

public class QueryUtils {
    private static final float DENSITY_M = 160.0f;

    private static final int DEFAULT_SUGGESTIONS_LIMIT = 10;
    private static final int DEFAULT_CARDS_LIMIT = 15;

    private static final String DEFAULT_OS              = "Android";
    private static final String MOCK_PACKAGE_NAME       = "com.zowdow.android.example";
    private static final String MOCK_SDK_VERSION        = "2.0.105";
    private static final String MOCK_APP_VERSION        = "1.0.218";
    private static final int    MOCK_APP_CODE           = 218;

    private static final Map<String, Object> sQueryMap = Collections.synchronizedMap(new HashMap<>());

    private QueryUtils() {}

    /**
     * Observable with map full of query parameters required for Init & Unified API calls.
     * @param context
     * @return
     */
    public static Observable<Map<String, Object>> getQueryMapObservable(Context context) {
        return Observable.create(subscriber -> {
            if (sQueryMap.isEmpty()) {
                final String os = DEFAULT_OS;
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
                sQueryMap.put(OS, os);
                sQueryMap.put(TRACKING, getIntFromBooleanValue(true));
                sQueryMap.put(SCREEN_SCALE, screenDensity);
                sQueryMap.put(SCREEN_WIDTH, screenWidth);
                sQueryMap.put(SCREEN_HEIGHT, screenHeight);
                sQueryMap.put(SYSTEM_VER, systemVersion);
                sQueryMap.put(SDK_VER, MOCK_SDK_VERSION);
            }

            Location location = LocationManager.get().getLocation(context);
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

            subscriber.onNext(sQueryMap);
            subscriber.onCompleted();
        });
    }

    public static Map<String, Object> createQueryMapForUnifiedApi(String searchQuery, String currentCardFormat) {
        Map<String, Object> unifiedQueryMap = sQueryMap;
        try {
            unifiedQueryMap.put("s_limit", DEFAULT_SUGGESTIONS_LIMIT);
            unifiedQueryMap.put("c_limit", DEFAULT_CARDS_LIMIT);
            unifiedQueryMap.put(QueryKeys.CARD_FORMAT, currentCardFormat);
            unifiedQueryMap.put("q", URLEncoder.encode(searchQuery, "UTF-8").replace("+", " "));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return unifiedQueryMap;
        }
        return unifiedQueryMap;
    }

    /**
     * Check Google Play Services for availability
     *
     * @param context
     * @return
     */
    private static boolean isGPServicesAvailable(Context context) {
        try {
            Class.forName("com.google.android.gms.common.api.GoogleApiClient");
            Class.forName("com.google.android.gms.common.GoogleApiAvailability");
            GoogleApiAvailability instance = GoogleApiAvailability.getInstance();
            if (instance != null) {
                return (ConnectionResult.SUCCESS == instance.isGooglePlayServicesAvailable(context));
            }
        } catch (ClassNotFoundException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        } catch (IllegalStateException e) {
            return false;
        }
        return false;
    }

    /**
     * Get Advertising Id as device id, or, if it's not available, get ANDROID_ID.
     * Call this method from background thread.
     *
     * @param context
     * @return
     */
    private static String getDeviceId(Context context) {
        String deviceId = null;
        if (isGPServicesAvailable(context)) {
            deviceId = getAdvertisingId(context);
            Log.d("QueryUtils", "Ad Device id: " + deviceId);
        }
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        return deviceId;
    }

    /**
     * Get Advertising Id from Google Play Services
     *
     * @param context
     * @return
     */
    private static String getAdvertisingId(Context context) {
        try {
            return AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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
