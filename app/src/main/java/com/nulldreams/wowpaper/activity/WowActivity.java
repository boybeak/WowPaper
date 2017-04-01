package com.nulldreams.wowpaper.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Toast;

import com.nulldreams.base.BaseActivity;
import com.nulldreams.wowpaper.Finals;
import com.nulldreams.wowpaper.R;

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

    private BroadcastReceiver mWowReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(Finals.KEY_BOOL_RESULT, false);
            Toast.makeText(WowActivity.this, success ? R.string.toast_set_wallpaper_success : R.string.toast_set_wallpaper_failed,
                    Toast.LENGTH_SHORT).show();
        }
    };

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
        LocalBroadcastManager.getInstance(this).registerReceiver(mWowReceiver,
                new IntentFilter(Finals.ACTION_WOW_PAPER_SET));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mWowReceiver);
    }

}
