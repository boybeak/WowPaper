package com.nulldreams.wowpaper.adapter.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.nulldreams.adapter.AbsViewHolder;
import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.delegate.TitleDelegate;

/**
 * Created by gaoyunfei on 2017/3/19.
 */

public class TitleHolder extends AbsViewHolder<TitleDelegate> {

    private TextView titleTv;

    public TitleHolder(View itemView) {
        super(itemView);

        titleTv = (TextView)findViewById(R.id.title);
    }

    @Override
    public void onBindView(Context context, TitleDelegate titleDelegate, int position, DelegateAdapter adapter) {
        String title = titleDelegate.getSource();
        titleTv.setText(title);
    }
}
