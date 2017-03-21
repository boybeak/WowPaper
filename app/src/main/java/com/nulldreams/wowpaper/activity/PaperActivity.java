package com.nulldreams.wowpaper.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.DelegateListParser;
import com.nulldreams.adapter.impl.LayoutImpl;
import com.nulldreams.base.animation.DefaultAnimatorListener;
import com.nulldreams.base.event.PermissionCallback;
import com.nulldreams.base.utils.Intents;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.WowApp;
import com.nulldreams.wowpaper.adapter.delegate.TagStyleDelegate;
import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.modules.Category;
import com.nulldreams.wowpaper.modules.Paper;
import com.nulldreams.wowpaper.modules.PaperInfoResult;

import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PaperActivity extends WowActivity {

    private static final String TAG = PaperActivity.class.getSimpleName();

    private Paper mPaper;

    private ProgressBar mPb;
    private SubsamplingScaleImageView mPaperIv;
    private View mMaskView;

    private Toolbar mTb;
    private View mPositionLayout, mPositionScreen;
    private RecyclerView mInfoRv;
    private ImageView mPositionThumbIv;

    private int mScreenWidth, mScreenHeight, mThumbWidth, mThumbHeight;

    private float mCenterX, mCenterY;
    private float mScale;

    private DelegateAdapter mInfoAdapter;

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int id = v.getId();
            if (id == mPositionThumbIv.getId()) {
                if (mPaperIv.isReady()) {
                    mPaperIv.animateCenter(new PointF(mCenterX, mCenterY)).start();
                }
            }
        }
    };

    private Bitmap mBmp = null;

    private Animator mSetBtnAnim;
    private AnimatorSet mPositionAnim;

    private int mThumbScale = 8;

    private RequestManager mGlideManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper);

        mPb = (ProgressBar)findViewById(R.id.paper_progress_bar);

        mTb = (Toolbar)findViewById(R.id.paper_tb);
        setSupportActionBar(mTb);

        mPaperIv = (SubsamplingScaleImageView)findViewById(R.id.paper_image);
        mPaperIv.setZoomEnabled(false);

        mMaskView = findViewById(R.id.paper_mask);

        mInfoRv = (RecyclerView) findViewById(R.id.paper_info_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        layoutManager.setStackFromEnd(true);
        mInfoRv.setLayoutManager(layoutManager);
        /*LinearSnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(mInfoRv);*/
        mInfoAdapter = new DelegateAdapter(this);
        mInfoRv.setAdapter(mInfoAdapter);

        mPositionLayout = findViewById(R.id.paper_position_layout);
        mPositionScreen = findViewById(R.id.paper_position_screen);
        mPositionThumbIv = (ImageView)findViewById(R.id.paper_position_thumb);

        mPaper = getIntent().getParcelableExtra(Paper.class.getSimpleName());

        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        mScale = mScreenHeight * 1.0f / mPaper.height;

        setTitle(mPaper.name);
        mTb.setSubtitle(getString(R.string.text_size, mPaper.width, mPaper.height) + "  " + Formatter.formatFileSize(this, mPaper.file_size));

        mPb.setVisibility(View.VISIBLE);
        ApiManager.getInstance(this).getPaperInfo(mPaper.id, new Callback<PaperInfoResult>() {
            @Override
            public void onResponse(Call<PaperInfoResult> call, Response<PaperInfoResult> response) {
                mPaper = response.body().wallpaper;
                mGlideManager = Glide.with(PaperActivity.this);
                mGlideManager.load(response.body().wallpaper.url_image).asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                mPb.setVisibility(View.GONE);
                                mBmp = resource;
                                mPaperIv.setImage(ImageSource.bitmap(resource));
                                mPositionThumbIv.setImageBitmap(Bitmap.createScaledBitmap(
                                        resource, mThumbWidth, mThumbHeight, true));
                            }
                        });
                mInfoAdapter.add(response.body(), new DelegateListParser<PaperInfoResult>() {
                    @Override
                    public List<LayoutImpl> parse(DelegateAdapter adapter, PaperInfoResult data) {
                        List<LayoutImpl> delegates = new ArrayList<LayoutImpl>();
                        List<Category> tags = data.tags;
                        if (tags != null && !tags.isEmpty()) {
                            for (Category tag : tags) {
                                delegates.add(new TagStyleDelegate(tag).setStyle(TagStyleDelegate.STYLE_TAG));
                            }
                        }
                        if (mPaper.user_id > 0) {
                            delegates.add(new TagStyleDelegate(
                                    new Category(mPaper.user_id, mPaper.user_name))
                                    .setStyle(TagStyleDelegate.STYLE_USER));
                        }
                        if (mPaper.category_id > 0) {
                            mInfoAdapter.add(new TagStyleDelegate(
                                    new Category(mPaper.category_id, mPaper.category))
                                    .setStyle(TagStyleDelegate.STYLE_CATEGORY));
                        }
                        if (mPaper.sub_category_id > 0) {
                            mInfoAdapter.add(new TagStyleDelegate(
                                    new Category(mPaper.sub_category_id, mPaper.sub_category))
                                    .setStyle(TagStyleDelegate.STYLE_SUB_CATEGORY));
                        }
                        if (mPaper.collection_id > 0) {
                            mInfoAdapter.add(new TagStyleDelegate(
                                    new Category(mPaper.collection_id, mPaper.collection))
                                    .setStyle(TagStyleDelegate.STYLE_COLLECTION));
                        }
                        if (mPaper.group_id > 0) {
                            mInfoAdapter.add(new TagStyleDelegate(
                                    new Category(mPaper.group_id, mPaper.group))
                                    .setStyle(TagStyleDelegate.STYLE_GROUP));
                        }
                        return delegates;
                    }
                });
                mInfoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<PaperInfoResult> call, Throwable t) {

            }
        });
        mPaperIv.setOnImageEventListener(new SubsamplingScaleImageView.DefaultOnImageEventListener(){
            @Override
            public void onReady() {
                mPaperIv.setScaleAndCenter(mScale,
                        new PointF(mPaper.width / 2, mPaper.height / 2));
                mCenterX = mPaperIv.getCenter().x;
                mCenterY = mPaperIv.getCenter().y;

            }
        });
        mPaperIv.setOnStateChangedListener(new SubsamplingScaleImageView.DefaultOnStateChangedListener(){
            @Override
            public void onCenterChanged(PointF newCenter, int origin) {
                float delta = mCenterX - newCenter.x;
                mPositionThumbIv.setTranslationX(delta / mThumbScale * mScale);
            }
        });

        mPaperIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPositionLayout.getVisibility() == View.GONE) {
                    animShowSetBtn();
                    animShowPositionLayout();
                } else {
                    animHidePositionLayout();
                    animHideSetBtn();
                }
            }
        });

        animShowPositionLayout();
        animShowSetBtn();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initPositionLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_paper, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mBmp == null) {
            Toast.makeText(PaperActivity.this, R.string.toast_wallpaper_downloading, Toast.LENGTH_SHORT).show();
            return true;
        }
        switch (item.getItemId()) {
            case R.id.paper_set:
                if (WowApp.checkWallpaperPermission(PaperActivity.this)) {
                    try {
                        WallpaperManager.getInstance(PaperActivity.this).setBitmap(mBmp);
                        WallpaperManager.getInstance(PaperActivity.this).suggestDesiredDimensions(
                                (int)(mPaper.width * mScale), mScreenHeight);
                        Toast.makeText(PaperActivity.this, R.string.toast_set_wallpaper_success, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(PaperActivity.this, R.string.toast_set_wallpaper_failed, Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "set wall paper failed with a IOException");
                    }
                } else {
                    new AlertDialog.Builder(PaperActivity.this)
                            .setMessage(R.string.dialog_msg_device_wallpaper_not_allowed)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
                return true;
            case R.id.paper_share:
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        try {
                            Intents.shareImage(PaperActivity.this, R.string.title_dialog_share, mBmp);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(PaperActivity.this, R.string.toast_no_app_response, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDenied() {

                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mPaperIv.recycle();
        /*if (mBmp != null && !mBmp.isRecycled()) {
            mBmp.recycle();
        }*/
        if (mGlideManager != null) {
            mGlideManager.onDestroy();
        }
        System.gc();
    }

    private void initPositionLayout () {

        final int height8 = mScreenHeight / mThumbScale;

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mPositionLayout.getLayoutParams();
        layoutParams.height = height8;
        mPositionLayout.setLayoutParams(layoutParams);

        FrameLayout.LayoutParams thumbParams = (FrameLayout.LayoutParams)mPositionThumbIv.getLayoutParams();
        mThumbWidth = (int)(height8  * 1f / mPaper.height * mPaper.width);
        mThumbHeight = height8;
        thumbParams.width = mThumbWidth;
        mPositionThumbIv.setLayoutParams(thumbParams);

        FrameLayout.LayoutParams screenParams = (FrameLayout.LayoutParams)mPositionScreen.getLayoutParams();
        screenParams.width = mScreenWidth / mThumbScale;
        screenParams.height = height8;
        mPositionScreen.setLayoutParams(screenParams);

        Glide.with(this).load(mPaper.getThumb350()).into(mPositionThumbIv);

        mPositionThumbIv.setOnClickListener(mClickListener);

        int currentHeight = (int)(mPaper.height * 1f * mScreenWidth / mPaper.width);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mMaskView.getLayoutParams();
        params.height = currentHeight;
        mMaskView.setLayoutParams(params);

    }

    private void animShowSetBtn () {
        if (mSetBtnAnim != null && mSetBtnAnim.isRunning()) {
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                mTb, "alpha", 0f, 1f);
        animator.addListener(new DefaultAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mTb.setVisibility(View.VISIBLE);
            }
        });
        animator.start();
        mSetBtnAnim = animator;
    }

    private void animHideSetBtn () {
        if (mSetBtnAnim != null && mSetBtnAnim.isRunning()) {
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                mTb, "alpha", 1f, 0f);
        animator.addListener(new DefaultAnimatorListener(){

            @Override
            public void onAnimationEnd(Animator animation) {
                mTb.setVisibility(View.GONE);
            }
        });
        animator.start();
        mSetBtnAnim = animator;
    }

    private void animShowPositionLayout() {
        if (mPositionAnim != null && mPositionAnim.isRunning()) {
            return;
        }
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                mPositionLayout, "alpha", 0f, 1f);
        ObjectAnimator infoAnim = ObjectAnimator.ofFloat(
                mInfoRv, "alpha", 0f, 1f);
        animator.addListener(new DefaultAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mPositionLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeAllListeners();
            }
        });
        set.play(infoAnim).after(animator);
        set.start();
        mPositionAnim = set;
    }

    private void animHidePositionLayout () {
        if (mPositionAnim != null && mPositionAnim.isRunning()) {
            return;
        }
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                mPositionLayout, "alpha", 1f, 0f);
        ObjectAnimator infoAnim = ObjectAnimator.ofFloat(
                mInfoRv, "alpha", 1f, 0f);
        animator.addListener(new DefaultAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeAllListeners();
                mPositionLayout.setVisibility(View.GONE);
            }
        });
        set.play(infoAnim).before(animator);
        set.start();
        mPositionAnim = set;
    }

}
