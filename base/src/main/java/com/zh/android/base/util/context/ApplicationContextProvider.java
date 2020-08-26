package com.zh.android.base.util.context;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.jetbrains.annotations.NotNull;


/**
 * Package: com.github.wally.applicationprovider
 * FileName: ApplicationContextProvider
 * Date: on 2018/5/31  下午11:08
 * Auther: zihe
 * Descirbe:用于拿取ApplicationContext使用的内容提供者
 * Email: hezihao@linghit.com
 */
public class ApplicationContextProvider extends ContentProvider {
    @SuppressLint("StaticFieldLeak")
    static Context mContext;

    @Override
    public boolean onCreate() {
        mContext = getContext();
        return false;
    }

    @Override
    public Cursor query(@NotNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(@NotNull Uri uri) {
        return null;
    }


    @Override
    public Uri insert(@NotNull Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NotNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NotNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}