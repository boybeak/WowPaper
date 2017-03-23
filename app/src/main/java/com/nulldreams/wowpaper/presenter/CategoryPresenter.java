package com.nulldreams.wowpaper.presenter;

import android.app.Activity;

import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.modules.Category;
import com.nulldreams.wowpaper.modules.CategoryResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gaoyunfei on 2017/3/22.
 */

public class CategoryPresenter extends WowPresenter {


    public CategoryPresenter(Activity activity, WowView view, Category category, String type) {
        super(activity, view, category, type);
    }

    public CategoryPresenter(Activity activity, WowView view, Category category, String type, int startPage) {
        super(activity, view, category, type, startPage);
    }

    @Override
    void loadNavList() {
        getWowView().onNavListLoading();
        ApiManager.getInstance(getActivity()).getCategories(new Callback<CategoryResult>() {
            @Override
            public void onResponse(Call<CategoryResult> call, Response<CategoryResult> response) {
                if (getWowView() != null) {
                    List<Category> categories = response.body().categories;
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
    boolean needLockDrawerLayout() {
        return false;
    }
}
