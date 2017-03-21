package com.nulldreams.base.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by boybe on 2017/3/21.
 */

public class UiHelper {
    public static int getActionBarSize (Context context) {
        TypedValue tv = new TypedValue();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            return TypedValue.complexToDimensionPixelSize(tv.data, metrics);
        }
        return (int)(56 * metrics.density);
    }
}
