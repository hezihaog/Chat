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
     * 上传文件
     */
    fun uploadFile(filePath: String): Observable<String> {
        return Observable.just(filePath)
            .map {
                File(it)
            }.flatMap {
                UploadRequester.uploadFile(TAG, it)
            }.map {
                it.data
            }
    }

    /**
     * 上传多个文件
     */
    fun uploadFiles(filePaths: List<String>): Observable<List<String>> {
        return Observable.fromIterable(filePaths)
            .map {
                File(it)
            }
            .toList()
            .toObservable()
            .flatMap {
                UploadRequester.uploadFiles(TAG, it)
            }.map {
                it.data
            }
    }

    /**
     * 上传单张图片
     */
    fun uploadImage(activity: Activity, filePath: String): Observable<String> {
        return uploadMultipleImage(activity, listOf(filePath))
            .map {
                it[0]
            }
    }

    /**
     * 上传多张图片
     */
    fun uploadMultipleImage(activity: Activity, filePaths: List<String>): Observable<List<String>> {
        return Observable.just(filePaths)
            .concatMap {
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