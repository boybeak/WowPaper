package com.nulldreams.wowpaper.presenter;

import android.app.Activity;

import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.modules.Category;
import com.nulldreams.wowpaper.modules.CategoryResult;

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
                getWowView().onNavListPrepared(response.body().categories);
            }

            @Override
            public void onFailure(Call<CategoryResult> call, Throwable t) {
                getWowView().onNavListFailed();
            }
        });
    }
}
