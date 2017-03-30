package com.nulldreams.wowpaper;

import android.content.Context;

import com.nulldreams.base.utils.UiHelper;

/**
 * Created by boybe on 2017/3/30.
 */

public class WowHelper {
    public static int getSrlOffset (Context context) {
        return UiHelper.getActionBarSize(context)
                + context.getResources().getDimensionPixelOffset(R.dimen.margin_big);
    }
}
