package com.nulldreams.wowpaper.adapter.delegate;

import android.support.annotation.IntDef;

import com.nulldreams.adapter.annotation.AnnotationDelegate;
import com.nulldreams.adapter.annotation.DelegateInfo;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.holder.FooterHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by boybe on 2017/3/22.
 */
@DelegateInfo(
        layoutID = R.layout.layout_footer,
        holderClass = FooterHolder.class
)
public class FooterDelegate extends AnnotationDelegate<Integer> {

    public static final int STATE_NONE = 0, STATE_LOADING = 1, STATE_SUCCESS = 2, STATE_FAILED = 3;

    @IntDef({STATE_NONE, STATE_LOADING, STATE_SUCCESS, STATE_FAILED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State{}

    public FooterDelegate(Integer s) {
        super(s);
    }

    public void setState (@State int state) {
        setSource(state);
    }

    public @State int getState () {
        return getSource();
    }
}
