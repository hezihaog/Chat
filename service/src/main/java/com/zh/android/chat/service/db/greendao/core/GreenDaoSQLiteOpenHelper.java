package com.zh.android.chat.service.db.greendao.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.apkfuns.logutils.LogUtils;
import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.zh.android.chat.service.db.greendao.DaoMaster;
import com.zh.android.chat.service.db.greendao.QrCodeScanHistoryEntityDao;

import org.greenrobot.greendao.database.Database;

/**
 * @author wally
 * @date 2021/02/21
 */
public class GreenDaoSQLiteOpenHelper extends DaoMaster.OpenHelper {
    public GreenDaoSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        if (oldVersion == newVersion) {
            LogUtils.i("数据库无需升级");
            return;
        }
        LogUtils.i("数据库从" + oldVersion + "升级到 ::: " + newVersion + "版本");
        //使用GreenDaoUpgradeHelper辅助类进行升级
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, QrCodeScanHistoryEntityDao.class);
    }
}