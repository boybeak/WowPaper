package com.nulldreams.wowpaper.fragment;

import com.nulldreams.wowpaper.manager.ApiManager;

/**
 * Created by gaoyunfei on 2017/4/6.
 */

public class HomeFragment extends PaperListFragment {
    @Override
    protected void loadPaperList(int page) {
        ApiManager.getInstance(getContext()).getWallpapers(page, ApiManager.WOW_TYPE_NEWEST, this);
    }
}
