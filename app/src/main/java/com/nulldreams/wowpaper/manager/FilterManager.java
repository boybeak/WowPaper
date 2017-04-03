package com.nulldreams.wowpaper.manager;

import android.content.Context;

import com.nulldreams.base.manager.AbsManager;
import com.nulldreams.wowpaper.BuildConfig;
import com.nulldreams.wowpaper.modules.Filter;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

/**
 * Created by gaoyunfei on 2017/4/1.
 */

public class FilterManager extends AbsManager
        implements DbManager.DbOpenListener, DbManager.DbUpgradeListener {

    private static FilterManager sManager = null;

    public static synchronized FilterManager getInstance (Context context) {
        if (sManager == null) {
            sManager = new FilterManager(context.getApplicationContext());
        }
        return sManager;
    }

    private DbManager.DaoConfig mCategoryConfig, mCollectionConfig;

    public FilterManager(Context context) {
        super(context);
        mCategoryConfig = new DbManager.DaoConfig()
                .setDbName("category.db")
                .setDbDir(context.getExternalCacheDir())
                .setDbVersion(BuildConfig.VERSION_CODE)
                .setDbOpenListener(this)
                .setDbUpgradeListener(this);
        mCollectionConfig = new DbManager.DaoConfig()
                .setDbName("collection.db")
                .setDbDir(context.getExternalCacheDir())
                .setDbVersion(BuildConfig.VERSION_CODE)
                .setDbOpenListener(this)
                .setDbUpgradeListener(this);
    }

    public void saveCategories (List<Filter> categoryList) {
        try {
            x.getDb(mCategoryConfig).saveOrUpdate(categoryList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public List<Filter> getCategories () {
        try {
            return x.getDb(mCategoryConfig).selector(Filter.class).orderBy("name").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveCollections (List<Filter> categoryList) {
        try {
            x.getDb(mCollectionConfig).saveOrUpdate(categoryList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public List<Filter> getCollections () {
        try {
            return x.getDb(mCollectionConfig).selector(Filter.class).orderBy("name").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onDbOpened(DbManager db) {

    }

    @Override
    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {

    }
}
