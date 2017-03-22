package com.nulldreams.wowpaper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.DelegateFilter;
import com.nulldreams.adapter.DelegateParser;
import com.nulldreams.adapter.SimpleFilter;
import com.nulldreams.adapter.impl.LayoutImpl;
import com.nulldreams.adapter.widget.OnScrollBottomListener;
import com.nulldreams.base.fragment.AbsPagerFragment;
import com.nulldreams.base.utils.UiHelper;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.decoration.PaperDecoration;
import com.nulldreams.wowpaper.adapter.delegate.FooterDelegate;
import com.nulldreams.wowpaper.adapter.delegate.PaperDelegate;
import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.modules.Paper;
import com.nulldreams.wowpaper.modules.PaperResult;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gaoyunfei on 2017/3/18.
 */

public class PaperListFragment extends AbsPagerFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = PaperListFragment.class.getSimpleName();

    public static PaperListFragment newInstance (String title, String method) {
        PaperListFragment fragment = new PaperListFragment();
        fragment.setTitle(title);
        fragment.setMethod(method);
        return fragment;
    }

    private String mTitle, mMethod;

    private SwipeRefreshLayout mSrl;
    private RecyclerView mRv;
    private DelegateAdapter mAdapter;

    private int mPage = 1;
    private boolean isLoading = false;
    private OnScrollBottomListener mBottomListener = new OnScrollBottomListener() {
        @Override
        public void onScrollBottom(RecyclerView recyclerView, int newState) {
            if (isLoading) {
                return;
            }
            loadData();
        }
    };

    private FooterDelegate mFooter = null;

    @Override
    public CharSequence getTitle(Context context, Bundle bundle) {
        return mTitle;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_paper_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int spanCount = getResources().getInteger(R.integer.span_count);

        mSrl = (SwipeRefreshLayout)view.findViewById(R.id.paper_list_srl);
        mSrl.setOnRefreshListener(this);
        mSrl.setColorSchemeResources(R.color.colorAccent);
        mSrl.setProgressViewOffset(false, 0, UiHelper.getActionBarSize(getContext()));

        mRv = (RecyclerView)view.findViewById(R.id.paper_list_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                if (i == mAdapter.getItemCount() - 1) {
                    return spanCount;
                }
                return 1;
            }
        });
        mRv.setLayoutManager(gridLayoutManager);
        mRv.addItemDecoration(new PaperDecoration(getContext()));
        mRv.addOnScrollListener(mBottomListener);
        mAdapter = new DelegateAdapter(getContext());
        mRv.setAdapter(mAdapter);

        mFooter = new FooterDelegate(FooterDelegate.STATE_NONE);

        if (savedInstanceState != null) {
            ArrayList<Paper> papers = savedInstanceState.getParcelableArrayList("papers");
            int position = savedInstanceState.getInt("position");
            mPage = savedInstanceState.getInt("page");
            if (papers != null && !papers.isEmpty()) {
                final int countBefore = mAdapter.getItemCount();
                mAdapter.addAll(papers, new DelegateParser<Paper>() {
                    @Override
                    public LayoutImpl parse(DelegateAdapter adapter, Paper data) {
                        return new PaperDelegate(data);
                    }
                });
                mFooter.setState(FooterDelegate.STATE_SUCCESS);
                mAdapter.addIfNotExist(mFooter);
                mAdapter.notifyItemRangeInserted(countBefore, mAdapter.getItemCount() - countBefore);
                mRv.scrollToPosition(position);
            }
        }

        if (mAdapter.isEmpty() && getUserVisibleHint() && !mSrl.isRefreshing()) {
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mAdapter != null && mAdapter.isEmpty() && !mSrl.isRefreshing()) {
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
        if (mAdapter != null && !mAdapter.isEmpty()) {
            ArrayList<Paper> papers = mAdapter.getDataSourceArrayList(new SimpleFilter<Paper>(Paper.class));
            outState.putParcelableArrayList("papers", papers);
            GridLayoutManager layoutManager = (GridLayoutManager)mRv.getLayoutManager();
            outState.putInt("position", layoutManager.findFirstVisibleItemPosition());
            outState.putInt("page", mPage);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRv.removeOnScrollListener(mBottomListener);
    }

    private void loadData () {
        isLoading = true;
        if (mAdapter.endWith(mFooter)) {
            mFooter.setState(FooterDelegate.STATE_LOADING);
            mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
        }
        ApiManager.getInstance(getContext()).getWallpapers(mPage, mMethod, new Callback<PaperResult>() {
            @Override
            public void onResponse(Call<PaperResult> call, Response<PaperResult> response) {
                isLoading = false;

                if (mSrl.isRefreshing()) {
                    mSrl.setRefreshing(false);
                }
                if (mPage == 1) {
                    mAdapter.clear();
                }
                final int countBefore = mAdapter.getItemCount();
                mAdapter.addAllAtFirst(new DelegateFilter() {
                    @Override
                    public boolean accept(DelegateAdapter adapter, LayoutImpl impl) {
                        return impl == mFooter;
                    }
                }, response.body().wallpapers, new DelegateParser<Paper>() {
                    @Override
                    public LayoutImpl parse(DelegateAdapter adapter, Paper data) {
                        PaperDelegate delegate = new PaperDelegate(data);
                        //delegate.putInt("width", mPaperWidth);
                        return delegate;
                    }
                });
                mFooter.setState(FooterDelegate.STATE_SUCCESS);
                mAdapter.addIfNotExist(mFooter);
                final int count = mAdapter.getItemCount() - countBefore;
                if (mPage == 1) {
                    mAdapter.notifyDataSetChanged();
                } else {
                    if (count == 0) {
                        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
                    } else {
                        mAdapter.notifyItemRangeInserted(countBefore, count);
                    }
                }
                mPage++;
            }

            @Override
            public void onFailure(Call<PaperResult> call, Throwable t) {
                isLoading = false;
                if (mSrl.isRefreshing()) {
                    mSrl.setRefreshing(false);
                }
                mAdapter.addIfNotExist(mFooter);
                mFooter.setState(FooterDelegate.STATE_FAILED);
                mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
                Toast.makeText(getContext(), R.string.toast_load_data_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setTitle (String title) {
        mTitle = title;
    }

    public void setMethod (String method) {
        mMethod = method;
    }

    @Override
    public void onRefresh() {
        mPage = 1;
        loadData();
    }
}
