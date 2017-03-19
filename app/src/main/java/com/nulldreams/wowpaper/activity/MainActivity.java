package com.nulldreams.wowpaper.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.nulldreams.base.fragment.AbsPagerFragment;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.fragment.PaperListFragment;
import com.nulldreams.wowpaper.fragment.TagStyleFragment;
import com.nulldreams.wowpaper.manager.ApiManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TabLayout mTabLayout;
    private ViewPager mVp;

    private AbsPagerFragment[] mPagers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*final int gap = getResources().getDimensionPixelSize(R.dimen.margin_middle);
        mPaperWidth = (getResources().getDisplayMetrics().widthPixels - (spanCount + 1) * gap) / spanCount;*/

        mTabLayout = (TabLayout)findViewById(R.id.main_tab_layout);
        mVp = (ViewPager) findViewById(R.id.main_vp);

        mPagers = new AbsPagerFragment[] {
                TagStyleFragment.newInstance(),
                PaperListFragment.newInstance(getString(R.string.title_newest),
                        ApiManager.WOW_TYPE_NEWEST),
                PaperListFragment.newInstance(getString(R.string.title_high_rate),
                        ApiManager.WOW_TYPE_HIGHEST_RATED)
        };

        mVp.setAdapter(new PaperAdapter(getSupportFragmentManager()));
        mVp.setCurrentItem(1);
        mTabLayout.setupWithViewPager(mVp);

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
            Log.v(TAG, "TAG instantiateItem");
            return super.instantiateItem(container, position);
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
