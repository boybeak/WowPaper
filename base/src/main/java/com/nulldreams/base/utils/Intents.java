package com.nulldreams.base.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by gaoyunfei on 2016/12/7.
 */

public abstract class Intents {

    public static void viewMyAppOnStore (Context context) {
        viewAppOnStore(context, context.getPackageName());
    }

    public static void viewAppOnStore (Context context, String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);;
        context.startActivity(intent);
    }

    public static void openUrl (Context context, String url) {
        Intent it = new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse(url));;
        context.startActivity(it);
    }

    public static void shareImage (Context context, String chooserTitle, Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_STREAM, uri)
                .setType("image/jpeg");
        context.startActivity(Intent.createChooser(shareIntent, chooserTitle));
    }

    public static void shareImage (Context context, String chooserTitle, File file) {
        shareImage(context, chooserTitle, Uri.fromFile(file));
    }

    public static void shareImage (Context context, String chooserTitle, String path) {
        shareImage(context, chooserTitle, new File(path));
    }

    public static void call (Context context, String phoneNumber) {
        Intent it = new Intent (Intent.ACTION_DIAL);
        String uri = "tel:" + phoneNumber.trim() ;
        it.setData(Uri.parse(uri));
        context.startActivity(it);
    }

}
