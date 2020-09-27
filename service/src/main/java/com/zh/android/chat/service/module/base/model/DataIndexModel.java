package com.zh.android.chat.service.module.base.model;

/**
 * <b>Package:</b> com.hule.dashi.service <br>
 * <b>Create Date:</b> 2019/3/4  2:25 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 包裹数据和角标位置的模型，一般用于Rx，遍历数据，过滤指定类型数据后，需要用到数据和角标的情况 <br>
 */
public class DataIndexModel<T> {
    private T data;
    private int index;

    public DataIndexModel(T data, int index) {
        this.data = data;
        this.index = index;
    }

    public T getData() {
        return data;
    }

    public int getIndex() {
        return index;
    }
}