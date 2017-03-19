package com.nulldreams.wowpaper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.DelegateParser;
import com.nulldreams.adapter.impl.LayoutImpl;
import com.nulldreams.base.fragment.AbsPagerFragment;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.delegate.TagStyleDelegate;
import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.modules.Category;
import com.nulldreams.wowpaper.modules.CategoryResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gaoyunfei on 2017/3/19.
 */

public class TagStyleFragment extends AbsPagerFragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = TagStyleFragment.class.getSimpleName();

    public static TagStyleFragment newInstance () {
        return new TagStyleFragment();
    }

    private SwipeRefreshLayout mSrl;
    private RecyclerView mRv;

    private DelegateAdapter mAdapter;

    private boolean isLoading;

    private ArrayList<Category> mCategories;

    @Override
    public CharSequence getTitle(Context context, Bundle bundle) {
        return context.getString(R.string.title_tag_style);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_paper_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSrl = (SwipeRefreshLayout)view.findViewById(R.id.paper_list_srl);
        mSrl.setColorSchemeResources(R.color.colorAccent);
        mSrl.setOnRefreshListener(this);

        mRv = (RecyclerView)view.findViewById(R.id.paper_list_rv);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager();
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        mRv.setLayoutManager(layoutManager);
        mAdapter = new DelegateAdapter(getContext());
        mRv.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            mCategories = savedInstanceState.getParcelableArrayList("categories");
            if (mCategories != null && !mCategories.isEmpty()) {
                mAdapter.clear();
                mAdapter.addAll(mCategories, new DelegateParser<Category>() {
                    @Override
                    public LayoutImpl parse(DelegateAdapter adapter, Category data) {
                        return new TagStyleDelegate(data).setStyle(TagStyleDelegate.STYLE_CATEGORY);
                    }
                });
                mAdapter.notifyDataSetChanged();
            }

        }

        Log.v(TAG, "TAG onViewCreated");

        if (mAdapter.isEmpty() && getUserVisibleHint() && !mSrl.isRefreshing()) {
            Log.v(TAG, "TAG onViewCreated loadData");
            mSrl.post(new Runnable() {
                @Override
                public void run() {
                    mSrl.setRefreshing(true);
                    loadData();
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mCategories != null) {
            outState.putParcelableArrayList("categories", mCategories);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSrl.setOnRefreshListener(null);
        Log.v(TAG, "TAG onDestroyView");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mAdapter != null && mAdapter.isEmpty() && !mSrl.isRefreshing()) {
            Log.v(TAG, "TAG setUserVisibleHint");
            mSrl.post(new Runnable() {
                @Override
                public void run() {
                    mSrl.setRefreshing(true);
                    loadData();
                }
            });
        }
    }

    private void loadData () {
        isLoading = true;
        ApiManager.getInstance(getContext()).getCategories(new Callback<CategoryResult>() {
            @Override
            public void onResponse(Call<CategoryResult> call, Response<CategoryResult> response) {
                isLoading = false;
                if (mSrl.isRefreshing()) {
                    mSrl.setRefreshing(false);
                }
                mCategories = response.body().categories;
                mAdapter.clear();
                mAdapter.addAll(mCategories, new DelegateParser<Category>() {
                    @Override
                    public LayoutImpl parse(DelegateAdapter adapter, Category data) {
                        return new TagStyleDelegate(data).setStyle(TagStyleDelegate.STYLE_CATEGORY);
                    }
                });
            }

            @Override
            public void onFailure(Call<CategoryResult> call, Throwable t) {
                isLoading = false;
                if (mSrl.isRefreshing()) {
                    mSrl.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        if (isLoading) {
            return;
        }
        loadData();
    }
}
