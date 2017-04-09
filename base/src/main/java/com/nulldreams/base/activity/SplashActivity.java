package com.nulldreams.base.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.nulldreams.base.BaseActivity;
import com.nulldreams.base.utils.UiHelper;

/**
 * Created by gaoyunfei on 2017/4/9.
 */

public abstract class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private int mDectorViewUiVisibility = 0;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDectorViewUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        getWindow().getDecorView().setSystemUiVisibility(mDectorViewUiVisibility);

        Log.v(TAG, "onCreate layoutDirection=" + getResources().getConfiguration().getLayoutDirection());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                UiHelper.saveRealScreenSize(SplashActivity.this);
                startActivity(getNextActivityIntent());
                finish();
            }
        }, getNextActivityStartDelay());
    }

    public abstract Intent getNextActivityIntent ();
    public abstract int getNextActivityStartDelay ();

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
