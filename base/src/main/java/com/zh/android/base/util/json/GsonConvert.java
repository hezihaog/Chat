package com.zh.android.base.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * <b>Package:</b> com.tongwei.smarttoilet.base.util.json <br>
 * <b>Create Date:</b> 2019-10-28  14:48 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class GsonConvert implements JsonConvert<Gson> {
    private static Gson mGson;

    static {
        mGson = new GsonBuilder()
                .registerTypeAdapter(Integer.class, (JsonDeserializer<Integer>) (json, type, arg2) -> {
                    try {
                        return json.getAsInt();
                    } catch (Exception e) {
                        return -1;
                    }
                })
                .registerTypeAdapter(Long.class, (JsonDeserializer<Long>) (json, type, arg2) -> {
                    try {
                        return json.getAsLong();
                    } catch (Exception e) {
                        return (long) -1;
                    }
                })
                .registerTypeAdapter(Float.class, (JsonDeserializer<Float>) (json, type, arg2) -> {
                    try {
                        return json.getAsFloat();
                    } catch (Exception e) {
                        return (float) -1;
                    }
                })
                .registerTypeAdapter(Double.class, (JsonDeserializer<Double>) (json, type, arg2) -> {
                    try {
                        return json.getAsDouble();
                    } catch (Exception e) {
                        return (double) -1;
                    }
                })
                //根据注解导出，需要序列化的字段自己标注
                // .excludeFieldsWithoutExposeAnnotation()
                .serializeNulls()
                .setPrettyPrinting()
                .create();
    }

    @Override
    public Gson getImpl() {
        return mGson;
    }

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mGson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <T> T fromJson(String json, Type type) {
        try {
            return mGson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toJson(Object model) {
        return mGson.toJson(model);
    }
}