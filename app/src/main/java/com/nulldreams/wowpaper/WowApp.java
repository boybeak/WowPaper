package com.nulldreams.wowpaper;

import android.app.Application;
import android.app.WallpaperManager;
import android.content.Context;
import android.os.Build;

import com.nulldreams.base.CrashHandler;

/**
 * Created by gaoyunfei on 2017/3/18.
 */

public class WowApp extends Application {
    public static boolean checkWallpaperPermission (Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return WallpaperManager.getInstance(context).isWallpaperSupported()
                    && WallpaperManager.getInstance(context).isSetWallpaperAllowed();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return WallpaperManager.getInstance(context).isWallpaperSupported();
        }
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        CrashHandler.getInstance(this).install();
    }
}
