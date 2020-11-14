package com.zh.android.chat.service.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.tencent.wcdb.room.db.WCDBOpenHelperFactory;
import com.zh.android.chat.service.db.login.dao.LoginUserDao;
import com.zh.android.chat.service.db.login.entity.LoginUserEntity;

/**
 * @author wally
 * @date 2020/11/14
 */
@Database(entities = {
        LoginUserEntity.class
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    /**
     * 数据库名称
     */
    private static final String DATABASE_NAME = "app_database";

    private static volatile AppDatabase INSTANCE;

    /**
     * 初始化
     */
    public static void initialize(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    WCDBOpenHelperFactory factory = new WCDBOpenHelperFactory()
                            //打开WAL以及读写并发，可以省略让Room决定是否要打开
                            .writeAheadLoggingEnabled(true)
                            //打开异步Checkpoint优化，不需要可以省略
                            .asyncCheckpointEnabled(true);
                    INSTANCE = Room.databaseBuilder(
                            context,
                            AppDatabase.class,
                            DATABASE_NAME
                    ).openHelperFactory(factory)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
    }

    /**
     * 获取实例
     */
    public static AppDatabase getInstance() {
        return INSTANCE;
    }

    /**
     * 获取LoginUserDao
     */
    public abstract LoginUserDao getLoginUserDao();
}