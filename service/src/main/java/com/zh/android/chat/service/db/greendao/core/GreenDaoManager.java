package com.zh.android.chat.service.db.greendao.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zh.android.chat.service.db.greendao.DaoMaster;
import com.zh.android.chat.service.db.greendao.DaoSession;

/**
 * @author wally
 * @date 2021/02/21
 * GreenDao管理器
 */
public class GreenDaoManager {
    private static final String DB_NAME = "app_green_dao";

    @SuppressLint("StaticFieldLeak")
    private static volatile GreenDaoManager mInstance;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    private GreenDaoManager() {
        if (mInstance == null) {
            if (mContext == null) {
                throw new IllegalStateException("请先调用init()进行初始化操作");
            }
            GreenDaoSQLiteOpenHelper openHelper = new GreenDaoSQLiteOpenHelper(mContext, DB_NAME, null);
            SQLiteDatabase db = openHelper.getWritableDatabase();
            mDaoMaster = new DaoMaster(db);
            mDaoSession = mDaoMaster.newSession();
        }
    }

    public static void init(Context context) {
        GreenDaoManager.mContext = context.getApplicationContext();
    }

    public static GreenDaoManager getInstance() {
        if (mInstance == null) {
            synchronized (GreenDaoManager.class) {
                if (mInstance == null) {
                    mInstance = new GreenDaoManager();
                }
            }
        }
        return mInstance;
    }

    public DaoMaster getDaoMaster() {
        return mDaoMaster;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }
}