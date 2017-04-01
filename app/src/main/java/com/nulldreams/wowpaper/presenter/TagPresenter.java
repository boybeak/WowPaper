package com.nulldreams.wowpaper.presenter;

import android.app.Activity;

import com.nulldreams.wowpaper.modules.Filter;

/**
 * Created by boybe on 2017/3/23.
 */

public class TagPresenter extends WowPresenter {

    public TagPresenter(Activity activity, WowView view, Filter filter, String type) {
        super(activity, view, filter, type);
    }

    public TagPresenter(Activity activity, WowView view, Filter filter, String type, int startPage) {
        super(activity, view, filter, type, startPage);
    }

    @Override
    void loadNavList() {
        //ApiManager.getInstance(getActivity()).getT
    }

    @Override
    public boolean needLockDrawerLayout() {
        return true;
    }
}
