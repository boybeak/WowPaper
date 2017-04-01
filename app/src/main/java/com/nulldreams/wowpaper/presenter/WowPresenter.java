package com.nulldreams.wowpaper.presenter;

import android.app.Activity;
import android.os.Bundle;

import com.nulldreams.base.mvp.ActivityPresenter;
import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.modules.Filter;
import com.nulldreams.wowpaper.modules.PaperResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gaoyunfei on 2017/3/22.
 */

public abstract class WowPresenter extends ActivityPresenter {

    private WowView mView;
    private Filter mFilter;
    private String mType;

    private int mPage = 1;

    public WowPresenter (Activity activity, WowView view, Filter filter, String type) {
        this (activity, view, filter, type, 1);
    }

    public WowPresenter (Activity activity, WowView view, Filter filter, String type, int startPage) {
        super(activity);
        mView = view;
        mFilter = filter;
        mType = type;
        mPage = startPage;
    }

    @Override
    public void create(Activity activity, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            loadNavList();
        }
        if (mFilter != null) {
            selectItem(mFilter, false);
        }
        mView.onLockDragLayout(needLockDrawerLayout());
    }

    public WowView getWowView () {
        return mView;
    }

    abstract void loadNavList ();

    public abstract boolean needLockDrawerLayout ();

    public void loadPaperList () {
        loadPaperList(mFilter);
    }

    public Filter getCurrentCategory () {
        return mFilter;
    }

    public void reloadPaperList () {
        mPage = 1;
        loadPaperList();
    }

    public Filter getCategory () {
        return mFilter;
    }

    public void selectItem (Filter filter) {
        selectItem(filter, true);
    }

    protected void selectItem(Filter filter, boolean userAction) {
        if ((filter == null || filter.equals(mFilter)) && userAction) {
            return;
        }
        mFilter = filter;
        getWowView().onItemChanged(filter, userAction);
        //loadNavList();
        //reloadPaperList();
    }

    public void loadNextPagerList () {
        loadPaperList();
    }

    protected void loadPaperList (Filter filter) {
        mView.onPaperListLoading(mPage);
        doPaperList(filter);
    }

    protected void doPaperList (Filter filter) {
        ApiManager.getInstance(getActivity()).getWallpapersWithId(mType, filter.id, mPage, new Callback<PaperResult>() {
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

    public int getPage () {
        return mPage;
    }

    @Override
    public void destroy(Activity activity) {
        super.destroy(activity);
        mView = null;
    }
}
