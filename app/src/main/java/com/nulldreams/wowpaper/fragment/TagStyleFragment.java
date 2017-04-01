package com.nulldreams.wowpaper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.DelegateParser;
import com.nulldreams.adapter.impl.LayoutImpl;
import com.nulldreams.base.fragment.AbsPagerFragment;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.delegate.TagStyleDelegate;
import com.nulldreams.wowpaper.adapter.delegate.TitleDelegate;
import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.modules.Filter;
import com.nulldreams.wowpaper.modules.CategoryResult;
import com.nulldreams.wowpaper.modules.CollectionResult;
import com.nulldreams.wowpaper.modules.GroupResult;

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

    private ArrayList<Filter> mCategories, mCollections, mGroups;

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
                mAdapter.add(new TitleDelegate(getString(R.string.title_category)));
                mAdapter.addAll(mCategories, new DelegateParser<Filter>() {
                    @Override
                    public LayoutImpl parse(DelegateAdapter adapter, Filter data) {
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
        if (mSrl.isRefreshing()) {
            mSrl.setRefreshing(false);
        }
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
        ApiManager.getInstance(getContext()).getCategories(new Callback<CategoryResult>() {
            @Override
            public void onResponse(Call<CategoryResult> call, Response<CategoryResult> response) {
                if (mSrl.isRefreshing()) {
                    mSrl.setRefreshing(false);
                }
                mCategories = response.body().categories;
                addDataList(getString(R.string.title_category), mCategories, TagStyleDelegate.STYLE_CATEGORY);
            }

            @Override
            public void onFailure(Call<CategoryResult> call, Throwable t) {
                if (mSrl.isRefreshing()) {
                    mSrl.setRefreshing(false);
                }
                Toast.makeText(getContext(), R.string.toast_load_data_failed, Toast.LENGTH_SHORT).show();
            }
        });

        ApiManager.getInstance(getContext()).getCollections(new Callback<CollectionResult>() {
            @Override
            public void onResponse(Call<CollectionResult> call, Response<CollectionResult> response) {
                if (mSrl.isRefreshing()) {
                    mSrl.setRefreshing(false);
                }
                mCategories = response.body().collections;
                addDataList(getString(R.string.title_collection), mCategories, TagStyleDelegate.STYLE_COLLECTION);
            }

            @Override
            public void onFailure(Call<CollectionResult> call, Throwable t) {
                if (mSrl.isRefreshing()) {
                    mSrl.setRefreshing(false);
                }
                Toast.makeText(getContext(), R.string.toast_load_data_failed, Toast.LENGTH_SHORT).show();
            }
        });
        ApiManager.getInstance(getContext()).getGroups(new Callback<GroupResult>() {
            @Override
            public void onResponse(Call<GroupResult> call, Response<GroupResult> response) {
                if (mSrl.isRefreshing()) {
                    mSrl.setRefreshing(false);
                }
                mGroups = response.body().groups;
                addDataList(getString(R.string.title_group), mGroups, TagStyleDelegate.STYLE_GROUP);
            }

            @Override
            public void onFailure(Call<GroupResult> call, Throwable t) {
                if (mSrl.isRefreshing()) {
                    mSrl.setRefreshing(false);
                }
                Toast.makeText(getContext(), R.string.toast_load_data_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private synchronized void addDataList (String title, List<Filter> categories, @TagStyleDelegate.Style final String style) {
        if (categories == null || categories.isEmpty()) {
            return;
        }
        mAdapter.add(new TitleDelegate(title));
        mAdapter.addAll(categories, new DelegateParser<Filter>() {
            @Override
            public LayoutImpl parse(DelegateAdapter adapter, Filter data) {
                return new TagStyleDelegate(data).setStyle(style);
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
        loadData();
    }
}
