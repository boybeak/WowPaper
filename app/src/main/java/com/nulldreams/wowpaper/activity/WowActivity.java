package com.nulldreams.wowpaper.activity;

import android.view.View;

import com.nulldreams.base.BaseActivity;

/**
 * Created by gaoyunfei on 2017/3/21.
 */

public class WowActivity extends BaseActivity {
    private int uiVisibility () {
        return View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && getWindow().getDecorView().getSystemUiVisibility() != uiVisibility()) {
            getWindow().getDecorView().setSystemUiVisibility(uiVisibility());
        }
    }
}
