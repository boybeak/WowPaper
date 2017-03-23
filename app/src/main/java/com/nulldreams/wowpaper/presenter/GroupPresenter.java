package com.nulldreams.wowpaper.presenter;

import android.app.Activity;

import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.modules.Category;
import com.nulldreams.wowpaper.modules.GroupResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by boybe on 2017/3/23.
 */

public class GroupPresenter extends WowPresenter {

    public GroupPresenter(Activity activity, WowView view, Category category, String type) {
        super(activity, view, category, type);
    }

    public GroupPresenter(Activity activity, WowView view, Category category, String type, int startPage) {
        super(activity, view, category, type, startPage);
    }

    @Override
    void loadNavList() {
        getWowView().onNavListLoading();
        ApiManager.getInstance(getActivity()).getGroups(new Callback<GroupResult>() {
            @Override
            public void onResponse(Call<GroupResult> call, Response<GroupResult> response) {
                if (getWowView() != null) {
                    List<Category> categories = response.body().groups;
                    getWowView().onNavListPrepared(categories);
                    if (categories != null && !categories.isEmpty() && getCategory() == null) {
                        selectItem(categories.get(0));
                    }
                }
            }

            @Override
            public void onFailure(Call<GroupResult> call, Throwable t) {
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
