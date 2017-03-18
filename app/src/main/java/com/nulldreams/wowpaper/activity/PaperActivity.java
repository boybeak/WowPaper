package com.nulldreams.wowpaper.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.DelegateListParser;
import com.nulldreams.adapter.impl.LayoutImpl;
import com.nulldreams.base.animation.DefaultAnimatorListener;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.WowApp;
import com.nulldreams.wowpaper.adapter.delegate.TagStyleDelegate;
import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.modules.Category;
import com.nulldreams.wowpaper.modules.Paper;
import com.nulldreams.wowpaper.modules.PaperInfoResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PaperActivity extends AppCompatActivity {

    private static final String TAG = PaperActivity.class.getSimpleName();

    private Paper mPaper;

    private SubsamplingScaleImageView mPaperIv;
    private View mMaskView;

    private View mSetBtn;
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
            if (id == mSetBtn.getId()) {
                if (mBmp != null) {
                    Toast.makeText(PaperActivity.this, R.string.toast_wallpaper_downloading, Toast.LENGTH_SHORT).show();
                    return;
                }
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
            } else if (id == mPositionThumbIv.getId()) {
                if (mPaperIv.isReady()) {
                    mPaperIv.animateCenter(new PointF(mCenterX, mCenterY)).start();
                }
            }
        }
    };

    private Bitmap mBmp = null;

    private Animator mSetBtnAnim;
    private AnimatorSet mPositionAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper);

        mSetBtn = findViewById(R.id.paper_set_btn);
        mSetBtn.setOnClickListener(mClickListener);

        mPaperIv = (SubsamplingScaleImageView)findViewById(R.id.paper_image);
        mPaperIv.setZoomEnabled(false);

        mMaskView = findViewById(R.id.paper_mask);

        mInfoRv = (RecyclerView) findViewById(R.id.paper_info_layout);
        mInfoRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        mInfoAdapter = new DelegateAdapter(this);
        mInfoRv.setAdapter(mInfoAdapter);

        mPositionLayout = findViewById(R.id.paper_position_layout);
        mPositionScreen = findViewById(R.id.paper_position_screen);
        mPositionThumbIv = (ImageView)findViewById(R.id.paper_position_thumb);

        mPaper = getIntent().getParcelableExtra(Paper.class.getSimpleName());



        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        mScale = mScreenHeight * 1.0f / mPaper.height;

        ApiManager.getInstance(this).getPaperInfo(mPaper.id, new Callback<PaperInfoResult>() {
            @Override
            public void onResponse(Call<PaperInfoResult> call, Response<PaperInfoResult> response) {
                mPaper = response.body().wallpaper;
                Glide.with(PaperActivity.this).load(response.body().wallpaper.url_image).asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                mBmp = resource;
                                mPaperIv.setImage(ImageSource.bitmap(resource));
                                mPositionThumbIv.setImageBitmap(Bitmap.createScaledBitmap(
                                        resource, mThumbWidth, mThumbHeight, true));
                            }
                        });
                mInfoAdapter.add(mPaper, new DelegateListParser<Paper>() {
                    @Override
                    public List<LayoutImpl> parse(DelegateAdapter adapter, Paper data) {
                        List<LayoutImpl> delegates = new ArrayList<LayoutImpl>();
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
                mPositionThumbIv.setTranslationX(delta / 8 * mScale);
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
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initPositionLayout();
    }

    private void initPositionLayout () {

        final int height8 = mScreenHeight / 8;

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mPositionLayout.getLayoutParams();
        layoutParams.height = height8;
        mPositionLayout.setLayoutParams(layoutParams);

        FrameLayout.LayoutParams thumbParams = (FrameLayout.LayoutParams)mPositionThumbIv.getLayoutParams();
        mThumbWidth = (int)(height8  * 1f / mPaper.height * mPaper.width);
        mThumbHeight = height8;
        thumbParams.width = mThumbWidth;
        mPositionThumbIv.setLayoutParams(thumbParams);

        FrameLayout.LayoutParams screenParams = (FrameLayout.LayoutParams)mPositionScreen.getLayoutParams();
        screenParams.width = mScreenWidth / 8;
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
                mSetBtn, "alpha", 0f, 1f);
        animator.addListener(new DefaultAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mSetBtn.setVisibility(View.VISIBLE);
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
                mSetBtn, "alpha", 1f, 0f);
        animator.addListener(new DefaultAnimatorListener(){

            @Override
            public void onAnimationEnd(Animator animation) {
                mSetBtn.setVisibility(View.GONE);
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
