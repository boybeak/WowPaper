package com.nulldreams.wowpaper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by gaoyunfei on 2017/4/4.
 */

public class DeviceInfo {

    private static int deviceWidth, deviceHeight;

    public static void saveDeviceScreenSize (Context context, int width, int height) {
        deviceWidth = width;
        deviceHeight = height;
        SharedPreferences preferences = context.getSharedPreferences(Finals.PREF_DEVICE_INFO,
                Context.MODE_PRIVATE);
        preferences.edit()
                .putInt("device_width", width)
                .putInt("device_height", height)
                .apply();
    }

    public static int getDeviceWidth (Context context) {
        if (deviceWidth == 0) {
            SharedPreferences preferences = context.getSharedPreferences(Finals.PREF_DEVICE_INFO,
                    Context.MODE_PRIVATE);
            deviceWidth = preferences.getInt("device_width", 0);
        }
        return deviceWidth;
    }

    public static int getDeviceHeight(Context context) {
        if (deviceHeight == 0) {
            SharedPreferences preferences = context.getSharedPreferences(Finals.PREF_DEVICE_INFO,
                    Context.MODE_PRIVATE);
            deviceHeight = preferences.getInt("device_height", 0);
        }
        return deviceHeight;
    }
}
