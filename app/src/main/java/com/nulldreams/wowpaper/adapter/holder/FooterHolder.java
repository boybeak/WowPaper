package com.nulldreams.wowpaper.adapter.holder;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nulldreams.adapter.AbsViewHolder;
import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.delegate.FooterDelegate;

/**
 * Created by boybe on 2017/3/22.
 */

public class FooterHolder extends AbsViewHolder<FooterDelegate> {

    private TextView msgTv;
    private ProgressBar progressBar;

    public FooterHolder(View itemView) {
        super(itemView);
        msgTv = (TextView)findViewById(R.id.footer_message);
        progressBar = (ProgressBar)findViewById(R.id.footer_progress);
    }

    @Override
    public void onBindView(Context context, FooterDelegate footerDelegate, int position, DelegateAdapter adapter) {
        switch (footerDelegate.getState()) {
            case FooterDelegate.STATE_NONE:
                msgTv.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                msgTv.setText(null);
                break;
            case FooterDelegate.STATE_LOADING:
                msgTv.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                break;
            case FooterDelegate.STATE_SUCCESS:
                msgTv.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                msgTv.setText(R.string.text_success);
                break;
            case FooterDelegate.STATE_FAILED:
                msgTv.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                msgTv.setText(R.string.text_failed);
                break;
        }
    }
}
