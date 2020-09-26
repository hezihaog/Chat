package com.hule.dashi.mediaplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.danikula.videocache.HttpProxyCacheServer;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * <b>Package:</b> com.hule.dashi.answer.util.media <br>
 * <b>Create Date:</b> 2019/2/5  7:34 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 媒体数据源帮助类 <br>
 */
public class MediaDataSourceHelper {
    private final HttpProxyCacheServer mProxyCacheServer;
    private Context mContext;

    public MediaDataSourceHelper(Context context) {
        mProxyCacheServer = new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(1024 * 1024 * 1024)
                .maxCacheFilesCount(20)
                .build();
        mContext = context;
    }

    /**
     * 数据源兼容设置
     */
    public void dataSourceCompat(MediaPlayer player,
                                 MediaDataSource.RealSource<?> realSource,
                                 long offset, long length) throws IOException {
        Object source = realSource.getSource();
        if (source instanceof Uri) {
            Uri uri = (Uri) source;
            String url = uri.toString();
            if (!TextUtils.isEmpty(url)) {
                //网络资源才加代理缓存
                if (url.startsWith("http") || url.startsWith("https")) {
                    String proxyUrl = mProxyCacheServer.getProxyUrl(url);
                    player.setDataSource(mContext, Uri.parse(proxyUrl));
                }
            }
        } else if (source instanceof String) {
            String path = (String) source;
            player.setDataSource(path);
        } else if (source instanceof AssetFileDescriptor) {
            if (Build.VERSION.SDK_INT > 24) {
                AssetFileDescriptor fileDescriptor = (AssetFileDescriptor) source;
                player.setDataSource(fileDescriptor);
            }
        } else if (source instanceof FileDescriptor) {
            FileDescriptor fileDescriptor = (FileDescriptor) source;
            if (offset == 0 || length == 0) {
                player.setDataSource(fileDescriptor);
            } else {
                player.setDataSource(fileDescriptor, offset, length);
            }
        }
    }
}