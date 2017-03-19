package com.nulldreams.wowpaper.adapter.event;

import android.content.Context;
import android.view.View;

import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.widget.OnItemClickListener;
import com.nulldreams.base.content.It;
import com.nulldreams.wowpaper.activity.PagerListActivity;
import com.nulldreams.wowpaper.adapter.delegate.TagStyleDelegate;
import com.nulldreams.wowpaper.adapter.holder.TagStyleHolder;

/**
 * Created by gaoyunfei on 2017/3/19.
 */

public class TagStyleClick implements OnItemClickListener<TagStyleDelegate, TagStyleHolder> {
    @Override
    public void onClick(View view, Context context, TagStyleDelegate tagStyleDelegate, TagStyleHolder tagStyleHolder, int position, DelegateAdapter adapter) {
        switch (tagStyleDelegate.getStyle()) {
            case TagStyleDelegate.STYLE_CATEGORY:
                It.newInstance().putExtra("style", tagStyleDelegate.getSource()).startActivity(context, PagerListActivity.class);
                break;
        }
    }
}
