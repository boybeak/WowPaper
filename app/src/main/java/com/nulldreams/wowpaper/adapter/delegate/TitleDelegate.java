package com.nulldreams.wowpaper.adapter.delegate;

import com.nulldreams.adapter.annotation.AnnotationDelegate;
import com.nulldreams.adapter.annotation.DelegateInfo;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.holder.TitleHolder;

/**
 * Created by gaoyunfei on 2017/3/19.
 */
@DelegateInfo(
        layoutID = R.layout.layout_title,
        holderClass = TitleHolder.class
)
public class TitleDelegate extends AnnotationDelegate<String> {
    public TitleDelegate(String s) {
        super(s);
    }
}
