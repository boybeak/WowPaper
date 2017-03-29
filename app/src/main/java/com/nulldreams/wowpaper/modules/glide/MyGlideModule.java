package com.nulldreams.wowpaper.modules.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.GlideModule;
import com.nulldreams.wowpaper.WowApp;

import java.io.File;

/**
 * Created by gaoyunfei on 2017/3/29.
 */

public class MyGlideModule implements GlideModule {

    public static final int CACHE_SIZE = 1024 * 1024 * 128;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        File cacheFolder = WowApp.getGlideCacheDir(context);
        builder.setDiskCache(new DiskLruCacheFactory(
                cacheFolder.getAbsolutePath(), CACHE_SIZE))
                .setDecodeFormat(DecodeFormat.PREFER_RGB_565);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
