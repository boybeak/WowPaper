package com.nulldreams.wowpaper.adapter.delegate;

import android.support.annotation.IntDef;

import com.nulldreams.adapter.AbsDelegate;
import com.nulldreams.adapter.annotation.AnnotationDelegate;
import com.nulldreams.adapter.annotation.DelegateInfo;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.event.TagStyleClick;
import com.nulldreams.wowpaper.adapter.holder.TagStyleHolder;
import com.nulldreams.wowpaper.modules.Category;

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
public class TagStyleDelegate extends AnnotationDelegate<Category> {

    public static final String KEY_STYLE = "style";

    public static final int STYLE_NONE = 0, STYLE_USER = 1,
            STYLE_CATEGORY = 2, STYLE_SUB_CATEGORY = 3, STYLE_COLLECTION = 4,
            STYLE_GROUP = 5, STYLE_TAG = 6;

    @IntDef({
            STYLE_NONE, STYLE_USER, STYLE_CATEGORY, STYLE_SUB_CATEGORY,
            STYLE_COLLECTION, STYLE_GROUP, STYLE_TAG
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Style {
    }


    public TagStyleDelegate(Category category) {
        super(category);
    }

    public AbsDelegate setStyle (@Style int style) {
        return putInt(KEY_STYLE, style);
    }

    public int getStyle () {
        return getInt(KEY_STYLE, STYLE_NONE);
    }
}
