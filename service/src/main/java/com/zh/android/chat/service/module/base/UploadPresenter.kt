package com.zh.android.chat.service.module.base

import android.app.Activity
import io.reactivex.Observable
import java.io.File

/**
 * @author wally
 * @date 2020/09/20
 * 文件上传
 */
class UploadPresenter {
    companion object {
        private val TAG = UploadPresenter::class.java.name
    }

    /**
     * 图片压缩
     */
    private val mCompressPresenter by lazy {
        CompressPresenter()
    }

    /**
     * 上传多张图片
     */
    fun uploadMultipleImage(activity: Activity, filePaths: List<String>): Observable<List<String>> {
        return Observable.just(filePaths)
            .concatMap {
                //批量压缩图片
                mCompressPresenter.compressImage(activity, filePaths)
            }.concatMap {
                Observable.fromIterable(it)
            }.map {
                File(it)
            }
            .toList()
            .toObservable()
            .flatMap { files ->
                //对每张压缩后的图片，进行上传
                UploadRequester.uploadFiles(TAG, files)
            }.flatMap {
                //拆解图片地址
                if (it.success()) {
                    Observable.just(it.data)
                } else {
                    Observable.error(RuntimeException(it.message))
                }
            }
    }
}