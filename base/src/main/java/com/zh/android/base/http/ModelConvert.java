package com.zh.android.base.http;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.lzy.okgo.convert.Converter;
import com.zh.android.base.util.json.JsonProxy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * <b>Project:</b> lingjidashi <br>
 * <b>Create Date:</b> 2019/1/10 <br>
 * <b>@author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b>  <br>
 */
public class ModelConvert<T> implements Converter<T> {
    private Type type;
    private Class<T> clazz;
    private Gson mGson;

    public ModelConvert() {
    }

    public ModelConvert(Type type) {
        this.type = type;
    }

    public ModelConvert(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void setGson(Gson gson) {
        mGson = gson;
    }

    @Override
    public T convertResponse(Response response) throws Throwable {
        ResponseBody body = response.body();
        if (body == null) {
            return null;
        }
        T data;
        try {
            if (mGson == null) {
                mGson = JsonProxy.get().getImpl().getImpl();
            }
            JsonReader jsonReader = new JsonReader(body.charStream());
            if (type != null) {
                data = mGson.fromJson(jsonReader, type);
            } else if (clazz != null) {
                data = mGson.fromJson(jsonReader, clazz);
            } else {
                Type genType = getClass().getGenericSuperclass();
                Type type = ((ParameterizedType) genType).getActualTypeArguments()[0];
                data = mGson.fromJson(jsonReader, type);
            }
        } finally {
            response.close();
        }
        return data;
    }
}