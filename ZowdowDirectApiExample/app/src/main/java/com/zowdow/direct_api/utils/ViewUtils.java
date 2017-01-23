package com.zowdow.direct_api.utils;

import android.content.res.Resources;
import android.util.TypedValue;

public class ViewUtils {
    public static int dpToPx(final int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }
}
