package com.nulldreams.wowpaper.presenter;

import com.nulldreams.base.mvp.ViewImpl;
import com.nulldreams.wowpaper.modules.Category;
import com.nulldreams.wowpaper.modules.Paper;

import java.util.List;

/**
 * Created by gaoyunfei on 2017/3/22.
 */

public interface WowView extends ViewImpl {

    void onNavListLoading ();
    void onNavListPrepared (List<Category> categoryList);
    void onNavListFailed ();

    void onPaperListLoading (int page);
    void onPaperListPrepared (List<Paper> paperList, int page);
    void onPaperListFailed ();
}
