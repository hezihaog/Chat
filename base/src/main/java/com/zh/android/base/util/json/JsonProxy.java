package com.zh.android.base.util.json;

import java.lang.reflect.Type;

/**
 * <b>Package:</b> com.tongwei.smarttoilet.base.util.json <br>
 * <b>Create Date:</b> 2019-10-28  14:52 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> Json转换封装 <br>
 */
public class JsonProxy implements JsonConvert<GsonConvert> {
    /**
     * Json转换器
     */
    private GsonConvert mJsonConvert;

    private JsonProxy() {
        mJsonConvert = new GsonConvert();
    }

    private static class SingleHolder {
        private static final JsonProxy INSTANCE = new JsonProxy();
    }

    public static JsonProxy get() {
        return SingleHolder.INSTANCE;
    }

    @Override
    public GsonConvert getImpl() {
        return mJsonConvert;
    }

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        return mJsonConvert.fromJson(json, clazz);
    }

    @Override
    public <T> T fromJson(String json, Type type) {
        return mJsonConvert.fromJson(json, type);
    }

    @Override
    public String toJson(Object model) {
        return mJsonConvert.toJson(model);
    }
}