package com.zh.android.chat.service.module.base

import android.app.Activity
import io.reactivex.Observable
import top.zibin.luban.Luban
import java.io.File

/**
 * @author wally
 * @date 2020/08/31
 * 压缩
 */
class CompressPresenter {
    /**
     * 压缩单张图片
     */
    fun compressImage(
        activity: Activity,
        filePath: String
    ): Observable<String> {
        return compressImage(activity, listOf(filePath))
            .flatMap {
                if (it.isEmpty()) {
                    Observable.error(NullPointerException())
                } else {
                    Observable.just(it[0])
                }
            }
    }

    /**
     * 批量压缩图片
     * @param filePaths 图片路径列表
     */
    fun compressImage(
        activity: Activity,
        filePaths: List<String>
    ): Observable<List<String>> {
        return Observable.just(filePaths)
            .concatMap {
                val compressFilePaths = Luban.with(activity)
                    .load(filePaths)
                    .get()
                Observable.fromIterable<File>(compressFilePaths)
            }.map {
                it.path
            }.toList()
            .toObservable()
    }
}