package com.nulldreams.wowpaper.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Messenger;
import android.util.Log;

import com.nulldreams.base.utils.UiHelper;
import com.nulldreams.wowpaper.modules.Paper;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class WowPaperService extends IntentService {

    private static final String TAG = WowPaperService.class.getSimpleName();

    public static final String
            ACTION_SET_WALLPAPER = "com.nulldreams.wowpaper.service.WowPaperService.ACTION_SET_WALLPAPER",
            ACTION_DOWNLOAD_WALLPAPER = "com.nulldreams.wowpaper.service.WowPaperService.ACTION_DOWNLOAD_WALLPAPER";

    public static final String KEY_WALLPAPER = "paper";

    public WowPaperService() {
        super("WallPaperService");
    }

    public static void setWallpaper(Context context, Paper paper) {
        Intent intent = new Intent(context, WowPaperService.class);
        intent.setAction(ACTION_SET_WALLPAPER);
        intent.putExtra(KEY_WALLPAPER, paper);
        context.startService(intent);
    }

    public static void downloadWallpaper (Context context, Paper paper) {
        Intent it = new Intent(context, WowPaperService.class);
        it.setAction(ACTION_DOWNLOAD_WALLPAPER);
        it.putExtra(KEY_WALLPAPER, paper);
        context.startService(it);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_DOWNLOAD_WALLPAPER:
                    handleDownloadPaper();
                    break;
                case ACTION_SET_WALLPAPER:
                    handleSetWallpaper();
                    break;
            }
        }
    }

    private void handleDownloadPaper () {

    }

    private void handleSetWallpaper () {

    }
}
