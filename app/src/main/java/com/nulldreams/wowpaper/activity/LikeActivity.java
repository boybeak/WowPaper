package com.nulldreams.wowpaper.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.DelegateParser;
import com.nulldreams.adapter.impl.LayoutImpl;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.decoration.PaperDecoration;
import com.nulldreams.wowpaper.adapter.delegate.PaperDelegate;
import com.nulldreams.wowpaper.manager.LikeManager;
import com.nulldreams.wowpaper.modules.Paper;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;
import java.util.Observable;

public class LikeActivity extends WowActivity {

    private static final String TAG = LikeActivity.class.getSimpleName();

    @ViewInject(R.id.like_toolbar)
    private Toolbar mTb;
    @ViewInject(R.id.like_rv)
    private RecyclerView mRv;

    private DelegateAdapter mAdapter;

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

        final int spanCount = getResources().getInteger(R.integer.span_count);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);
        /*gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                if (mAdapter.getItemCount() - 1 == i) {
                    return spanCount;
                }
                return 1;
            }
        });*/
        mRv.setLayoutManager(gridLayoutManager);
        mRv.addItemDecoration(new PaperDecoration(this));
        mAdapter = new DelegateAdapter(this);
        mRv.setAdapter(mAdapter);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        List<Paper> paperList = LikeManager.getInstance(this).findAll();
        mAdapter.addAll(paperList, new DelegateParser<Paper>() {
            @Override
            public LayoutImpl parse(DelegateAdapter adapter, Paper data) {
                return new PaperDelegate(data);
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData () {
    }
}
