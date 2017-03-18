package com.nulldreams.wowpaper.adapter.event;

import android.content.Context;
import android.view.View;

import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.widget.OnItemClickListener;
import com.nulldreams.base.content.It;
import com.nulldreams.wowpaper.adapter.delegate.PaperDelegate;
import com.nulldreams.wowpaper.adapter.holder.PaperHolder;
import com.nulldreams.wowpaper.modules.Paper;
import com.nulldreams.wowpaper.activity.PaperActivity;

/**
 * Created by boybe on 2017/3/17.
 */

public class PaperClick implements OnItemClickListener<PaperDelegate, PaperHolder> {
    @Override
    public void onClick(View view, Context context, PaperDelegate paperDelegate, PaperHolder paperHolder, int position, DelegateAdapter adapter) {
        It.newInstance().putExtra(Paper.class.getSimpleName(), paperDelegate.getSource())
                .startActivity(context, PaperActivity.class);
    }
}
