package com.zowdow.direct_api.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.webkit.WebView;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zowdow.direct_api.BuildConfig;
import com.zowdow.direct_api.utils.location.LocationMgr;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Request;
import retrofit2.Call;

public class RequestUtils {
    private static final float DENSITY_M = 160.0f;

    public static final String APP_ID            = "app_id";
    public static final String APP_VER           = "app_ver";
    public static final String APP_BUILD         = "app_build";
    public static final String DEVICE_MODEL      = "device_model";
    public static final String DEVICE_ID         = "device_id";
    public static final String OS                = "os";
    public static final String SCREEN_SCALE      = "screen_scale";
    public static final String SCREEN_WIDTH      = "screen_width";
    public static final String SCREEN_HEIGHT     = "screen_height";
    public static final String SYSTEM_VER        = "system_ver";
    public static final String SDK_VER           = "sdk_ver";
    public static final String LAT               = "lat";
    public static final String LONG              = "long";
    public static final String LOCATION_ACCURACY = "location_accuracy";
    public static final String ANDROID_ID        = "android_id";
    public static final String NETWORK           = "network";
    public static final String LOCALE            = "locale";
    public static final String TIMEZONE          = "timezone";
    public static final String KEYBOARD          = "keyboard";
    public static final String CARD_FORMAT       = "card_format";
    public static final String ADS_AVAILIBILITY  = "ads";

    private static final String CARD_FORMATS_SEPARATOR  = "||";
    private static final String USER_AGENT_MACROS       = "ZD_USER_AGENT";
    private static final String CLIENT_IP_MACROS        = "ZD_CLIENT_IP";
    private static final String FAKE_DEBUG_IP_ADDRESS   = "206.214.34.141";

    private static final boolean DEFAULT_ADS_AVAILIBILITY = true;

    public static final Map<String, Object> sQueryMap = Collections.synchronizedMap(new HashMap<>());

    private RequestUtils() {}

    /**
     * Create a JsonObject for Zowdow request
     *
     * @param context
     * @return
     */
    public static JsonObject createRequestParams(Context context) {
        return new JsonParser().parse(new Gson().toJson(createQueryMap(context))).getAsJsonObject();
    }

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
            final String packageName = context.getPackageName();
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            final int screenWidth = displayMetrics.widthPixels;
            final int screenHeight = displayMetrics.heightPixels;
            final float screenDensity = displayMetrics.densityDpi / DENSITY_M;

            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                sQueryMap.put(APP_VER, "1.0.218");
                sQueryMap.put(APP_BUILD, 218);
            } catch (PackageManager.NameNotFoundException e) {
                // Do nothing
            }

            sQueryMap.put(APP_ID, "com.searchmaster.searchapp");
            sQueryMap.put(DEVICE_MODEL, deviceModel);
            sQueryMap.put(ANDROID_ID, Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            sQueryMap.put(OS, os);
            sQueryMap.put(SCREEN_SCALE, screenDensity);
            sQueryMap.put(SCREEN_WIDTH, screenWidth);
            sQueryMap.put(SCREEN_HEIGHT, screenHeight);
            sQueryMap.put(SYSTEM_VER, systemVersion);
            sQueryMap.put(SDK_VER, "2.0.105");
            sQueryMap.put(ADS_AVAILIBILITY, getAdsAvailabilityIntValue(DEFAULT_ADS_AVAILIBILITY));
        }

        Location location = LocationMgr.get().getLocation(context);
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

        return new HashMap<>(sQueryMap);
    }

    public static void setCustomAppId(final String appId) {
        sQueryMap.put(APP_ID, appId);
    }

    /**
     * Get Advertising Id as device id, or, if it's not available, get ANDROID_ID.
     * Call this method from background thread.
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceId;
    }

    /**
     * Converts the array of different card formats to a formatted server-compatible string.
     * @param cardFormats
     * @return
     */
    public static String getFormattedCardFormats(String[] cardFormats) {
        return TextUtils.join(CARD_FORMATS_SEPARATOR, cardFormats);
    }

    /**
     * Check Google Play Services for availability
     *
     * @param context
     * @return
     */
    public static boolean isGPServicesAvailable(Context context) {
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
     * Get Advertising Id from Google Play Services
     *
     * @param context
     * @return
     */
    public static String getAdvertisingId(Context context) {
        try {
            return AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getUrlFromRetrofitCall(Call initCall) throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Field delegateField = initCall.getClass().getDeclaredField("delegate");
        delegateField.setAccessible(true);
        Object delegate = delegateField.get(initCall);
        Method createRawCall = delegate.getClass().getDeclaredMethod("createRawCall");
        createRawCall.setAccessible(true);
        okhttp3.Call okHttpCall = (okhttp3.Call) createRawCall.invoke(delegate);

        Field originalRequestField = okHttpCall.getClass().getDeclaredField("originalRequest");
        originalRequestField.setAccessible(true);
        Request originalRequest = (Request) originalRequestField.get(okHttpCall);
        return originalRequest.url().toString();
    }

    /**
     * Get User Agent for ad call URL.
     * @param context
     * @return user agent string for ad call URL.
     */
    public static String getUserAgent(Context context) {
        return new WebView(context).getSettings().getUserAgentString();
    }

    /**
     * Get action target URL for AdMarketPlace ad customized for current device depending on its' user agent.
     * @see RequestUtils#getUserAgent(Context)
     * @param context
     * @param oldActionTarget
     * @return
     */
    public static String getCustomizedAdCallActionTarget(Context context, String oldActionTarget) {
        return oldActionTarget
                .replace(USER_AGENT_MACROS, getUserAgent(context))
                .replace(CLIENT_IP_MACROS, FAKE_DEBUG_IP_ADDRESS);
    }

    /**
     * Get action target URL for AdMarketPlace ad customized for current device depending on a custom user agent string.
     * @param userAgent
     * @param oldActionTarget
     * @return action target URL for AdMarketPlace ad customized for current device.
     */
    public static String getCustomizedAdCallActionTarget(String userAgent, String oldActionTarget) {
        return oldActionTarget
                .replace(USER_AGENT_MACROS, userAgent)
                .replace(CLIENT_IP_MACROS, FAKE_DEBUG_IP_ADDRESS);
    }

    /**
     * Returns 0 for disabled ads and 1 for enabled.
     * @param adsEnabled defines ads availability.
     * @return 0 for disabled ads and 1 for enabled.
     */
    private static int getAdsAvailabilityIntValue(boolean adsEnabled) {
        return adsEnabled ? 1 : 0;
    }
}
