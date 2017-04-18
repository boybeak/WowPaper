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
    public static boolean lollipopAndAbove () {
        return api21AndAbove();
    }
    public static boolean api19AndAbove () {
        return nAndAbove(Build.VERSION_CODES.KITKAT);
    }
    public static boolean kitkatAndAbove () {
        return api19AndAbove();
    }

    public static boolean nAndAbove (int n) {
        return Build.VERSION.SDK_INT >= n;
    }
}
