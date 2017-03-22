package com.nulldreams.wowpaper.presenter;

import android.app.Activity;

import com.nulldreams.base.mvp.ActivityPresenter;
import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.modules.Category;
import com.nulldreams.wowpaper.modules.PaperResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gaoyunfei on 2017/3/22.
 */

public abstract class WowPresenter extends ActivityPresenter {

    private WowView mView;
    private Category mCategory;
    private String mType;

    private int mPage = 1;

    public WowPresenter (Activity activity, WowView view, Category category, String type) {
        this (activity, view, category, type, 1);

        loadNavList();
    }

    public WowPresenter (Activity activity, WowView view, Category category, String type, int startPage) {
        super(activity);
        mView = view;
        mCategory = category;
        mType = type;
        mPage = startPage;

        loadNavList();
    }

    public WowView getWowView () {
        return mView;
    }

    abstract void loadNavList ();

    public void loadPaperList () {
        loadPaperList(mCategory);
    }

    public void reloadPaperList () {
        mPage = 1;
        loadPaperList();
    }

    public void loadNextPagerList () {
        loadPaperList();
    }

    protected void loadPaperList (Category category) {
        mView.onPaperListLoading(mPage);
        doPaperList(category);
    }

    protected void doPaperList (Category category) {
        ApiManager.getInstance(getActivity()).getWallpapersWithId(mType, category.id, mPage, new Callback<PaperResult>() {
            @Override
            public void onResponse(Call<PaperResult> call, Response<PaperResult> response) {
                mView.onPaperListPrepared(response.body().wallpapers, mPage);
                mPage++;
            }

            @Override
            public void onFailure(Call<PaperResult> call, Throwable t) {
                mView.onPaperListFailed();
            }
        });
    }

    @Override
    public void destroy(Activity activity) {
        super.destroy(activity);
        mView = null;
    }
}
