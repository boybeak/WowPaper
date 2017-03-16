package com.nulldreams.wowpaper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.DelegateParser;
import com.nulldreams.adapter.impl.LayoutImpl;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRv = (RecyclerView)findViewById(R.id.main_rv);
        mRv.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new DelegateAdapter(this);
        mRv.setAdapter(mAdapter);

        ApiManager.getInstance(this).getNewest(new Callback<PaperResult>() {
            @Override
            public void onResponse(Call<PaperResult> call, Response<PaperResult> response) {
                mAdapter.addAll(response.body().wallpapers, new DelegateParser<Paper>() {
                    @Override
                    public LayoutImpl parse(DelegateAdapter adapter, Paper data) {
                        return new PaperDelegate(data);
                    }
                });
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<PaperResult> call, Throwable t) {

            }
        });
    }
}
