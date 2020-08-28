package com.hule.dashi.websocket.cache;

import java.io.Serializable;

/**
 * <b>Package:</b> com.hule.dashi.websocket.cache <br>
 * <b>Create Date:</b> 2019/4/1  5:48 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class CacheItem<T> implements Serializable {
    private static final long serialVersionUID = -401778630524300400L;

    /**
     * 缓存的对象
     */
    private T cacheTarget;
    /**
     * 最近使用时间
     */
    private long recentlyUsedTime;

    public CacheItem(T cacheTarget, long recentlyUsedTime) {
        this.cacheTarget = cacheTarget;
        this.recentlyUsedTime = recentlyUsedTime;
    }

    public T getCacheTarget() {
        return cacheTarget;
    }

    public void setCacheTarget(T cacheTarget) {
        this.cacheTarget = cacheTarget;
    }

    public long getRecentlyUsedTime() {
        return recentlyUsedTime;
    }

    public void setRecentlyUsedTime(long recentlyUsedTime) {
        this.recentlyUsedTime = recentlyUsedTime;
    }
}