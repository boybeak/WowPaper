package com.nulldreams.wowpaper.activity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nulldreams.base.fragment.AbsPagerFragment;
import com.nulldreams.base.utils.BuildHelper;
import com.nulldreams.base.utils.UiHelper;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.fragment.HomeFragment;
import com.nulldreams.wowpaper.fragment.LikeFragment;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends WowActivity
        implements BottomNavigationView.OnNavigationItemReselectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    @ViewInject(value = R.id.main_tb)
    private Toolbar mTb;
    @ViewInject(value = R.id.main_bottom_nav)
    private BottomNavigationView mBottomNav;
    @ViewInject(value = R.id.main_nav_place_holder)
    private View mNavPlaceHolderView;
    @ViewInject(value = R.id.main_status_bar_place_holder)
    private View mStatusPlaceHolderView;

    private HomeFragment mHomeFragment;
    private LikeFragment mLikeFragment;

    private AbsPagerFragment mLastFragment;

    private List<Fragment> mFragments = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        x.view().inject(this);

        mFragments = new ArrayList<>();
        if (savedInstanceState != null) {
            mHomeFragment = (HomeFragment) getSupportFragmentManager()
                    .findFragmentByTag(HomeFragment.class.getName());
            mLikeFragment = (LikeFragment) getSupportFragmentManager()
                    .findFragmentByTag(LikeFragment.class.getName());
        }
        if (mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
        }
        if (mLikeFragment == null) {
            mLikeFragment = new LikeFragment();
        }
        mFragments.add(mHomeFragment);
        mFragments.add(mLikeFragment);

        setSupportActionBar(mTb);

        mBottomNav.setOnNavigationItemReselectedListener(this);
        mBottomNav.setOnNavigationItemSelectedListener(this);

        LinearLayout.LayoutParams statusParams = (LinearLayout.LayoutParams)mStatusPlaceHolderView.getLayoutParams();
        statusParams.height = UiHelper.getStatusBarHeight(this);
        mStatusPlaceHolderView.setLayoutParams(statusParams);

        if (BuildHelper.api21AndAbove() && hasVirtualNavBar()) {
            int navSize = UiHelper.getNavigationBarSize(this);
            ViewGroup.LayoutParams params = mNavPlaceHolderView.getLayoutParams();
            if (params == null) {
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, navSize);
            } else {
                params.height = navSize;
            }
            mNavPlaceHolderView.setLayoutParams(params);
        }

        float scrHei = getResources().getConfiguration().screenHeightDp * getResources().getDisplayMetrics().density;
        Log.v(TAG, "heightDpi=" + getResources().getConfiguration().screenHeightDp);
        Log.v(TAG, "height=" + (scrHei + UiHelper.getStatusBarHeight(this) + UiHelper.getNavigationBarSize(this)));

    }

    private boolean hasVirtualNavBar () {
        return UiHelper.getRealHeight(this) > getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        int mLastSelectId = -1;
        if (savedInstanceState != null) {
            mLastSelectId = savedInstanceState.getInt("nav_last_select_id", -1);
        }
        Log.v(TAG, "onPostCreate mLastSelectId=" + mLastSelectId);

        switch (mLastSelectId) {
            case R.id.nav_home:
                mLastFragment = mHomeFragment;
                mBottomNav.setSelectedItemId(mLastSelectId);
                break;
            case R.id.nav_category:
                break;
            case R.id.nav_collection:
                break;
            case R.id.nav_like:
                mLastFragment = mLikeFragment;
                mBottomNav.setSelectedItemId(mLastSelectId);
                break;
            default:
                mBottomNav.setSelectedItemId(R.id.nav_home);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("nav_last_select_id", mBottomNav.getSelectedItemId());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
        AbsPagerFragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = mHomeFragment;
                break;
            case R.id.nav_like:
                fragment = mLikeFragment;
                break;
        }
        if (fragment == null) {
            return;
        }
        if (fragment.isAdded()) {

        } else {
            showFragment(fragment, item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        AbsPagerFragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = mHomeFragment;
                break;
            case R.id.nav_like:
                fragment = mLikeFragment;
                break;
        }
        showFragment(fragment, item);
        return fragment != null;
    }

    private void showFragment (AbsPagerFragment fragment, MenuItem item) {
        if (fragment == null) {
            return;
        }
        mTb.setTitle(item.getTitle());
        FragmentTransaction transaction
                = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        /*int index = mFragments.indexOf(fragment);

        if (index > mLastFragmentIndex) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (index < mLastFragmentIndex) {
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        }*/
        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(R.id.main_fragment_container, fragment, fragment.getClass().getName());
        }
        Log.v(TAG, "showFragment mLastFragment=" + mLastFragment);
        if (mLastFragment != null) {
            transaction.hide(mLastFragment);
        }

        transaction.commitNow();
        mLastFragment = fragment;
    }
}
