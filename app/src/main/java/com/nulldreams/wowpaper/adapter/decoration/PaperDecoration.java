package com.nulldreams.wowpaper.adapter.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.nulldreams.base.utils.UiHelper;
import com.nulldreams.wowpaper.R;

/**
 * Created by boybe on 2017/3/17.
 */

public class PaperDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = PaperDecoration.class.getSimpleName();

    private int offset;
    private int gap;

    public PaperDecoration(Context context) {
        offset = UiHelper.getActionBarSize(context);
        gap = context.getResources().getDimensionPixelSize(R.dimen.grid_gap_width);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        final int position = parent.getChildAdapterPosition(view);

        GridLayoutManager gridLayoutManager = (GridLayoutManager)parent.getLayoutManager();
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams)view.getLayoutParams();

        //Log.v(TAG, "getItemOffsets spanSize=" + params.getSpanSize() + " " + gridLayoutManager.getSpanCount());

        final int spanIndex = params.getSpanIndex();
        final int rowIndex = position / gridLayoutManager.getSpanCount();
        int l, r, t, b;
        if (spanIndex == 0) {
            l = gap;
        } else {
            l = 0;
        }
        r = gap;

        if (rowIndex == 0) {
            t = gap * 2 + offset;
        } else {
            t = 0;
        }
        b = gap;
        Log.v(TAG, "getItemOffsets position=" + position + " spanIndex=" + spanIndex + " rowIndex=" + rowIndex + " (l, t, r, b)=(" + l + ", " + t + ", " + r + ", " + b + ")");
        outRect.set(l, t, r, b);
    }
}
