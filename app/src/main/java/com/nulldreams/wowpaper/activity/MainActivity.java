package com.nulldreams.wowpaper.activity;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.nulldreams.base.content.It;
import com.nulldreams.base.fragment.AbsPagerFragment;
import com.nulldreams.base.utils.BuildHelper;
import com.nulldreams.base.utils.UiHelper;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.fragment.HomeFragment;
import com.nulldreams.wowpaper.fragment.LikeFragment;
import com.nulldreams.wowpaper.fragment.TagStyleFragment;

import org.xutils.view.annotation.Event;
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
    private TagStyleFragment mTagFragment;
    private LikeFragment mLikeFragment;

    private AbsPagerFragment mLastFragment;

    private List<Fragment> mFragments = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (BuildHelper.nAndAbove(Build.VERSION_CODES.KITKAT)) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }*/
        setContentView(R.layout.activity_main);

        x.view().inject(this);

        mFragments = new ArrayList<>();
        if (savedInstanceState != null) {
            mHomeFragment = (HomeFragment) getSupportFragmentManager()
                    .findFragmentByTag(HomeFragment.class.getName());
            /*mTagFragment = (TagStyleFragment) getSupportFragmentManager()
                    .findFragmentByTag(TagStyleFragment.class.getName());*/
            mLikeFragment = (LikeFragment) getSupportFragmentManager()
                    .findFragmentByTag(LikeFragment.class.getName());
        }
        if (mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
        }
        /*if (mTagFragment == null) {
            mTagFragment = new TagStyleFragment();
        }*/
        if (mLikeFragment == null) {
            mLikeFragment = new LikeFragment();
        }
        mFragments.add(mHomeFragment);
//        mFragments.add(mTagFragment);
        mFragments.add(mLikeFragment);

        setSupportActionBar(mTb);

        mBottomNav.setOnNavigationItemReselectedListener(this);
        mBottomNav.setOnNavigationItemSelectedListener(this);

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
            /*case R.id.nav_category:
                mLastFragment = mTagFragment;
                mBottomNav.setSelectedItemId(mLastSelectId);
                break;
            case R.id.nav_collection:
                break;*/
            case R.id.nav_like:
                mLastFragment = mLikeFragment;
                mBottomNav.setSelectedItemId(mLastSelectId);
                break;
            default:
                mBottomNav.setSelectedItemId(R.id.nav_home);
                break;
        }

        if (BuildHelper.kitkatAndAbove()) {
            if (hasVirtualNavBar()) {
                int navSize = UiHelper.getNavigationBarSize(this);
                ViewGroup.LayoutParams params = mNavPlaceHolderView.getLayoutParams();
                if (params == null) {
                    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, navSize);
                } else {
                    params.height = navSize;
                }
                mNavPlaceHolderView.setLayoutParams(params);
            }

            int statusSize = UiHelper.getStatusBarHeight(this);
            ViewGroup.LayoutParams statusParams = mStatusPlaceHolderView.getLayoutParams();
            if (statusParams == null) {
                statusParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusSize);
            } else {
                statusParams.height = statusSize;
            }
            mStatusPlaceHolderView.setLayoutParams(statusParams);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("nav_last_select_id", mBottomNav.getSelectedItemId());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nav, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_settings:
                It.newInstance().startActivity(this, SettingsActivity.class);
                return true;
            case R.id.nav_about:
                It.newInstance().startActivity(this, AboutActivity.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Event(value = {
            R.id.main_tb
    })
    private void onClick (View view) {
        switch (view.getId()) {
            case R.id.main_tb:
                if (mLastFragment != null) {
                    mLastFragment.actionCommand(1, null);
                }
                break;
        }
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
        AbsPagerFragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = mHomeFragment;
                break;
            /*case R.id.nav_category:
                fragment = mTagFragment;
                break;*/
            case R.id.nav_like:
                fragment = mLikeFragment;
                break;
        }
        if (fragment == null) {
            return;
        }
        if (fragment.isAdded()) {
            fragment.actionCommand(2, null);
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
            /*case R.id.nav_category:
                fragment = mTagFragment;
                break;*/
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
