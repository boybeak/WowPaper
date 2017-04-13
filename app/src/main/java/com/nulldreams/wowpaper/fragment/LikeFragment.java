package com.nulldreams.wowpaper.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nulldreams.adapter.DelegateAction;
import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.DelegateFilter;
import com.nulldreams.adapter.impl.LayoutImpl;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.adapter.delegate.FooterDelegate;
import com.nulldreams.wowpaper.adapter.delegate.PaperDelegate;
import com.nulldreams.wowpaper.manager.LikeManager;
import com.nulldreams.wowpaper.modules.Paper;

/**
 * Created by gaoyunfei on 2017/4/7.
 */

public class LikeFragment extends PaperListFragment implements LikeManager.Callback{
    @Override
    protected void loadPaperList(int page) {
        if (page == 1) {
            onPaperList(LikeManager.getInstance(getContext()).findAll());
        } else {
            onPaperList(null);
        }
    }

    @Override
    public CharSequence getStaticFooterMsg() {
        if (getAdapter().getCount(new DelegateFilter() {
            @Override
            public boolean accept(DelegateAdapter adapter, LayoutImpl impl) {
                return impl instanceof PaperDelegate;
            }
        }) == 0) {
            return getContext().getString(R.string.text_like_empty);
        }
        return getContext().getString(R.string.text_like_will_lost_when_uninstalled);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LikeManager.getInstance(getContext()).registerCallback(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LikeManager.getInstance(getContext()).unregisterCallback(this);
    }

    @Override
    public void onLikeEvent(boolean like, final Paper paper) {
        if (like) {
            getAdapter().add(getAdapter().getItemCount() - 1, new PaperDelegate(paper));
            getAdapter().notifyItemInserted(getAdapter().getItemCount() - 1);
        } else {
            int index = getAdapter().firstIndexOf(new DelegateFilter() {
                @Override
                public boolean accept(DelegateAdapter adapter, LayoutImpl impl) {
                    if (impl instanceof PaperDelegate) {
                        PaperDelegate delegate = (PaperDelegate)impl;
                        return delegate.getSource().equals(paper);
                    }
                    return false;
                }
            });
            if (index >= 0 && index < getAdapter().getItemCount()) {
                getAdapter().remove(index);
                getAdapter().notifyItemRemoved(index);
            }
        }
        getAdapter().actionWith(new DelegateAction() {
            @Override
            public void onAction(LayoutImpl impl) {
                if (impl instanceof FooterDelegate) {
                    FooterDelegate footer = (FooterDelegate)impl;
                    footer.setStaticMsg(getStaticFooterMsg());
                    getAdapter().notifyItemChanged(getAdapter().getItemCount() - 1);
                }
            }
        });
    }
}
