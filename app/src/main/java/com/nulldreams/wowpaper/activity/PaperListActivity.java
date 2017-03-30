package com.nulldreams.wowpaper.activity;

import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.nulldreams.adapter.DelegateAction;
import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.DelegateFilter;
import com.nulldreams.adapter.DelegateParser;
import com.nulldreams.adapter.SimpleFilter;
import com.nulldreams.adapter.impl.LayoutImpl;
import com.nulldreams.adapter.widget.OnScrollBottomListener;
import com.nulldreams.base.utils.Intents;
import com.nulldreams.base.utils.UiHelper;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.WowHelper;
import com.nulldreams.wowpaper.adapter.decoration.PaperDecoration;
import com.nulldreams.wowpaper.adapter.delegate.FooterDelegate;
import com.nulldreams.wowpaper.adapter.delegate.PaperDelegate;
import com.nulldreams.wowpaper.adapter.delegate.TagStyleDelegate;
import com.nulldreams.wowpaper.modules.Category;
import com.nulldreams.wowpaper.modules.Paper;
import com.nulldreams.wowpaper.presenter.CategoryPresenter;
import com.nulldreams.wowpaper.presenter.CollectionPresenter;
import com.nulldreams.wowpaper.presenter.GroupPresenter;
import com.nulldreams.wowpaper.presenter.TagPresenter;
import com.nulldreams.wowpaper.presenter.WowPresenter;
import com.nulldreams.wowpaper.presenter.WowView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class PaperListActivity extends WowActivity implements SwipeRefreshLayout.OnRefreshListener,
        WowView{

    private static final String TAG = PaperListActivity.class.getSimpleName();

    private DrawerLayout mDl;
    private Toolbar mTb;
    private RecyclerView mRv, mNavRv;
    private DelegateAdapter mAdapter, mNavAdapter;

    private Category mCategory;
    private String mType;

    //private int mPage = 1;
    private boolean isLoading = false;

    private SwipeRefreshLayout mSrl;

    private OnScrollBottomListener mBottomListener = new OnScrollBottomListener() {
        @Override
        public void onScrollBottom(RecyclerView recyclerView, int newState) {
            if (isLoading) {
                return;
            }
            mPresenter.loadNextPagerList();
        }
    };

    private DelegateParser<Paper> mPaperParser = new DelegateParser<Paper>() {
        @Override
        public LayoutImpl parse(DelegateAdapter adapter, Paper data) {
            PaperDelegate delegate = new PaperDelegate(data);
            delegate.setShowInfo(showInfo);
            return delegate;
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

    private FooterDelegate mFooter;

    private WowPresenter mPresenter;

    private boolean showInfo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_list);

        mDl = (DrawerLayout)findViewById(R.id.paper_list_dl);

        mTb = (Toolbar)findViewById(R.id.paper_list_tb);
        setSupportActionBar(mTb);
        mTb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRv.scrollToPosition(0);
            }
        });

        mSrl = (SwipeRefreshLayout)findViewById(R.id.paper_list_srl);
        mSrl.setColorSchemeResources(R.color.colorAccent);
        mSrl.setOnRefreshListener(this);
        mSrl.setProgressViewOffset(false, 0, WowHelper.getSrlOffset(this));

        mRv = (RecyclerView)findViewById(R.id.paper_list_rv);
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
        mRv.addOnScrollListener(mBottomListener);

        mFooter = new FooterDelegate (FooterDelegate.STATE_NONE);

        mNavRv = (RecyclerView)findViewById(R.id.paper_list_nav_rv);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager();
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        mNavRv.setLayoutManager(layoutManager);
        mNavAdapter = new DelegateAdapter(this);
        mNavRv.setAdapter(mNavAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        int page = 1;

        if (savedInstanceState != null) {

            ArrayList<Paper> papers = savedInstanceState.getParcelableArrayList("papers");
            page = savedInstanceState.getInt("page", 1);

            onPaperListPrepared(papers, page);

            final int position = savedInstanceState.getInt("position");
            mRv.scrollToPosition(position);
            mCategory = savedInstanceState.getParcelable("style");
            mType = savedInstanceState.getString("type");

            ArrayList<Category> categories = savedInstanceState.getParcelableArrayList("categories");

            onNavListPrepared(categories);

        } else {
            mCategory = getIntent().getParcelableExtra("style");
            mType = getIntent().getStringExtra("type");
        }

        switch (mType) {
            case TagStyleDelegate.STYLE_CATEGORY:
                mPresenter = new CategoryPresenter(this, this, mCategory, mType, page);
                break;
            case TagStyleDelegate.STYLE_GROUP:
                mPresenter = new GroupPresenter(this, this, mCategory, mType, page);
                break;
            case TagStyleDelegate.STYLE_COLLECTION:
                mPresenter = new CollectionPresenter(this, this, mCategory, mType, page);
                break;
            case TagStyleDelegate.STYLE_TAG:
            case TagStyleDelegate.STYLE_USER:
            case TagStyleDelegate.STYLE_SUB_CATEGORY:
                mPresenter = new TagPresenter(this, this, mCategory, mType, page);
                break;
        }

        mPresenter.create(this, savedInstanceState);

        showInfo = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                getString(R.string.pref_key_show_info_grid), true
        );
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mPrefListener);

    }

    @Override
    public void onBackPressed() {
        if (mDl.isDrawerOpen(Gravity.END|Gravity.RIGHT)) {
            mDl.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mAdapter != null && !mAdapter.isEmpty()) {
            ArrayList<Paper> papers = mAdapter.getDataSourceArrayList(new SimpleFilter<Paper>(Paper.class));
            final int position = ((GridLayoutManager)mRv.getLayoutManager()).findFirstVisibleItemPosition();
            outState.putParcelableArrayList("papers", papers);
            outState.putInt("position", position);
        }
        outState.putInt("page", mPresenter.getPage());
        outState.putParcelable("style", mCategory);
        outState.putString("type", mType);

        if (mNavAdapter != null && !mNavAdapter.isEmpty()) {
            ArrayList<Category> categories
                    = mNavAdapter.getDataSourceArrayList(new SimpleFilter<Category>(Category.class));
            outState.putParcelableArrayList("categories", categories);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_paper_list, menu);
        if (mCategory != null && !TextUtils.isEmpty(mCategory.url)) {
            menu.findItem(R.id.paper_list_share).setVisible(true);
        }
        if (!mPresenter.needLockDrawerLayout()) {
            menu.findItem(R.id.paper_list_filter).setVisible(true);
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
            case R.id.paper_list_filter:
                mDl.openDrawer(Gravity.RIGHT | Gravity.END);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy(this);
        mRv.removeOnScrollListener(mBottomListener);
        mSrl.setOnRefreshListener(null);

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mPrefListener);
    }

    @Override
    public void onRefresh() {
        if (isLoading) {
            mSrl.setRefreshing(false);
            return;
        }
        mPresenter.reloadPaperList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategorySelected (Category category) {
        mPresenter.selectItem(category);
        mDl.closeDrawers();
    }

    @Override
    public void onItemChanged(Category category, boolean userAction) {
        mCategory = category;
        setTitle(category.name);
        mNavAdapter.actionWith(new DelegateAction() {
            @Override
            public void onAction(LayoutImpl impl) {
                if (impl instanceof TagStyleDelegate) {
                    TagStyleDelegate delegate = (TagStyleDelegate)impl;
                    delegate.setSelected(delegate.getSource().equals(mPresenter.getCategory()));
                }
            }
        });
        mNavAdapter.notifyDataSetChanged();
        if (userAction) {
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }
        if (mAdapter.isEmpty()) {
            mSrl.post(new Runnable() {
                @Override
                public void run() {
                    mSrl.setRefreshing(true);
                    mPresenter.reloadPaperList();
                }
            });
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onNavListLoading() {

    }

    @Override
    public void onNavListPrepared(List<Category> categoryList) {
        mNavAdapter.clear();
        mNavAdapter.addAll(categoryList, new DelegateParser<Category>() {
            @Override
            public LayoutImpl parse(DelegateAdapter adapter, Category data) {
                TagStyleDelegate tagStyleDelegate = new TagStyleDelegate(data);
                tagStyleDelegate.setSelected(data.equals(mCategory));
                return tagStyleDelegate;
            }
        });
        mNavAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNavListFailed() {

    }

    @Override
    public void onPaperListLoading(int page) {
        isLoading = true;
        if (mAdapter.endWith(mFooter)) {
            mFooter.setState(FooterDelegate.STATE_LOADING);
            mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void onPaperListPrepared(List<Paper> paperList, int page) {
        isLoading = false;
        if (page == 1) {
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
        }, paperList, mPaperParser);

        mAdapter.addIfNotExist(mFooter);
        mFooter.setState(FooterDelegate.STATE_SUCCESS);
        final int count = mAdapter.getItemCount() - countBefore;
        if (page == 1) {
            mAdapter.notifyDataSetChanged();
        } else {
            if (count == 0) {
                mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
            } else {
                mAdapter.notifyItemRangeInserted(countBefore, count);
            }
        }
    }

    @Override
    public void onPaperListFailed() {
        isLoading = false;
        if (mSrl.isRefreshing()) {
            mSrl.setRefreshing(false);
        }
        mAdapter.addIfNotExist(mFooter);
        mFooter.setState(FooterDelegate.STATE_FAILED);
        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
        Toast.makeText(PaperListActivity.this, R.string.toast_load_data_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLockDragLayout(boolean needLock) {
        if (needLock) {
            mDl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }
}
