package com.hule.dashi.websocket.cache;

import com.hule.dashi.websocket.WebSocketInfo;

/**
 * <b>Package:</b> com.hule.dashi.websocket.cache <br>
 * <b>Create Date:</b> 2019/4/1  6:30 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> WebSocketInfo缓存池，使用享元模式，减少创建WebSocketInfo消耗的内存 <br>
 */
public class WebSocketInfoPool extends BaseCachePool<WebSocketInfo> {

    @Override
    public WebSocketInfo onCreateCache() {
        return new WebSocketInfo();
    }

    @Override
    public int onSetupMaxCacheCount() {
        return 8;
    }

    @Override
    public WebSocketInfo onObtainCacheAfter(ICacheTarget<WebSocketInfo> cacheTarget) {
        //重置所有字段
        return cacheTarget.reset();
    }
}