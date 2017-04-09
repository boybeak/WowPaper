package com.nulldreams.wowpaper.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.nulldreams.base.utils.Intents;
import com.nulldreams.wowpaper.DeviceInfo;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.WowApp;
import com.nulldreams.wowpaper.adapter.delegate.TagStyleDelegate;
import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.manager.LikeManager;
import com.nulldreams.wowpaper.modules.Filter;
import com.nulldreams.wowpaper.modules.Paper;
import com.nulldreams.wowpaper.modules.PaperInfoResult;
import com.nulldreams.wowpaper.service.PaperService;

import org.xutils.common.task.PriorityExecutor;
import org.xutils.http.RequestParams;
import org.xutils.x;
import org.xutils.common.Callback.ProgressCallback;
import org.xutils.common.Callback.Cancelable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PaperActivity extends WowActivity {

    private static final String TAG = PaperActivity.class.getSimpleName();

    private final static int MAX_DOWNLOAD_THREAD = 2; // 有效的值范围[1, 3], 设置为3时, 可能阻塞图片加载.

    private Paper mPaper;

    private ContentLoadingProgressBar mPb;
    private SubsamplingScaleImageView mPaperIv;

    private Toolbar mTb;
    private View mPositionLayout, mPositionScreen;
    private RecyclerView mInfoRv;
    private ImageView mPositionThumbIv, mMaskIv;

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

    private Animator mSetBtnAnim;
    private AnimatorSet mPositionAnim;

    private int mThumbScale = 8;

    //private RequestManager mGlideManager = null;

    private File mPaperFile = null;

    private final Executor executor = new PriorityExecutor(MAX_DOWNLOAD_THREAD, true);

    private ProgressCallback<File> mDownloadCallback = new ProgressCallback<File>() {
        @Override
        public void onWaiting() {

        }

        @Override
        public void onStarted() {
            mPb.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoading(long total, long current, boolean isDownloading) {
            mPb.setProgress((int) (current * 100f / total));
        }

        @Override
        public void onSuccess(File result) {
            showWallpaper(result);
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
        }

        @Override
        public void onCancelled(CancelledException cex) {
        }

        @Override
        public void onFinished() {

        }
    };

    private Cancelable mCancelable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper);

        mPb = (ContentLoadingProgressBar)findViewById(R.id.paper_progress_bar);

        mTb = (Toolbar)findViewById(R.id.paper_tb);
        setSupportActionBar(mTb);

        mPaperIv = (SubsamplingScaleImageView)findViewById(R.id.paper_image);
        mPaperIv.setZoomEnabled(false);

        mMaskIv = (ImageView) findViewById(R.id.paper_mask);

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

        mScreenWidth = DeviceInfo.getDeviceWidth(this);
        mScreenHeight = DeviceInfo.getDeviceHeight(this);
        mScale = mScreenHeight * 1.0f / mPaper.height;

        setTitle(mPaper.name);
        mTb.setSubtitle(mPaper.getInfo(this));

        ApiManager.getInstance(this).getPaperInfo(mPaper.id, new Callback<PaperInfoResult>() {
            @Override
            public void onResponse(Call<PaperInfoResult> call, Response<PaperInfoResult> response) {
                if (isDestroyed()) {
                    return;
                }
                mPaper = response.body().wallpaper;

                mInfoAdapter.add(response.body(), new DelegateListParser<PaperInfoResult>() {
                    @Override
                    public List<LayoutImpl> parse(DelegateAdapter adapter, PaperInfoResult data) {
                        List<LayoutImpl> delegates = new ArrayList<LayoutImpl>();
                        List<Filter> tags = data.tags;
                        if (tags != null && !tags.isEmpty()) {
                            for (Filter tag : tags) {
                                delegates.add(new TagStyleDelegate(tag).setStyle(TagStyleDelegate.STYLE_TAG));
                            }
                        }
                        if (mPaper.user_id > 0) {
                            delegates.add(new TagStyleDelegate(
                                    new Filter(mPaper.user_id, mPaper.user_name))
                                    .setStyle(TagStyleDelegate.STYLE_USER));
                        }
                        if (mPaper.category_id > 0) {
                            mInfoAdapter.add(new TagStyleDelegate(
                                    new Filter(mPaper.category_id, mPaper.category))
                                    .setStyle(TagStyleDelegate.STYLE_CATEGORY));
                        }
                        if (mPaper.sub_category_id > 0) {
                            mInfoAdapter.add(new TagStyleDelegate(
                                    new Filter(mPaper.sub_category_id, mPaper.sub_category))
                                    .setStyle(TagStyleDelegate.STYLE_SUB_CATEGORY));
                        }
                        if (mPaper.collection_id > 0) {
                            mInfoAdapter.add(new TagStyleDelegate(
                                    new Filter(mPaper.collection_id, mPaper.collection))
                                    .setStyle(TagStyleDelegate.STYLE_COLLECTION));
                        }
                        if (mPaper.group_id > 0) {
                            mInfoAdapter.add(new TagStyleDelegate(
                                    new Filter(mPaper.group_id, mPaper.group))
                                    .setStyle(TagStyleDelegate.STYLE_GROUP));
                        }
                        return delegates;
                    }
                });
                mInfoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<PaperInfoResult> call, Throwable t) {
                Toast.makeText(PaperActivity.this, R.string.toast_load_data_failed, Toast.LENGTH_SHORT).show();
            }
        });
        mPaperIv.setOnImageEventListener(new SubsamplingScaleImageView.DefaultOnImageEventListener(){

            @Override
            public void onReady() {
                mMaskIv.setVisibility(View.GONE);
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

        initPositionLayout();
        downloadPicture(mPaper);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        fullscreen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_paper, menu);
        MenuItem item = menu.findItem(R.id.paper_like);
        item.setChecked(LikeManager.getInstance(this).isLiked(mPaper.id));
        item.setIcon(item.isChecked() ? R.drawable.ic_heart : R.drawable.ic_heart_outline);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        switch (item.getItemId()) {
            case R.id.paper_like:
                item.setChecked(!item.isChecked());
                if (item.isChecked()) {
                    item.setIcon(R.drawable.ic_heart);
                    LikeManager.getInstance(this).save(mPaper);
                } else {
                    item.setIcon(R.drawable.ic_heart_outline);
                    LikeManager.getInstance(this).delete(mPaper);
                }

                //Toast.makeText(this, "" + item.isChecked(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.paper_set:
                if (mPaperFile == null || !mPaperFile.exists() || mPaper.file_size != mPaperFile.length()) {
                    Toast.makeText(PaperActivity.this, R.string.toast_wallpaper_downloading, Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (WowApp.checkWallpaperPermission(PaperActivity.this)) {
                    PaperService.setWallpaper(PaperActivity.this, mPaperFile.getAbsolutePath(), (int)(mScale * mPaper.width), mScreenHeight);
                } else {
                    new AlertDialog.Builder(PaperActivity.this)
                            .setMessage(R.string.dialog_msg_device_wallpaper_not_allowed)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
                return true;
            case R.id.paper_share:
                try {
                    Intents.shareText(PaperActivity.this, R.string.title_dialog_share, mPaper.url_page);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PaperActivity.this, R.string.toast_no_app_response, Toast.LENGTH_SHORT).show();
                }
                /*requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionCallback() {
                    @Override
                    public void onGranted() {
                    }

                    @Override
                    public void onDenied() {

                    }
                });*/
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void downloadPicture (Paper paper) {
        File paperFile = getTargetFile(paper);
        if (paperFile.exists() && paperFile.length() == paper.file_size) {
            Log.v(TAG, "downloadPicture IF");
            showWallpaper(paperFile);
        } else {
            Log.v(TAG, "downloadPicture ELSE");
            RequestParams params = new RequestParams(paper.url_image);
            params.setAutoRename(true);
            params.setAutoResume(true);
            params.setSaveFilePath(paperFile.getAbsolutePath());
            params.setExecutor(executor);
            params.setCancelFast(true);
            mCancelable= x.http().get(params, mDownloadCallback);
        }
    }

    private File getTargetFile (Paper paper) {
        return new File(WowApp.getPaperCacheDir(this), "" + paper.id);
    }

    private void showWallpaper (File result) {
        mPaperFile = result;
        //Uri uri = FileProvider.getUriForFile(this, "com.nulldreams.wowpaper", mPaperFile);
        Glide.with(PaperActivity.this).load(result).asBitmap().into(new SimpleTarget<Bitmap>() {

            @Override
            public void onStart() {
                super.onStart();
                Log.v(TAG, "showWallpaper onStart");
            }

            @Override
            public void onStop() {
                super.onStop();
                Log.v(TAG, "showWallpaper onStop");
            }

            @Override
            public void onLoadStarted(Drawable placeholder) {
                super.onLoadStarted(placeholder);
                Log.v(TAG, "showWallpaper onLoadStarted");
            }

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Log.v(TAG, "showWallpaper onResourceReady");
                mPb.setVisibility(View.GONE);
                mPaperIv.setImage(ImageSource.bitmap(resource));
                mPositionThumbIv.setImageBitmap(Bitmap.createScaledBitmap(
                        resource, mThumbWidth, mThumbHeight, true));
            }

            @Override
            public void onLoadCleared(Drawable placeholder) {
                super.onLoadCleared(placeholder);
                Log.v(TAG, "showWallpaper onLoadCleared");
            }

            @Override
            public void onDestroy() {
                super.onDestroy();
                Log.v(TAG, "showWallpaper onDestroy");
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                Log.v(TAG, "showWallpaper onLoadFailed e=" + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mPaperIv.recycle();
        /*if (mBmp != null && !mBmp.isRecycled()) {
            mBmp.recycle();
        }*/
        /*if (mGlideManager != null) {
            mGlideManager.onDestroy();
        }*/
        if (mCancelable != null && !mCancelable.isCancelled() && mPaperFile == null) {
            mCancelable.cancel();
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

        Glide.with(this).load(mPaper.getThumb350()).asBitmap().placeholder(R.drawable.bg_paper_place_holder).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                mPositionThumbIv.setImageBitmap(resource);
                mPositionScreen.setVisibility(View.VISIBLE);
                if (mPaperFile == null) {
                    mMaskIv.setVisibility(View.VISIBLE);
                    mMaskIv.setImageBitmap(resource);
                }
            }

            @Override
            public void onLoadStarted(Drawable placeholder) {
                mPositionThumbIv.setImageDrawable(placeholder);
            }
        });

        mPositionThumbIv.setOnClickListener(mClickListener);

        int currentHeight = (int)(mPaper.height * 1f * mScreenWidth / mPaper.width);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mMaskIv.getLayoutParams();
        params.height = currentHeight;
        mMaskIv.setLayoutParams(params);

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
