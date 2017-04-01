package com.nulldreams.wowpaper.adapter.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.nulldreams.adapter.AbsViewHolder;
import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.delegate.TagStyleDelegate;
import com.nulldreams.wowpaper.modules.Filter;

/**
 * Created by gaoyunfei on 2017/3/18.
 */

public class TagStyleHolder extends AbsViewHolder<TagStyleDelegate> {

    private TextView textTv;

    public TagStyleHolder(View itemView) {
        super(itemView);

        textTv = (TextView)findViewById(R.id.tag_style_text);
    }

    @Override
    public void onBindView(Context context, TagStyleDelegate tagStyleDelegate, int position, DelegateAdapter adapter) {
        Filter filter = tagStyleDelegate.getSource();

        switch (tagStyleDelegate.getStyle()) {
            case TagStyleDelegate.STYLE_NONE:
                textTv.setText(filter.name);
                break;
            case TagStyleDelegate.STYLE_USER:
                textTv.setText("@" + filter.name);
                break;
            case TagStyleDelegate.STYLE_CATEGORY:
                textTv.setText("\"" + filter.name + "\"");
                break;
            case TagStyleDelegate.STYLE_SUB_CATEGORY:
                textTv.setText("'" + filter.name + "'");
                break;
            case TagStyleDelegate.STYLE_COLLECTION:
            case TagStyleDelegate.STYLE_GROUP:
                textTv.setText("#" + filter.name + "#");
                break;
            case TagStyleDelegate.STYLE_TAG:
                textTv.setText("*" + filter.name + "*");
                break;
            default:
                textTv.setText(filter.name);
                break;
        }

        itemView.setSelected(tagStyleDelegate.isSelected());
    }
}
