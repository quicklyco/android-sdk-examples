package com.zowdow.direct_api.utils.helpers;

import android.content.res.Resources;

public class DeviceHelper {
    public int getDeviceScreenDensity() {
        return Math.round(Resources.getSystem().getDisplayMetrics().density);
    }
}
