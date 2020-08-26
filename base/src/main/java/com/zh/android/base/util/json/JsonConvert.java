package com.zh.android.base.util.json;

import java.lang.reflect.Type;

/**
 * <b>Package:</b> com.tongwei.smarttoilet.base.util.json <br>
 * <b>Create Date:</b> 2019-10-28  14:45 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> Json转换器接口 <br>
 */
public interface JsonConvert<S> {
    /**
     * 获取实现
     */
    S getImpl();

    /**
     * 根据Json和模型Class，转换出模型实例
     *
     * @param json  Json
     * @param clazz 模型Class
     * @return 转换出来的模型实例
     */
    <T> T fromJson(String json, Class<T> clazz);

    /**
     * 根据Json和Type，转换出模型实例，一般用于泛型
     *
     * @param json Json
     * @param type Type
     */
    <T> T fromJson(String json, Type type);

    /**
     * 模型转Json
     *
     * @param model 模型
     * @return 转换的Json
     */
    String toJson(Object model);
}