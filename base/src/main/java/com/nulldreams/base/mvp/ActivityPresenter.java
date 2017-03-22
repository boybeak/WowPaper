package com.nulldreams.base.mvp;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import java.lang.ref.WeakReference;

/**
 * Created by gaoyunfei on 2017/3/22.
 */

public class ActivityPresenter implements PresenterImpl {

    private WeakReference<Activity> mActivityRef;

    public ActivityPresenter(Activity activity) {
        mActivityRef = new WeakReference<Activity>(activity);
    }


    public void create (Activity activity, Bundle savedInstanceState) {

    }
    public void postCreate (Activity activity, Bundle savedInstanceState) {

    }
    public void start (Activity activity) {

    }
    public void resume (Activity activity) {

    }
    public void pause (Activity activity) {

    }
    public void stop (Activity activity) {

    }
    public void saveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }
    public void restoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {

    }
    public void destroy (Activity activity) {
        mActivityRef.clear();
    }

    public Activity getActivity () {
        return mActivityRef.get();
    }
}
