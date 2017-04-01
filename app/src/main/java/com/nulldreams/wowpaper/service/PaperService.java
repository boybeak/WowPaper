package com.nulldreams.wowpaper.service;

import android.app.IntentService;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;

import com.nulldreams.wowpaper.Finals;

public class PaperService extends IntentService {

    private static final String TAG = PaperService.class.getSimpleName();

    private static final String ACTION_SET_WALLPAPER = "com.nulldreams.wowpaper.service.action.SET_WALLPAPER";

    private static final String EXTRA_PATH = "com.nulldreams.wowpaper.service.extra.PATH",
            EXTRA_WIDTH = "com.nulldreams.wowpaper.service.extra.WIDTH",
            EXTRA_HEIGHT = "com.nulldreams.wowpaper.service.extra.HEIGHT";

    public PaperService() {
        super("PaperService");
    }

    public static void setWallpaper(Context context, String path, int targetWidth, int targetHeight) {
        Intent intent = new Intent(context, PaperService.class);
        intent.setAction(ACTION_SET_WALLPAPER);
        intent.putExtra(EXTRA_PATH, path);
        intent.putExtra(EXTRA_WIDTH, targetWidth);
        intent.putExtra(EXTRA_HEIGHT, targetHeight);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SET_WALLPAPER.equals(action)) {
                final String path = intent.getStringExtra(EXTRA_PATH);
                final int width = intent.getIntExtra(EXTRA_WIDTH, 0);
                final int height = intent.getIntExtra(EXTRA_HEIGHT, 0);

                handleSetWallpaper(path, width, height);
            }
        }
    }

    private void handleSetWallpaper(String path, int width, int height) {
        boolean success = false;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Bitmap bmp = Bitmap.createScaledBitmap(bitmap, width, height, true);
            bitmap.recycle();
            WallpaperManager.getInstance(this).setBitmap(bmp);
            WallpaperManager.getInstance(this).suggestDesiredDimensions(width, height);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(
                new Intent(Finals.ACTION_WOW_PAPER_SET).putExtra(Finals.KEY_BOOL_RESULT, success));
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
