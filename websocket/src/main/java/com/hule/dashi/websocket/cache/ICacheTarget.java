package com.hule.dashi.websocket.cache;

/**
 * <b>Package:</b> com.linghit.lingjidashi.base.lib.utils.cache <br>
 * <b>Create Date:</b> 2019/4/9  2:48 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 缓存对象需要实现的接口 <br>
 */
public interface ICacheTarget<T> {
    /**
     * 重置方法
     *
     * @return 重置后的对象
     */
    T reset();
}