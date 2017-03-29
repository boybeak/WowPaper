package com.nulldreams.wowpaper.activity;

import android.view.View;
import android.widget.Toast;

import com.nulldreams.base.BaseActivity;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.event.PaperSetEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWallpaperResult (PaperSetEvent result) {
        Toast.makeText(this,
                result.success ? R.string.toast_set_wallpaper_success : R.string.toast_set_wallpaper_failed,
                Toast.LENGTH_SHORT).show();
    }
}
