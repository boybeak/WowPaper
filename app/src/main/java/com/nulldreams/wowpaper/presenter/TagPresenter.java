package com.nulldreams.wowpaper.presenter;

import android.app.Activity;

import com.nulldreams.wowpaper.modules.Category;

/**
 * Created by boybe on 2017/3/23.
 */

public class TagPresenter extends WowPresenter {

    public TagPresenter(Activity activity, WowView view, Category category, String type) {
        super(activity, view, category, type);
    }

    public TagPresenter(Activity activity, WowView view, Category category, String type, int startPage) {
        super(activity, view, category, type, startPage);
    }

    @Override
    void loadNavList() {
        //ApiManager.getInstance(getActivity()).getT
    }

    @Override
    boolean needLockDrawerLayout() {
        return true;
    }
}
