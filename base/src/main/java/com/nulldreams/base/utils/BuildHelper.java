package com.nulldreams.base.utils;

import android.os.Build;

/**
 * Created by gaoyunfei on 2017/2/3.
 */

public class BuildHelper {
    public static boolean api16AndAbove () {
        return nAndAbove(Build.VERSION_CODES.JELLY_BEAN);
    }
    public static boolean api17AndAbove () {
        return nAndAbove(Build.VERSION_CODES.JELLY_BEAN_MR1);
    }
    public static boolean api21AndAbove () {
        return nAndAbove(Build.VERSION_CODES.LOLLIPOP);
    }

    public static boolean nAndAbove (int n) {
        return Build.VERSION.SDK_INT >= n;
    }
}
