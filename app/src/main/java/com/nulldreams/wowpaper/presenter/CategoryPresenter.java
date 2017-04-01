package com.nulldreams.wowpaper.presenter;

import android.app.Activity;

import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.manager.FilterManager;
import com.nulldreams.wowpaper.modules.Filter;
import com.nulldreams.wowpaper.modules.CategoryResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gaoyunfei on 2017/3/22.
 */

public class CategoryPresenter extends WowPresenter {


    public CategoryPresenter(Activity activity, WowView view, Filter filter, String type) {
        super(activity, view, filter, type);
    }

    public CategoryPresenter(Activity activity, WowView view, Filter filter, String type, int startPage) {
        super(activity, view, filter, type, startPage);
    }

    @Override
    void loadNavList() {
        getWowView().onNavListLoading();
        final List<Filter> filters = FilterManager.getInstance(getActivity()).getCategories();
        if (filters != null && !filters.isEmpty()) {
            if (getWowView() != null) {
                getWowView().onNavListPrepared(filters);
                if (getCategory() == null) {
                    selectItem(filters.get(0));
                }
            }
        }
        ApiManager.getInstance(getActivity()).getCategories(new Callback<CategoryResult>() {
            @Override
            public void onResponse(Call<CategoryResult> call, Response<CategoryResult> response) {
                if (filters != null && !filters.isEmpty()) {
                    return;
                }
                if (getWowView() != null) {
                    List<Filter> categories = response.body().categories;
                    getWowView().onNavListPrepared(categories);
                    if (categories != null && !categories.isEmpty() && getCategory() == null) {
                        selectItem(categories.get(0));
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryResult> call, Throwable t) {
                if (getWowView() != null) {
                    getWowView().onNavListFailed();
                }
            }
        });
    }

    @Override
    public boolean needLockDrawerLayout() {
        return false;
    }
}
