package com.nulldreams.wowpaper.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.nulldreams.base.utils.BuildHelper;
import com.nulldreams.base.utils.Intents;
import com.nulldreams.base.utils.UiHelper;
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
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
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

    @ViewInject(R.id.paper_progress_bar) private ContentLoadingProgressBar mPb;
    @ViewInject(R.id.paper_pb) private ContentLoadingProgressBar mCirclePb;
    @ViewInject(R.id.paper_image) private SubsamplingScaleImageView mPaperIv;
    @ViewInject(R.id.paper_status_bar_holder) private View mStatusHolder;
    @ViewInject(R.id.paper_tb) private Toolbar mTb;
    @ViewInject(R.id.paper_tb_layout) private View mTbLayout;
    @ViewInject(R.id.paper_position_layout) private View mPositionLayout;
    @ViewInject(R.id.paper_position_screen) private View mPositionScreen;
    @ViewInject(R.id.paper_info_rv) private RecyclerView mInfoRv;
    @ViewInject(R.id.paper_position_thumb) private ImageView mPositionThumbIv;
    @ViewInject(R.id.paper_navigation_bar_holder) private View mNavigationHolder;
    @ViewInject(R.id.paper_navigation_layout) private View mNavLayout;
    @ViewInject(R.id.paper_mask) private ImageView mMaskIv;
    @ViewInject(R.id.paper_like_btn) private ImageView mLikeBtn;
    @ViewInject(R.id.paper_share_btn) private ImageView mShareBtn;
    @ViewInject(R.id.paper_set_btn) private ImageView mSetBtn;
    @ViewInject(R.id.paper_menu_layout) private FrameLayout mMenuLayout;

    private int mScreenWidth, mScreenHeight, mThumbWidth, mThumbHeight;

    private float mCenterX, mCenterY;
    private float mScale;

    private DelegateAdapter mInfoAdapter;

    private AnimatorSet mSetBtnAnim, mPositionAnim;

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

        x.view().inject(this);

        setSupportActionBar(mTb);

        mPaperIv.setZoomEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        layoutManager.setStackFromEnd(true);
        mInfoRv.setLayoutManager(layoutManager);
        /*LinearSnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(mInfoRv);*/
        mInfoAdapter = new DelegateAdapter(this);
        mInfoRv.setAdapter(mInfoAdapter);

        mPaper = getIntent().getParcelableExtra(Paper.class.getSimpleName());

        mScreenWidth = UiHelper.getPortraitRealWidth(this);
        mScreenHeight = UiHelper.getPortraitRealHeight(this);
        mScale = mScreenHeight * 1.0f / mPaper.height;

        setTitle(mPaper.name);
        mTb.setSubtitle(mPaper.getInfo(this));

        mCirclePb.setVisibility(View.VISIBLE);
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

                mPb.setVisibility(View.GONE);
                mPositionScreen.setVisibility(View.VISIBLE);
                mCirclePb.setVisibility(View.INVISIBLE);
//                mPaperIv.setImage(ImageSource.bitmap(resource));
                expandMenu();
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
                if (mNavLayout.getVisibility() == View.GONE) {
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
        if (BuildHelper.kitkatAndAbove()) {
            if (hasVirtualNavBar()) {
                int navHeight = UiHelper.getNavigationBarSize(this);
                ViewGroup.LayoutParams navParams = mNavigationHolder.getLayoutParams();
                if (navParams == null) {
                    navParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, navHeight);
                } else {
                    navParams.height = navHeight;
                }
                mNavigationHolder.setLayoutParams(navParams);
            }
        }
        int statusSize = UiHelper.getStatusBarHeight(this);
        ViewGroup.LayoutParams statusParams = mStatusHolder.getLayoutParams();
        if (statusParams == null) {
            statusParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusSize);
        } else {
            statusParams.height = statusSize;
        }
        mStatusHolder.setLayoutParams(statusParams);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onWallpaperSetStart() {
        mCirclePb.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onWallpaperSetEnd(boolean success) {
        super.onWallpaperSetEnd(success);
        mCirclePb.setVisibility(View.INVISIBLE);
    }

    @Event(value = {
            R.id.paper_position_thumb,
            R.id.paper_set_btn,
            R.id.paper_like_btn,
            R.id.paper_share_btn
    })
    private void onClick (View view) {
        switch (view.getId()) {
            case R.id.paper_position_thumb:
                if (mPaperIv.isReady()) {
                    mPaperIv.animateCenter(new PointF(mCenterX, mCenterY)).start();
                }
                break;
            case R.id.paper_set_btn:
                if (mPaperFile == null || !mPaperFile.exists() || mPaper.file_size != mPaperFile.length()) {
                    Toast.makeText(PaperActivity.this, R.string.toast_wallpaper_downloading, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (WowApp.checkWallpaperPermission(PaperActivity.this)) {
                    PaperService.setWallpaper(PaperActivity.this, mPaperFile.getAbsolutePath(), (int)(mScale * mPaper.width), mScreenHeight);
                } else {
                    new AlertDialog.Builder(PaperActivity.this)
                            .setMessage(R.string.dialog_msg_device_wallpaper_not_allowed)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
                break;
            case R.id.paper_like_btn:
                view.setSelected(!view.isSelected());
                if (view.isSelected()) {
                    LikeManager.getInstance(this).save(mPaper);
                } else {
                    LikeManager.getInstance(this).delete(mPaper);
                }
                break;
            case R.id.paper_share_btn:
                try {
                    Intents.shareText(PaperActivity.this, R.string.title_dialog_share, mPaper.url_page);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PaperActivity.this, R.string.toast_no_app_response, Toast.LENGTH_SHORT).show();
                }
                break;
        }
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
            mCancelable = x.http().get(params, mDownloadCallback);
        }
    }

    private File getTargetFile (Paper paper) {
        return new File(WowApp.getPaperCacheDir(this), "" + paper.id);
    }

    private void showWallpaper (File result) {
        mPaperFile = result;
        mPaperIv.setImage(ImageSource.uri(Uri.fromFile(result)));
        //Uri uri = FileProvider.getUriForFile(this, "com.nulldreams.wowpaper", mPaperFile);
        Glide.with(PaperActivity.this).load(result).asBitmap().into(mPositionThumbIv);
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

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)mPositionLayout.getLayoutParams();
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

//        mPositionThumbIv.setOnClickListener(mClickListener);

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
                mTbLayout, "alpha", 0f, 1f);
        ObjectAnimator menuLayoutAnim = ObjectAnimator.ofFloat(
                mMenuLayout, "alpha", 0f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.play(animator).with(menuLayoutAnim);
        set.addListener(new DefaultAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mTbLayout.setVisibility(View.VISIBLE);
                mMenuLayout.setVisibility(View.VISIBLE);
                mMenuLayout.setEnabled(true);
            }
        });
        set.start();
        mSetBtnAnim = set;
    }

    private void animHideSetBtn () {
        if (mSetBtnAnim != null && mSetBtnAnim.isRunning()) {
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                mTbLayout, "alpha", 1f, 0f);
        ObjectAnimator menuLayoutAnim = ObjectAnimator.ofFloat(
                mMenuLayout, "alpha", 1f, 0f);
        AnimatorSet set = new AnimatorSet();
        set.play(animator).with(menuLayoutAnim);
        set.addListener(new DefaultAnimatorListener(){

            @Override
            public void onAnimationEnd(Animator animation) {
                mTbLayout.setVisibility(View.GONE);
                mMenuLayout.setVisibility(View.INVISIBLE);
                mMenuLayout.setEnabled(false);
            }
        });
        set.start();
        mSetBtnAnim = set;
    }

    private void animShowPositionLayout() {
        if (mPositionAnim != null && mPositionAnim.isRunning()) {
            return;
        }
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                mNavLayout, "alpha", 0f, 1f);
        /*ObjectAnimator infoAnim = ObjectAnimator.ofFloat(
                mInfoRv, "alpha", 0f, 1f);*/
        animator.addListener(new DefaultAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mNavLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeAllListeners();
            }
        });
        set.play(animator);
        set.start();
        mPositionAnim = set;
    }

    private void animHidePositionLayout () {
        if (mPositionAnim != null && mPositionAnim.isRunning()) {
            return;
        }
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                mNavLayout, "alpha", 1f, 0f);
        /*ObjectAnimator infoAnim = ObjectAnimator.ofFloat(
                mInfoRv, "alpha", 1f, 0f);*/
        animator.addListener(new DefaultAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeAllListeners();
                mNavLayout.setVisibility(View.GONE);
            }
        });
        set.play(animator);
        set.start();
        mPositionAnim = set;
    }

    private void expandMenu () {
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams)mMenuLayout.getLayoutParams();
        params.height = mCirclePb.getHeight() * 4;
        mMenuLayout.setLayoutParams(params);

        mLikeBtn.setSelected(LikeManager.getInstance(this).isLiked(mPaper.id));

        ObjectAnimator shareTranslate =
                ObjectAnimator.ofFloat(mShareBtn, "translationY", 0, mShareBtn.getHeight() * 3f);
        ObjectAnimator shareAlpha =
                ObjectAnimator.ofFloat(mShareBtn, "alpha", 0f, 1f);
        ObjectAnimator likeTranslate =
                ObjectAnimator.ofFloat(mLikeBtn, "translationY", 0, mLikeBtn.getHeight() * 1.5f);
        ObjectAnimator likeAlpha =
                ObjectAnimator.ofFloat(mLikeBtn, "alpha", 0f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        set.play(shareTranslate).with(likeTranslate).with(shareAlpha).with(likeAlpha);
        set.start();
    }

}
