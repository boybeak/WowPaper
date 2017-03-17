package com.nulldreams.wowpaper.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.DelegateParser;
import com.nulldreams.adapter.impl.LayoutImpl;
import com.nulldreams.adapter.widget.OnScrollBottomListener;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.PaperDecoration;
import com.nulldreams.wowpaper.adapter.PaperDelegate;
import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.modules.Paper;
import com.nulldreams.wowpaper.modules.PaperResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int spanCount = 2;
        /*final int gap = getResources().getDimensionPixelSize(R.dimen.margin_middle);
        mPaperWidth = (getResources().getDisplayMetrics().widthPixels - (spanCount + 1) * gap) / spanCount;*/

        mRv = (RecyclerView)findViewById(R.id.main_rv);
        mRv.setLayoutManager(new GridLayoutManager(this, spanCount));
        mRv.addItemDecoration(new PaperDecoration(this));
        mRv.addOnScrollListener(mBottomListener);
        mAdapter = new DelegateAdapter(this);
        mRv.setAdapter(mAdapter);

        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRv.removeOnScrollListener(mBottomListener);
    }

    private void loadData () {
        isLoading = true;
        ApiManager.getInstance(this).getNewest(mPage, ApiManager.WOW_TYPE_HIGHEST_RATED, new Callback<PaperResult>() {
            @Override
            public void onResponse(Call<PaperResult> call, Response<PaperResult> response) {
                isLoading = false;
                mAdapter.addAll(response.body().wallpapers, new DelegateParser<Paper>() {
                    @Override
                    public LayoutImpl parse(DelegateAdapter adapter, Paper data) {
                        PaperDelegate delegate = new PaperDelegate(data);
                        //delegate.putInt("width", mPaperWidth);
                        return delegate;
                    }
                });
                mAdapter.notifyDataSetChanged();
                mPage++;
            }

            @Override
            public void onFailure(Call<PaperResult> call, Throwable t) {
                isLoading = false;
            }
        });
    }
}
