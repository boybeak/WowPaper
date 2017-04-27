package com.nulldreams.wowpaper.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.nulldreams.adapter.DelegateAction;
import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.DelegateFilter;
import com.nulldreams.adapter.DelegateParser;
import com.nulldreams.adapter.SimpleFilter;
import com.nulldreams.adapter.impl.LayoutImpl;
import com.nulldreams.adapter.widget.OnScrollBottomListener;
import com.nulldreams.base.fragment.AbsPagerFragment;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.WowHelper;
import com.nulldreams.wowpaper.adapter.decoration.PaperDecoration;
import com.nulldreams.wowpaper.adapter.delegate.FooterDelegate;
import com.nulldreams.wowpaper.adapter.delegate.PaperDelegate;
import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.modules.Paper;
import com.nulldreams.wowpaper.modules.PaperResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gaoyunfei on 2017/3/18.
 */

public abstract class PaperListFragment extends AbsPagerFragment
        implements SwipeRefreshLayout.OnRefreshListener, Callback<PaperResult> {

    private static final String TAG = PaperListFragment.class.getSimpleName();

    /*public static PaperListFragment newInstance (String title, String method) {
        PaperListFragment fragment = new PaperListFragment();
        fragment.setTitle(title);
        fragment.setMethod(method);
        return fragment;
    }*/

    private String mTitle;

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
            loadPaperList();
        }
    };

    private SharedPreferences.OnSharedPreferenceChangeListener mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.pref_key_show_info_grid))) {
                final boolean show = sharedPreferences.getBoolean(key, true);
                mAdapter.actionWith(new DelegateAction() {
                    @Override
                    public void onAction(LayoutImpl impl) {
                        if (impl instanceof PaperDelegate) {
                            ((PaperDelegate)impl).setShowInfo(show);
                        }
                    }
                });
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    private FooterDelegate mFooter = null;

    /*@Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        }
        return AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
    }*/

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
        mSrl.setProgressViewOffset(false, 0, WowHelper.getSrlOffset(getContext()));

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
        if (!TextUtils.isEmpty(getStaticFooterMsg())){
            mFooter.setStaticMsg(getStaticFooterMsg());
        }

        if (savedInstanceState != null) {
            ArrayList<Paper> papers = savedInstanceState.getParcelableArrayList("papers");
            int position = savedInstanceState.getInt("position");
            mPage = savedInstanceState.getInt("page");
            //mMethod = savedInstanceState.getString("method");
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
                    loadPaperList();
                }
            });
        }

        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(mPrefListener);

    }

    public CharSequence getStaticFooterMsg () {
        return null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mAdapter != null && mAdapter.isEmpty() && !mSrl.isRefreshing()) {
            mSrl.post(new Runnable() {
                @Override
                public void run() {
                    mSrl.setRefreshing(true);
                    loadPaperList();
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
            //outState.putString("method", mMethod);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void actionCommand(int command, Bundle bundle) {
        if (command == 1) {
            mRv.scrollToPosition(0);
        } else if (command == 2) {
            mRv.scrollToPosition(0);
            mSrl.post(new Runnable() {
                @Override
                public void run() {
                    mSrl.setRefreshing(true);
                    loadPaperList();
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSrl.isRefreshing()) {
            mSrl.setRefreshing(false);
        }
        mSrl.setOnRefreshListener(null);
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(mPrefListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRv.removeOnScrollListener(mBottomListener);
    }

    @Override
    public void onResponse(Call<PaperResult> call, Response<PaperResult> response) {
        if (isDetached()) {
            return;
        }
        onPaperList(response.body().wallpapers);
    }

    protected void onPaperList (List<Paper> paperList) {
        isLoading = false;

        if (mSrl.isRefreshing()) {
            mSrl.setRefreshing(false);
        }
        if (mPage == 1) {
            mAdapter.clear();
        }
        final int countBefore = mAdapter.getItemCount();
        final boolean show = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(getString(R.string.pref_key_show_info_grid), true);
        mAdapter.addAllAtFirst(new DelegateFilter() {
            @Override
            public boolean accept(DelegateAdapter adapter, LayoutImpl impl) {
                return impl == mFooter;
            }
        }, paperList, new DelegateParser<Paper>() {
            @Override
            public LayoutImpl parse(DelegateAdapter adapter, Paper data) {
                PaperDelegate delegate = new PaperDelegate(data);
                delegate.setShowInfo(show);
                return delegate;
            }
        }).autoNotify();
        mFooter.setState(FooterDelegate.STATE_SUCCESS);
        if (!TextUtils.isEmpty(getStaticFooterMsg())) {
            mFooter.setStaticMsg(getStaticFooterMsg());
        }
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
        if (isDetached()) {
            return;
        }
        if (mSrl.isRefreshing()) {
            mSrl.setRefreshing(false);
        }
        mAdapter.addIfNotExist(mFooter);
        mFooter.setState(FooterDelegate.STATE_FAILED);
        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
        Toast.makeText(getContext(), R.string.toast_load_data_failed, Toast.LENGTH_SHORT).show();
    }

    private void loadPaperList () {
        isLoading = true;
        if (mAdapter.endWith(mFooter)) {
            mFooter.setState(FooterDelegate.STATE_LOADING);
            mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
        }
        loadPaperList(mPage);
    }

    protected abstract void loadPaperList (int page);

    public void setTitle (String title) {
        mTitle = title;
    }

    protected DelegateAdapter getAdapter () {
        return mAdapter;
    }

    /*public void setMethod (String method) {
        mMethod = method;
    }*/

    @Override
    public void onRefresh() {
        mPage = 1;
        loadPaperList();
    }
}
