package com.nulldreams.wowpaper.activity;

import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.DelegateFilter;
import com.nulldreams.adapter.DelegateParser;
import com.nulldreams.adapter.SimpleFilter;
import com.nulldreams.adapter.impl.LayoutImpl;
import com.nulldreams.adapter.widget.OnScrollBottomListener;
import com.nulldreams.base.utils.Intents;
import com.nulldreams.base.utils.UiHelper;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.decoration.PaperDecoration;
import com.nulldreams.wowpaper.adapter.delegate.FooterDelegate;
import com.nulldreams.wowpaper.adapter.delegate.PaperDelegate;
import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.modules.Category;
import com.nulldreams.wowpaper.modules.Paper;
import com.nulldreams.wowpaper.modules.PaperResult;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaperListActivity extends WowActivity implements SwipeRefreshLayout.OnRefreshListener{

    private Toolbar mTb;
    private RecyclerView mRv;
    private DelegateAdapter mAdapter;

    private Category mCategory;
    private String mType;

    private int mPage = 1;
    private boolean isLoading = false;

    private SwipeRefreshLayout mSrl;

    private OnScrollBottomListener mBottomListener = new OnScrollBottomListener() {
        @Override
        public void onScrollBottom(RecyclerView recyclerView, int newState) {
            if (isLoading) {
                return;
            }
            loadData();
        }
    };

    private DelegateParser<Paper> mPaperParser = new DelegateParser<Paper>() {
        @Override
        public LayoutImpl parse(DelegateAdapter adapter, Paper data) {
            return new PaperDelegate(data);
        }
    };

    private FooterDelegate mFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_list);

        mCategory = getIntent().getParcelableExtra("style");
        mType = getIntent().getStringExtra("type");

        final int spanCount = getResources().getInteger(R.integer.span_count);

        mTb = (Toolbar)findViewById(R.id.paper_list_tb);
        setSupportActionBar(mTb);

        mSrl = (SwipeRefreshLayout)findViewById(R.id.paper_list_srl);
        mSrl.setColorSchemeResources(R.color.colorAccent);
        mSrl.setOnRefreshListener(this);
        mSrl.setProgressViewOffset(false, 0, UiHelper.getActionBarSize(this));

        mRv = (RecyclerView)findViewById(R.id.paper_list_rv);
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
        mRv.addOnScrollListener(mBottomListener);

        mFooter = new FooterDelegate (FooterDelegate.STATE_NONE);

        if (savedInstanceState != null) {
            ArrayList<Paper> papers = savedInstanceState.getParcelableArrayList("papers");
            final int countBefore = mAdapter.getItemCount();
            mAdapter.addAll(papers, mPaperParser);
            mFooter.setState(FooterDelegate.STATE_SUCCESS);
            mAdapter.addIfNotExist(mFooter);
            mAdapter.notifyItemRangeInserted(countBefore, mAdapter.getItemCount() - countBefore);
            mPage = savedInstanceState.getInt("page");
            final int position = savedInstanceState.getInt("position");
            mRv.scrollToPosition(position);
            mCategory = savedInstanceState.getParcelable("style");
            mType = savedInstanceState.getString("type");
        } else {
            mSrl.post(new Runnable() {
                @Override
                public void run() {
                    mSrl.setRefreshing(true);
                    loadData();
                }
            });
        }

        setTitle(mCategory.name);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (mAdapter != null && !mAdapter.isEmpty()) {
            ArrayList<Paper> papers = mAdapter.getDataSourceArrayList(new SimpleFilter<Paper>(Paper.class));
            final int position = ((GridLayoutManager)mRv.getLayoutManager()).findFirstVisibleItemPosition();
            outState.putParcelableArrayList("papers", papers);
            outState.putInt("position", position);
        }
        outState.putInt("page", mPage);
        outState.putParcelable("style", mCategory);
        outState.putString("type", mType);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!TextUtils.isEmpty(mCategory.url)) {
            getMenuInflater().inflate(R.menu.menu_paper_list, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        switch (item.getItemId()) {
            case R.id.paper_list_share:
                if (TextUtils.isEmpty(mCategory.url)) {
                    return true;
                }
                try {
                    Intents.shareText(this, R.string.title_dialog_share, mCategory.url);
                } catch (Exception e) {
                    Toast.makeText(this, R.string.toast_no_app_response, Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*private void uiVisibility () {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }*/

    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            uiVisibility();
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRv.removeOnScrollListener(mBottomListener);
        mSrl.setOnRefreshListener(null);
    }

    private void loadData () {
        isLoading = true;
        if (mAdapter.endWith(mFooter)) {
            mFooter.setState(FooterDelegate.STATE_LOADING);
            mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
        }
        ApiManager.getInstance(this).getWallpapersWithId(mType, mCategory.id, mPage, new Callback<PaperResult>() {
            @Override
            public void onResponse(Call<PaperResult> call, Response<PaperResult> response) {
                isLoading = false;
                if (mPage == 1) {
                    mAdapter.clear();
                }
                if (mSrl.isRefreshing()) {
                    mSrl.setRefreshing(false);
                }
                final int countBefore = mAdapter.getItemCount();
                mAdapter.addAllAtFirst(new DelegateFilter() {
                    @Override
                    public boolean accept(DelegateAdapter adapter, LayoutImpl impl) {
                        return impl == mFooter;
                    }
                }, response.body().wallpapers, mPaperParser);

                mAdapter.addIfNotExist(mFooter);
                mFooter.setState(FooterDelegate.STATE_SUCCESS);
                mAdapter.notifyItemRangeInserted(countBefore, mAdapter.getItemCount() - countBefore);
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
                Toast.makeText(PaperListActivity.this, R.string.toast_load_data_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        if (isLoading) {
            mSrl.setRefreshing(false);
            return;
        }
        mPage = 1;
        loadData();
    }
}
