package com.nulldreams.base;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.nulldreams.base.event.PermissionCallback;

import java.util.Random;

/**
 * Created by gaoyunfei on 2017/3/21.
 */

public class BaseActivity extends AppCompatActivity {

    private PermissionCallback mPermissionCallback = null;
    private int mPermissionRequestCode = 0;

    public void requestPermission (String permission, @NonNull PermissionCallback callback) {
        requestPermissions(new String[]{permission}, callback);
    }

    public void requestPermissions (@NonNull String[] permissions, @NonNull PermissionCallback callback) {
        mPermissionCallback = callback;
        boolean granted = true;
        for (String permission : permissions) {
            granted &= PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, permission);
        }
        if (granted) {
            callback.onGranted();
            mPermissionCallback = null;
        } else {
            mPermissionRequestCode = new Random().nextInt(0xFFFE) + 1;
            ActivityCompat.requestPermissions(this, permissions, mPermissionRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermissionRequestCode == requestCode && mPermissionCallback != null) {
            boolean granted = true;
            for (int result : grantResults) {
                granted &= result == PackageManager.PERMISSION_GRANTED;
            }
            if (granted) {
                mPermissionCallback.onGranted();
            } else {
                mPermissionCallback.onDenied();
            }
            mPermissionCallback = null;
            mPermissionRequestCode = 0;
        }
    }
}
