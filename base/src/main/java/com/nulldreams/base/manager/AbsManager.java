package com.nulldreams.base.manager;

import android.content.Context;

/**
 * Created by gaoyunfei on 2017/1/29.
 */

public abstract class AbsManager {

    private Context mContext = null;

    public AbsManager (Context context) {
        mContext = context;
    }


    public Context getContext () {
        return mContext;
    }

    public void recycle () {
        mContext = null;
    }

    public boolean isRecycled () {
        return mContext == null;
    }
}
