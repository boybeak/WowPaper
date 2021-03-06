package com.nulldreams.wowpaper.activity;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.nulldreams.base.activity.SplashActivity;
import com.nulldreams.base.content.It;
import com.nulldreams.wowpaper.R;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class HelloActivity extends SplashActivity {

    private static final String TAG = HelloActivity.class.getSimpleName();

    @ViewInject(value = R.id.hello_bg)
    private ImageView mBgIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        x.view().inject(this);

        Drawable drawable = WallpaperManager.getInstance(this).getFastDrawable();
        if (drawable != null) {
            mBgIv.setImageDrawable(drawable);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.v(TAG, "navigation_hidden=" + (getResources().getConfiguration().navigationHidden == Configuration.NAVIGATIONHIDDEN_YES));

    }

    @Override
    public Intent getNextActivityIntent() {
        Log.v(TAG, "getNextActivityIntent " + getResources().getDisplayMetrics().heightPixels);
        return It.newInstance().setClass(this, MainActivity.class);
    }

    @Override
    public int getNextActivityStartDelay() {
        return 2000;
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "navigation_hidden=" + (getResources().getConfiguration().navigationHidden == Configuration.NAVIGATIONHIDDEN_YES));
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
