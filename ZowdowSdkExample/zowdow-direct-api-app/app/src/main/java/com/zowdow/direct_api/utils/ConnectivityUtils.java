package com.zowdow.direct_api.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class ConnectivityUtils {
    private ConnectivityUtils() {}

    /**
     * Get the network info
     *
     * @param context Android Context
     * @return NetworkInfo from system ConnectivityManager
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity
     *
     * @param context Android Context
     * @return true if device "is connected" to network
     */
    @SuppressWarnings("unused")
    public static boolean isConnected(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    /**
     * Check if there is any connectivity to a Wifi network
     *
     * @param context Android Context
     * @return true if device "is connected" to network and the connection type is WiFi
     */
    @SuppressWarnings("unused")
    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Check if there is any connectivity to a mobile network
     *
     * @param context Android Context
     * @return true if device "is connected" to network and the connection type is Mobile
     */
    @SuppressWarnings("unused")
    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * Check if there is fast connectivity
     *
     * @param context Android Context
     * @return current connection type name
     */
    public static String getConnectionType(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info == null) {
            return "unknown";
        } else {
            return getConnectionNameByType(info.getType(), info.getSubtype());
        }
    }

    /**
     * Get connection type name (wi-fi, gprs, cdma, etc.)
     *
     * @param type    connection type
     * @param subType connection subtype
     * @return connection name for specified type and subtype
     */
    public static String getConnectionNameByType(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return "wifi";
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return "1xrtt"; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return "cdma"; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return "edge"; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return "evdo_0"; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return "evdo_a"; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return "gprs"; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return "hsdpa"; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return "hspa"; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return "hsupa"; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return "umts"; // ~ 400-7000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return "evdo_b"; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return "iden"; // ~25 kbps
            /*
             * Above API level 7, make sure to set android:targetSdkVersion
             * to appropriate level to use these
             */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return "ehrpd"; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return "hspap"; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return "lte"; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return "unknown";
            }
        } else {
            return "unknown";
        }
    }
}
