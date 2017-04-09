package com.nulldreams.wowpaper.fragment;

import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.manager.LikeManager;

/**
 * Created by gaoyunfei on 2017/4/7.
 */

public class LikeFragment extends PaperListFragment {
    @Override
    protected void loadPaperList(int page) {
        if (page == 1) {
            onPaperList(LikeManager.getInstance(getContext()).findAll());
        } else {
            onPaperList(null);
        }
    }

    @Override
    public CharSequence getStaticFooterMsg() {
        return getContext().getString(R.string.text_like_will_lost_when_uninstalled);
    }
}
