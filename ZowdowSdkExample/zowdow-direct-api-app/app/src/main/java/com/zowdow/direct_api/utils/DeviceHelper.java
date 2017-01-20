package com.zowdow.direct_api.utils;

import android.content.res.Resources;

public class DeviceHelper {
    private DeviceHelper() {}

    public int getDeviceScreenDensity() {
        return Math.round(Resources.getSystem().getDisplayMetrics().density);
    }
}
