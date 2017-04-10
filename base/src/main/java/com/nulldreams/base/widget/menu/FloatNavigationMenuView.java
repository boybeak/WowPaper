package com.nulldreams.base.widget.menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.widget.LinearLayout;

import com.nulldreams.base.R;

/**
 * Created by gaoyunfei on 2017/4/10.
 */

public class FloatNavigationMenuView extends LinearLayout implements MenuView {

    private MenuBuilder mMenuBuilder;

    public FloatNavigationMenuView(Context context) {
        this(context, null);
    }

    public FloatNavigationMenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatNavigationMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(MenuBuilder menu) {
        mMenuBuilder = menu;
    }

    @Override
    public int getWindowAnimations() {
        return 0;
    }
}
