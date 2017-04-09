package com.nulldreams.base.widget.behavior;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by gaoyunfei on 2017/4/9.
 */

public class TopHideBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    private static final String TAG = TopHideBehavior.class.getSimpleName();

    private int mLastDyConsumed = 0;
    private ObjectAnimator mAnimator;

    public TopHideBehavior() {
    }

    public TopHideBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*@Override
    public boolean layoutDependsOn(CoordinatorLayout parent, V child, View dependency) {
        return dependency instanceof FrameLayout;
    }*/

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V child, View directTargetChild, View target, int nestedScrollAxes) {
        //Log.v(TAG, "onStartNestedScroll " + nestedScrollAxes);
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        return true;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

        float translationY = child.getTranslationY() - dyConsumed;

        if (translationY < -child.getHeight()) {
            translationY = -child.getHeight();
        } else if (translationY > 0) {
            translationY = 0;
        }
        child.setTranslationY(translationY);
        mLastDyConsumed = dyConsumed;
        //Log.v(TAG, target.getClass().getSimpleName() + " onNestedScroll dxConsumed=" + dxConsumed + " dyConsumed=" + dyConsumed + " dxUnconsumed=" + dxUnconsumed + " dyUnconsumed=" + dyUnconsumed + " child=" + child.getClass().getSimpleName());
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {
        mAnimator = null;
        if (mLastDyConsumed > 0) {
            mAnimator = ObjectAnimator.ofFloat(child, "translationY", child.getTranslationY(), -child.getHeight());
        } else if (mLastDyConsumed < 0) {
            mAnimator = ObjectAnimator.ofFloat(child, "translationY", child.getTranslationY(), 0);
        }
        if (mAnimator != null) {
            mAnimator.start();
        }
    }
}
