package com.nulldreams.wowpaper.adapter.delegate;

import com.nulldreams.adapter.annotation.AnnotationDelegate;
import com.nulldreams.adapter.annotation.DelegateInfo;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.holder.PaperHolder;
import com.nulldreams.wowpaper.adapter.event.PaperClick;
import com.nulldreams.wowpaper.modules.Paper;

/**
 * Created by gaoyunfei on 2017/3/17.
 */
@DelegateInfo(
        layoutID = R.layout.layout_paper,
        holderClass = PaperHolder.class,
        onClick = PaperClick.class
)
public class PaperDelegate extends AnnotationDelegate<Paper> {

    private boolean showInfo = true;

    public PaperDelegate(Paper paper) {
        super(paper);
    }

    public boolean isShowInfo() {
        return showInfo;
    }

    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
    }
}
