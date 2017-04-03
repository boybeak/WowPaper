package com.nulldreams.wowpaper.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.nulldreams.base.utils.Connectivity;
import com.nulldreams.wowpaper.Finals;
import com.nulldreams.wowpaper.WowApp;
import com.nulldreams.wowpaper.manager.LikeManager;
import com.nulldreams.wowpaper.modules.Paper;

import org.xutils.common.Callback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SwitchService extends JobService {

    private static final String TAG = SwitchService.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters params) {

        showNextPaper();

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.v(TAG, "schedule onStopJob");
        return false;
    }

    private void showNextPaper () {
        Paper paper = getNextPaper();
        if (paper == null) {
            return;
        }
        setWallpaper(paper);
        Log.v(TAG, "schedule showNextPaper paper.id=" + paper.id);
    }

    private Paper getNextPaper () {
        List<Paper> likes = LikeManager.getInstance(this).findAll();
        if (likes == null || likes.isEmpty()) {
            return null;
        }
        SharedPreferences preferences = getSharedPreferences("switch.pref", Context.MODE_PRIVATE);
        long lastId = preferences.getLong(Finals.KEY_PAPER_ID, 0);
        int index = 0;
        for (int i = 0; i < likes.size(); i++) {
            if (lastId == likes.get(i).id) {
                index = i;
            }
        }
        return likes.get((index + 1) % likes.size());
    }

    private void setWallpaper (Paper paper) {
        SharedPreferences preferences = getSharedPreferences("switch.pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(Finals.KEY_PAPER_ID, paper.id);
        editor.apply();

        File imageFile = new File(WowApp.getPaperCacheDir(this), paper.id + "");
        int[] size = paper.getTargetSize(this);
        if (paper.checkFile(this)) {
            doSetWallpaper(imageFile, size);
        } else {
            if (Connectivity.isConnectedWifi(this)) {
                downloadPaperFile(paper, imageFile, size);
            } else {
                showNextPaper();
            }
        }
    }

    private void doSetWallpaper (File imageFile, int[] size) {
        Log.v(TAG, "doSetWallpaper width=" + size[0] + " height=" + size[1]);
        if (imageFile.exists()) {
            PaperService.setWallpaper(this, imageFile.getAbsolutePath(), size[0], size[1]);
        }
    }

    private void downloadPaperFile (Paper paper, File file, final int[] size) {
        RequestParams params = new RequestParams(paper.url_image);
        params.setAutoRename(true);
        params.setAutoResume(true);
        params.setSaveFilePath(file.getAbsolutePath());
        params.setExecutor(new PriorityExecutor(1, true));
        params.setCancelFast(true);
        x.http().get(params, new Callback.CommonCallback<File>() {
            @Override
            public void onSuccess(File result) {
                doSetWallpaper(result, size);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showNextPaper();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

}
