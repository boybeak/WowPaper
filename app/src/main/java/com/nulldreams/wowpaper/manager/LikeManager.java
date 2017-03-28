package com.nulldreams.wowpaper.manager;

import android.content.Context;
import android.util.Log;

import com.nulldreams.base.manager.AbsManager;
import com.nulldreams.wowpaper.BuildConfig;
import com.nulldreams.wowpaper.modules.Paper;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

/**
 * Created by gaoyunfei on 2017/3/28.
 */

public class LikeManager extends AbsManager
        implements DbManager.DbOpenListener, DbManager.DbUpgradeListener{

    private static final String TAG = LikeManager.class.getSimpleName();

    private static LikeManager sManager = null;

    public static synchronized LikeManager getInstance (Context context) {
        if (sManager == null) {
            sManager = new LikeManager(context.getApplicationContext());
        }
        return sManager;
    }

    private DbManager.DaoConfig mConfig = null;

    private LikeManager(Context context) {
        super(context);

        mConfig = new DbManager.DaoConfig()
                .setDbName("like.db")
                .setDbDir(context.getExternalCacheDir())
                .setDbVersion(BuildConfig.VERSION_CODE)
                .setDbOpenListener(this)
                .setDbUpgradeListener(this);

    }

    public boolean isLiked (Paper paper) {
        return isLiked(paper.id);
    }

    public boolean isLiked (long id) {
        try {
            //Log.v(TAG, "isLiked ");
            DbManager manager = x.getDb(mConfig);
            return manager.selector(Paper.class).where("id", "=", id).count() > 0;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Paper get (long id) {
        try {
            DbManager manager = x.getDb(mConfig);
            return manager.selector(Paper.class).where("id", "=", id).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Paper> findAll () {
        try {
            DbManager manager = x.getDb(mConfig);
            return manager.findAll(Paper.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save (Paper paper) {
        try {
            DbManager manager = x.getDb(mConfig);
            manager.saveOrUpdate(paper);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void delete (Paper paper) {
        try {
            DbManager manager = x.getDb(mConfig);
            manager.deleteById(Paper.class, paper.id);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDbOpened(DbManager db) {
        db.getDatabase().enableWriteAheadLogging();
    }

    @Override
    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {

    }
}
