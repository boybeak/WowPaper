package com.nulldreams.wowpaper.presenter;

import com.nulldreams.base.mvp.ViewImpl;
import com.nulldreams.wowpaper.modules.Filter;
import com.nulldreams.wowpaper.modules.Paper;

import java.util.List;

/**
 * Created by gaoyunfei on 2017/3/22.
 */

public interface WowView extends ViewImpl {

    void onItemChanged(Filter filter, boolean userAction);

    void onNavListLoading ();
    void onNavListPrepared (List<Filter> filterList);
    void onNavListFailed ();

    void onPaperListLoading (int page);
    void onPaperListPrepared (List<Paper> paperList, int page);
    void onPaperListFailed ();

    void onLockDragLayout (boolean needLock);
}
