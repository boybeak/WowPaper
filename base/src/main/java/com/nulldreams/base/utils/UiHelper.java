package com.nulldreams.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.nulldreams.base.activity.SplashActivity;

/**
 * Created by boybe on 2017/3/21.
 */

public class UiHelper {

    private static final String TAG = UiHelper.class.getSimpleName();

    private static int sRealWidth = 0, sRealHeight = 0;

    public static int getActionBarSize (Context context) {
        TypedValue tv = new TypedValue();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            return TypedValue.complexToDimensionPixelSize(tv.data, metrics);
        }
        return (int)(56 * metrics.density);
    }

    public static int getNavigationBarSize (Context context) {
        int navBarHeight = 0;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navBarHeight = resources.getDimensionPixelSize(resourceId);
        }
        return navBarHeight;
    }

    public static int getScreenHeightPortrait (Activity activity) {
        int navBarHeight = getNavigationBarSize(activity);

        DisplayMetrics dm = new DisplayMetrics();
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getMetrics(dm);

        boolean hasPhysicalHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        Point size = new Point();
        int screen_height = 0;
        if (android.os.Build.VERSION.SDK_INT >= 17){
            display.getRealSize(size);
            //screen_width = size.x;
            screen_height = size.y;
        } else if (hasPhysicalHomeKey){
            screen_height = dm.heightPixels;
        } else {
            screen_height = dm.heightPixels + navBarHeight;
        }
        return screen_height;
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /*public static boolean hasNavBar (Activity activity) {
    }*/

    public static void saveRealScreenSize (SplashActivity activity) {
        SharedPreferences preferences = getDeviceRealScreenSizeSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        View decorView = activity.getWindow().getDecorView();
        final int orientation = activity.getResources().getConfiguration().orientation;
        Log.v(TAG, "saveRealScreenSize orientation=" + orientation);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            sRealWidth = decorView.getWidth();
            sRealHeight = decorView.getHeight();
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            sRealWidth = decorView.getHeight();
            sRealHeight = decorView.getWidth();
        }
        editor.putInt("portrait_width", sRealWidth);
        editor.putInt("portrait_height", sRealHeight);
        editor.apply();
    }

    public static int getPortraitRealWidth (Context context) {
        if (sRealWidth == 0) {
            sRealWidth = getDeviceRealScreenSizeSharedPreferences(context).getInt("portrait_width", 0);
        }
        return sRealWidth;
    }

    public static int getPortraitRealHeight (Context context) {
        if (sRealHeight == 0) {
            sRealHeight = getDeviceRealScreenSizeSharedPreferences(context).getInt("portrait_height", 0);
        }
        return sRealHeight;
    }

    public static int getLandscapeRealWidth (Context context) {
        return getPortraitRealHeight(context);
    }

    public static int getLandscapeRealHeight (Context context) {
        return getPortraitRealWidth(context);
    }

    public static int getRealWidth (Activity activity) {
        final int orientation = activity.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return getPortraitRealWidth(activity);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return getLandscapeRealWidth(activity);
        }
        return 0;
    }

    public static int getRealHeight (Activity activity) {
        final int orientation = activity.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return getPortraitRealHeight(activity);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return getLandscapeRealHeight(activity);
        }
        return 0;
    }

    private static SharedPreferences getDeviceRealScreenSizeSharedPreferences (Context context) {
        return context.getSharedPreferences(
                context.getPackageName() + ".device_real_screen_size.pref", Context.MODE_PRIVATE);
    }

}
