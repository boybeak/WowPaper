package com.nulldreams.wowpaper.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.nulldreams.adapter.DelegateAction;
import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.DelegateFilter;
import com.nulldreams.adapter.DelegateParser;
import com.nulldreams.adapter.impl.LayoutImpl;
import com.nulldreams.base.utils.WeakAsyncTask;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.decoration.PaperDecoration;
import com.nulldreams.wowpaper.adapter.delegate.FooterDelegate;
import com.nulldreams.wowpaper.adapter.delegate.PaperDelegate;
import com.nulldreams.wowpaper.manager.LikeManager;
import com.nulldreams.wowpaper.modules.Paper;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class LikeActivity extends WowActivity implements LikeManager.Callback{

    private static final String TAG = LikeActivity.class.getSimpleName();

    @Override
    public void onLikeEvent(boolean like, final Paper paper) {
        if (like) {
            mAdapter.add(mAdapter.getItemCount() - 1, new PaperDelegate(paper));
            mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
        } else {
            int index = mAdapter.firstIndexOf(new DelegateFilter() {
                @Override
                public boolean accept(DelegateAdapter adapter, LayoutImpl impl) {
                    if (impl instanceof PaperDelegate) {
                        PaperDelegate delegate = (PaperDelegate)impl;
                        return delegate.getSource().equals(paper);
                    }
                    return false;
                }
            });
            if (index >= 0 && index < mAdapter.getItemCount()) {
                mAdapter.remove(index);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

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

    private class LoadTask extends WeakAsyncTask<Void, Integer, List<Paper>, LikeActivity> {

        public LoadTask(LikeActivity likeActivity) {
            super(likeActivity);
        }

        @Override
        protected void onPreExecute(LikeActivity likeActivity) {
            likeActivity.onLoadStart();
        }

        @Override
        protected List<Paper> doInBackground(LikeActivity likeActivity, Void... params) {
            return LikeManager.getInstance(likeActivity).findAll();
        }

        @Override
        protected void onPostExecute(LikeActivity likeActivity, List<Paper> paperList) {
            likeActivity.onPaperList(paperList);
        }
    }

    @ViewInject(R.id.like_toolbar)
    private Toolbar mTb;
    @ViewInject(R.id.like_rv)
    private RecyclerView mRv;

    private DelegateAdapter mAdapter;

    private FooterDelegate mFooter;

    private int mPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);

        x.view().inject(this);

        setSupportActionBar(mTb);
        setTitle(R.string.title_menu_like);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mTb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRv.scrollToPosition(0);
            }
        });

        final int spanCount = getResources().getInteger(R.integer.span_count);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                if (mAdapter.getItemCount() - 1 == i) {
                    return spanCount;
                }
                return 1;
            }
        });
        mRv.setLayoutManager(gridLayoutManager);
        mRv.addItemDecoration(new PaperDecoration(this));
        mAdapter = new DelegateAdapter(this);
        mRv.setAdapter(mAdapter);

        mFooter = new FooterDelegate();
        mFooter.setStaticMsg(getString(R.string.text_like_will_lost_when_uninstalled));
        LikeManager.getInstance(this).registerCallback(this);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mPrefListener);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt("position", 0);
        }
        loadData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        GridLayoutManager gridLayoutManager = (GridLayoutManager)mRv.getLayoutManager();
        int position = gridLayoutManager.findFirstVisibleItemPosition();
        outState.putInt("position", position);
        super.onSaveInstanceState(outState);
    }

    private WeakAsyncTask<Void, Integer, List<Paper>, LikeActivity> mAsyncTask = null;
    private void loadData () {
        new LoadTask (this).execute();
    }

    protected void onLoadStart () {
        mFooter.setState(FooterDelegate.STATE_LOADING);
        mAdapter.addIfNotExist(mFooter);
        mAdapter.notifyDataSetChanged();
    }

    protected void onPaperList (List<Paper> paperList) {
        final boolean show = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                getString(R.string.pref_key_show_info_grid), true
        );
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
        });
        mFooter.setState(FooterDelegate.STATE_SUCCESS);
        mAdapter.notifyDataSetChanged();
        if (mPosition > 0) {
            mRv.scrollToPosition(mPosition);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LikeManager.getInstance(this).unregisterCallback(this);
        if (mAsyncTask != null && !mAsyncTask.isCancelled()) {
            mAsyncTask.cancel(true);
        }

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mPrefListener);
    }
}
