package com.nulldreams.wowpaper.activity;

import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.DelegateParser;
import com.nulldreams.adapter.SimpleFilter;
import com.nulldreams.adapter.impl.LayoutImpl;
import com.nulldreams.adapter.widget.OnScrollBottomListener;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.decoration.PaperDecoration;
import com.nulldreams.wowpaper.adapter.delegate.PaperDelegate;
import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.modules.Category;
import com.nulldreams.wowpaper.modules.Paper;
import com.nulldreams.wowpaper.modules.PaperResult;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaperListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_list);

        mCategory = getIntent().getParcelableExtra("style");
        mType = getIntent().getStringExtra("type");

        final int spanCount = getResources().getInteger(R.integer.span_count);

        mSrl = (SwipeRefreshLayout)findViewById(R.id.paper_list_srl);
        mSrl.setColorSchemeResources(R.color.colorAccent);
        mSrl.setOnRefreshListener(this);

        mRv = (RecyclerView)findViewById(R.id.paper_list_rv);
        mRv.setLayoutManager(new GridLayoutManager(this, spanCount));
        mRv.addItemDecoration(new PaperDecoration(this));
        mAdapter = new DelegateAdapter(this);
        mRv.setAdapter(mAdapter);
        mRv.addOnScrollListener(mBottomListener);

        if (savedInstanceState != null) {
            ArrayList<Paper> papers = savedInstanceState.getParcelableArrayList("papers");
            mAdapter.addAll(papers, mPaperParser);
            mAdapter.notifyDataSetChanged();
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

        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRv.removeOnScrollListener(mBottomListener);
        mSrl.setOnRefreshListener(null);
    }

    private void loadData () {
        isLoading = true;
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
                mAdapter.addAll(response.body().wallpapers, mPaperParser);
                mAdapter.notifyDataSetChanged();
                mPage++;
            }

            @Override
            public void onFailure(Call<PaperResult> call, Throwable t) {
                isLoading = false;
                if (mSrl.isRefreshing()) {
                    mSrl.setRefreshing(false);
                }
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
