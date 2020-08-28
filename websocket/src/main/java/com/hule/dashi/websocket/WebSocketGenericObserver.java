package com.hule.dashi.websocket;


import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * <b>Package:</b> com.hule.dashi.websocket <br>
 * <b>Create Date:</b> 2019/1/17  1:45 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 支持泛型传入类型，直接转换msg为指定的Bean类型 <br>
 */
public abstract class WebSocketGenericObserver<T> extends WebSocketObserver {
    @Override
    @CallSuper
    public void onMessage(@NonNull String text) {
        Disposable disposable = Observable
                .just(text)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String json) throws Exception {
                        return isTargetType(json);
                    }
                })
                .flatMap(new Function<String, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(String json) throws Exception {
                        return onParseData(json);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {
                        onMessage(t);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        onError(throwable);
                    }
                });
        onStart(disposable);
    }

    /**
     * 是否是当前类型，因为不同类型的消息的基本结构是一样的，只是msg_type不一致，所以这里尝试解析，是本类型返回true，否则false
     */
    protected abstract boolean isTargetType(String json);

    /**
     * 复写该方法进行Json解析，返回对用的实体类
     */
    protected abstract ObservableSource<T> onParseData(String json);

    /**
     * 子类直接复写提供的泛型方法做操作即可
     */
    protected abstract void onMessage(T data);

    /**
     * 解析出错
     */
    protected void onParseError() {

    }
}