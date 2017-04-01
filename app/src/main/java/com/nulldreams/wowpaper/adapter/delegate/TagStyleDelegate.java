package com.nulldreams.wowpaper.adapter.delegate;

import android.support.annotation.StringDef;

import com.nulldreams.adapter.AbsDelegate;
import com.nulldreams.adapter.annotation.AnnotationDelegate;
import com.nulldreams.adapter.annotation.DelegateInfo;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.event.TagStyleClick;
import com.nulldreams.wowpaper.adapter.holder.TagStyleHolder;
import com.nulldreams.wowpaper.modules.Filter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by gaoyunfei on 2017/3/18.
 */
@DelegateInfo(
        layoutID = R.layout.layout_tag_style,
        holderClass = TagStyleHolder.class,
        onClick = TagStyleClick.class
)
public class TagStyleDelegate extends AnnotationDelegate<Filter> {

    public static final String KEY_STYLE = "style";

    public static final String STYLE_NONE = "", STYLE_USER = "user",
            STYLE_CATEGORY = "category", STYLE_SUB_CATEGORY = "sub_category",
            STYLE_COLLECTION = "collection", STYLE_GROUP = "group", STYLE_TAG = "tag";

    @StringDef({
            STYLE_NONE, STYLE_USER, STYLE_CATEGORY, STYLE_SUB_CATEGORY,
            STYLE_COLLECTION, STYLE_GROUP, STYLE_TAG
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Style {
    }

    private boolean isSelected = false;

    public TagStyleDelegate(Filter filter) {
        super(filter);
    }

    public AbsDelegate setStyle (@Style String style) {
        return putString(KEY_STYLE, style);
    }

    public String getStyle () {
        return getString(KEY_STYLE, STYLE_NONE);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
