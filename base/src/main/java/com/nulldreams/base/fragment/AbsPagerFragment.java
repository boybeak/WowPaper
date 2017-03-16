package com.nulldreams.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

/**
 * Created by gaoyunfei on 16/8/23.
 */
public abstract class AbsPagerFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private ViewPager mVp;

    public abstract CharSequence getTitle (Context context, Bundle bundle);

    public void setupWithViewPager (ViewPager vp) {
        mVp = vp;
        mVp.addOnPageChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVp != null) {
            mVp.removeOnPageChangeListener(this);
            mVp = null;
        }
    }

    public void actionCommand (int command, Bundle bundle) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
