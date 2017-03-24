package com.nulldreams.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

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

}
