package com.nulldreams.wowpaper.activity;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nulldreams.base.fragment.AbsPagerFragment;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.fragment.PaperListFragment;
import com.nulldreams.wowpaper.manager.ApiManager;

public class MainActivity extends WowActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout mDl;
    private NavigationView mNavView;
    private TabLayout mTabLayout;
    private ViewPager mVp;
    private Toolbar mTb;

    private ImageView mHeaderCover;

    private ActionBarDrawerToggle mToggle;

    private AbsPagerFragment[] mPagers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDl = (DrawerLayout)findViewById(R.id.main_drawer);
        mNavView = (NavigationView)findViewById(R.id.main_nav);
        mTabLayout = (TabLayout)findViewById(R.id.main_tab_layout);
        mVp = (ViewPager)findViewById(R.id.main_vp);
        mTb = (Toolbar)findViewById(R.id.main_tb);

        mHeaderCover = (ImageView) mNavView.getHeaderView(0).findViewById(R.id.nav_header_cover);

        mPagers = new AbsPagerFragment[] {
                //TagStyleFragment.newInstance(),
                PaperListFragment.newInstance(getString(R.string.title_newest),
                        ApiManager.WOW_TYPE_NEWEST),
                /*PaperListFragment.newInstance(getString(R.string.title_high_rate),
                        ApiManager.WOW_TYPE_HIGHEST_RATED)*/
        };

        mVp.setAdapter(new PaperAdapter(getSupportFragmentManager()));
        mVp.setCurrentItem(1);
        mTabLayout.setupWithViewPager(mVp);

        setSupportActionBar(mTb);

        mToggle = new ActionBarDrawerToggle(this, mDl, mTb, R.string.title_drawer_open, R.string.title_drawer_close);
        mDl.addDrawerListener(mToggle);
        mToggle.syncState();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
//        uiVisibility();
    }

    @Override
    public void onBackPressed() {
        if (mDl.isDrawerOpen(Gravity.LEFT|Gravity.START)) {
            mDl.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Drawable wallpaper = this.getWallpaper();
            if (wallpaper != null) {
                mHeaderCover.setImageDrawable(wallpaper);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDl.removeDrawerListener(mToggle);
    }

    private class PaperAdapter extends FragmentStatePagerAdapter {

        public PaperAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.v(TAG, "TAG getItem");
            return mPagers[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            AbsPagerFragment fragment = (AbsPagerFragment)super.instantiateItem(container, position);
            mPagers[position] = fragment;
            return fragment;
        }

        @Override
        public int getCount() {
            return mPagers.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPagers[position].getTitle(MainActivity.this, null);
        }
    }

}
