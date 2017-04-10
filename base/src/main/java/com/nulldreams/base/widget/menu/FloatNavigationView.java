package com.nulldreams.base.widget.menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.view.menu.MenuBuilder;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.FrameLayout;

import com.nulldreams.base.R;

/**
 * Created by gaoyunfei on 2017/4/10.
 */

public class FloatNavigationView extends FrameLayout {

    private MenuInflater mMenuInflater;

    private MenuBuilder mMenu;

    private FloatNavigationMenuView mMenuView;

    private FloatingActionButton mActionBtn;

    public FloatNavigationView(@NonNull Context context) {
        this(context, null);
    }

    public FloatNavigationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatNavigationView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mMenuView = new FloatNavigationMenuView(context);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FloatNavigationView, defStyleAttr, 0);
        if (array.hasValue(R.styleable.FloatNavigationView_menu)) {
            inflateMenu(array.getResourceId(R.styleable.FloatNavigationView_menu, 0));
            mActionBtn = new FloatingActionButton(context);
        }
        array.recycle();

        //this.addView();
    }

    private MenuInflater getMenuInflater() {
        if (mMenuInflater == null) {
            mMenuInflater = new MenuInflater(getContext());
        }
        return mMenuInflater;
    }

    public void inflateMenu (int menuRes) {
        getMenuInflater().inflate(menuRes, mMenu);
    }

}
