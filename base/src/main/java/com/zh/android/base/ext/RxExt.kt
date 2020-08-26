package com.zh.android.base.ext

import androidx.lifecycle.LifecycleOwner
import com.uber.autodispose.ObservableSubscribeProxy
import com.zh.android.base.util.rx.RxLifecycleUtil
import com.zh.android.base.util.rx.RxSchedulerUtil
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions

/**
 * <b>Package:</b> com.linghit.base.ext <br>
 * <b>Create Date:</b> 2019-07-02  16:46 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> RxJava拓展 <br>
 */

private val onNextStub: (Any) -> Unit = {}
private val onErrorStub: (Throwable) -> Unit = {}
private val onCompleteStub: () -> Unit = {}

private fun <T : Any> ((T) -> Unit).asConsumer(): Consumer<T> {
    return if (this === onNextStub) Functions.emptyConsumer() else Consumer(this)
}

private fun ((Throwable) -> Unit).asOnErrorConsumer(): Consumer<Throwable> {
    return if (this === onErrorStub) Functions.ON_ERROR_MISSING else Consumer(this)
}

private fun (() -> Unit).asOnCompleteAction(): Action {
    return if (this === onCompleteStub) Functions.EMPTY_ACTION else Action(this)
}

/**
 * 线程切换拓展
 */
fun <T> Observable<T>.ioToMain(): Observable<T> {
    return this.compose(RxSchedulerUtil.ioToMain())
}

/**
 * 拓展绑定AutoDispose
 */
fun <T> Observable<T>.lifecycle(owner: LifecycleOwner): ObservableSubscribeProxy<T> {
    return this.`as`(RxLifecycleUtil.bindLifecycle(owner))
}

/**
 * 当Observable被订阅时，回调指定闭包，在Ui线程调用
 */
fun <T> Observable<T>.doOnSubscribeUi(task: () -> Unit): Observable<T> {
    return this.doOnSubscribe {
        runTaskOnUi(task)
    }
}

/**
 * 拓展AutoDispose绑定生命周期后返回的ObservableSubscribeProxy，增加DSL
 */
fun <T : Any> ObservableSubscribeProxy<T>.subscribeBy(onNext: (T) -> Unit = onNextStub,
                                                      onError: (Throwable) -> Unit = onErrorStub,
                                                      onComplete: () -> Unit = onCompleteStub
): Disposable {
    return subscribe(onNext.asConsumer(), onError.asOnErrorConsumer(), onComplete.asOnCompleteAction())
}