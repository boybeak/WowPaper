package com.nulldreams.wowpaper.adapter.event;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.widget.OnItemClickListener;
import com.nulldreams.base.content.It;
import com.nulldreams.wowpaper.Finals;
import com.nulldreams.wowpaper.activity.PaperListActivity;
import com.nulldreams.wowpaper.adapter.delegate.TagStyleDelegate;
import com.nulldreams.wowpaper.adapter.holder.TagStyleHolder;

/**
 * Created by gaoyunfei on 2017/3/19.
 */

public class TagStyleClick implements OnItemClickListener<TagStyleDelegate, TagStyleHolder> {
    @Override
    public void onClick(View view, Context context, TagStyleDelegate tagStyleDelegate, TagStyleHolder tagStyleHolder, int position, DelegateAdapter adapter) {
        if (!TagStyleDelegate.STYLE_NONE.equals(tagStyleDelegate.getStyle())) {
            It.newInstance()
                    .putExtra("style", tagStyleDelegate.getSource())
                    .putExtra("type", tagStyleDelegate.getStyle())
                    .startActivity(context, PaperListActivity.class);
        } else {
            LocalBroadcastManager.getInstance(context).sendBroadcast(
                    new Intent(Finals.ACTION_FILTER_SELECT)
                            .putExtra(Finals.KEY_FILTER, tagStyleDelegate.getSource())
            );
            //EventBus.getDefault().post(tagStyleDelegate.getSource());
        }
    }
}
