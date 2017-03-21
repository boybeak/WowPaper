package com.nulldreams.wowpaper.adapter.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nulldreams.adapter.AbsViewHolder;
import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.delegate.PaperDelegate;
import com.nulldreams.wowpaper.modules.Paper;

/**
 * Created by gaoyunfei on 2017/3/17.
 */

public class PaperHolder extends AbsViewHolder<PaperDelegate> {

    private static final String TAG = PaperHolder.class.getSimpleName();

    private ImageView thumbIv;

    public PaperHolder(View itemView) {
        super(itemView);
        thumbIv = (ImageView)findViewById(R.id.paper_thumb);
    }

    @Override
    public void onBindView(Context context, PaperDelegate paperDelegate, int position, DelegateAdapter adapter) {
        Paper paper = paperDelegate.getSource();
        Glide.with(context).load(paper.getThumb350()).crossFade().placeholder(R.drawable.bg_paper_place_holder).into(thumbIv);
    }

    @Override
    public void onViewRecycled(Context context) {
        Glide.clear(thumbIv);
    }
}
