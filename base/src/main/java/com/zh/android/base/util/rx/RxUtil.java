package com.zh.android.base.util.rx;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;

/**
 * <b>Package:</b> com.linghit.lingjidashi.base.lib.utils <br>
 * <b>Create Date:</b> 2019/1/25  4:00 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class RxUtil {
    private RxUtil() {
    }

    /**
     * Rx倒计时
     *
     * @param startDelayTime 开始前的延时时间，例如开始前有1秒缓冲
     * @param cycle          周期，每隔多久重复执行，例如1秒执行一次的倒计时功能
     * @param time           执行多久，例如倒计时3秒，则为3
     * @param unit           时间单位，倒计时3秒，单位为秒
     */
    public static Observable<Integer> countdown(long startDelayTime, long cycle, int time, TimeUnit unit) {
        final int countTime = Math.max(time, 0);
        return Observable.interval(startDelayTime, cycle, unit)
                .map(new Function<Long, Integer>() {
                    @Override
                    public Integer apply(Long increaseTime) throws Exception {
                        return countTime - increaseTime.intValue();
                    }
                })
                //take指定到多少次就停止，这里指定到时间后就结束
                .take(countTime + 1);
    }

    /**
     * 倒计时，执行无限次
     *
     * @param cycle 周期
     * @param unit  单位
     */
    public static Observable<Long> countdown(long cycle, TimeUnit unit) {
        return Observable.interval(0, cycle, unit);
    }

    /**
     * 延时操作
     */
    public static Observable<Long> delayed(int time, TimeUnit unit) {
        return Observable.timer(time, unit);
    }

    /**
     * 点击防抖
     */
    public static Observable<Object> click(View view) {
        return RxView.clicks(view).throttleFirst(500, TimeUnit.MILLISECONDS);
    }

    public static Observable<Activity> click(View view, final Activity activity) {
        return click(view).filter(new Predicate<Object>() {
            @Override
            public boolean test(Object o) throws Exception {
                return activity != null && !activity.isFinishing();
            }
        }).map(new Function<Object, Activity>() {
            @Override
            public Activity apply(Object o) throws Exception {
                return activity;
            }
        });
    }

    /**
     * 文字改变监听
     */
    public static Observable<CharSequence> textChanges(final TextView textView) {
        return PublishSubject.create(new ObservableOnSubscribe<CharSequence>() {
            @Override
            public void subscribe(final ObservableEmitter<CharSequence> emitter) throws Exception {
                textView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence inputText, int start, int before, int count) {
                        emitter.onNext(inputText.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
            }
        });
    }

    /**
     * 文字改变监听，去掉第一次发送的事件，一般第一次发送的是空的字符串，可以选择过滤掉
     */
    public static Observable<CharSequence> textChangesSkipFirst(TextView textView) {
        return textChanges(textView).skip(1);
    }

    /**
     * 文字改变监听，只保留指定时间内的最后一次，默认超时时间为200毫秒
     */
    public static Observable<CharSequence> textChangesWithDebounce(TextView textView) {
        return textChangesWithDebounce(textView, 200);
    }

    /**
     * 文字改变监听，只保留指定时间内的最后一次，一般用于自动搜索关键字
     *
     * @param timeout 超时时间
     */
    public static Observable<CharSequence> textChangesWithDebounce(TextView textView, long timeout) {
        return textChanges(textView)
                //200毫秒内，发送多次，只保留最后一次
                .debounce(timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取一个什么都不做的Consumer
     */
    public static Consumer<Throwable> nothingErrorConsumer() {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
            }
        };
    }

    public static <T> Observer<T> nothingObserver() {
        return new Observer<T>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Object o) {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
    }
}