package com.nulldreams.wowpaper.presenter;

import android.app.Activity;

import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.modules.Filter;
import com.nulldreams.wowpaper.modules.GroupResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by boybe on 2017/3/23.
 */

public class GroupPresenter extends WowPresenter {

    public GroupPresenter(Activity activity, WowView view, Filter filter, String type) {
        super(activity, view, filter, type);
    }

    public GroupPresenter(Activity activity, WowView view, Filter filter, String type, int startPage) {
        super(activity, view, filter, type, startPage);
    }

    @Override
    void loadNavList() {
        getWowView().onNavListLoading();
        ApiManager.getInstance(getActivity()).getGroups(new Callback<GroupResult>() {
            @Override
            public void onResponse(Call<GroupResult> call, Response<GroupResult> response) {
                if (getWowView() != null) {
                    List<Filter> categories = response.body().groups;
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
    public boolean needLockDrawerLayout() {
        return false;
    }
}
