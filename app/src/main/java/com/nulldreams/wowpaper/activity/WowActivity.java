package com.nulldreams.wowpaper.activity;

import android.view.View;

import com.nulldreams.base.BaseActivity;

/**
 * Created by gaoyunfei on 2017/3/21.
 */

public class WowActivity extends BaseActivity {
    private int uiVisibility () {
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        return uiOptions;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        /*if (hasFocus && getWindow().getDecorView().getSystemUiVisibility() != uiVisibility()) {
            getWindow().getDecorView().setSystemUiVisibility(uiVisibility());
        }*/
    }
}
