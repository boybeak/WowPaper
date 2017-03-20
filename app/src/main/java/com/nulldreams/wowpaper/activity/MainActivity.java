package com.nulldreams.wowpaper.activity;

import android.app.WallpaperManager;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.nulldreams.base.fragment.AbsPagerFragment;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.fragment.PaperListFragment;
import com.nulldreams.wowpaper.fragment.TagStyleFragment;
import com.nulldreams.wowpaper.manager.ApiManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private NavigationView mNavView;
    private TabLayout mTabLayout;
    private ViewPager mVp;

    private ImageView mHeaderCover;

    private AbsPagerFragment[] mPagers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavView = (NavigationView)findViewById(R.id.main_nav);
        mTabLayout = (TabLayout)findViewById(R.id.main_tab_layout);
        mVp = (ViewPager) findViewById(R.id.main_vp);

        mHeaderCover = (ImageView) mNavView.getHeaderView(0).findViewById(R.id.nav_header_cover);

        mHeaderCover.setImageDrawable(this.getWallpaper());

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

        uiVisibility();

    }

    private void uiVisibility () {
        /*getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);*/
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        uiVisibility();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
