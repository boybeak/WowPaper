package com.nulldreams.wowpaper.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.nulldreams.base.activity.SplashActivity;
import com.nulldreams.base.content.It;
import com.nulldreams.wowpaper.R;

public class HelloActivity extends SplashActivity {

    private static final String TAG = HelloActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hello);

        Log.v(TAG, "heightDpi=" + getResources().getConfiguration().screenHeightDp);

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.v(TAG, "navigation_hidden=" + (getResources().getConfiguration().navigationHidden == Configuration.NAVIGATIONHIDDEN_YES));
        /*getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                View view = getWindow().getDecorView();
                DeviceInfo.saveDeviceScreenSize(HelloActivity.this, view.getWidth(), view.getHeight());

                finish();
            }
        }, 3000);*/

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
}
