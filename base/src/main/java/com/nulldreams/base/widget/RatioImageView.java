package com.nulldreams.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.nulldreams.base.R;

/**
 * Created by boybe on 2016/9/3.
 */
public class RatioImageView extends AppCompatImageView {

    private int mWidthRatio, mHeightRatio;

    public RatioImageView(Context context) {
        this(context, null);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initThis(context, attrs);
    }

    private void initThis (Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);
        try {
            mWidthRatio = array.getInt(R.styleable.RatioImageView_widthRatio, 1);
            mHeightRatio = array.getInt(R.styleable.RatioImageView_heightRatio, 1);
        } finally {
            array.recycle();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        /*int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);*/
        int heightSize = widthSize * mHeightRatio / mWidthRatio;
        setMeasuredDimension(widthSize, heightSize);
    }
}
