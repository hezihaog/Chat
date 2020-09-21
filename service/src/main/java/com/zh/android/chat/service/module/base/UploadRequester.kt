package com.zh.android.chat.service.module.base

import com.lzy.okgo.OkGo
import com.lzy.okgo.request.PostRequest
import com.lzy.okrx2.adapter.ObservableBody
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.ext.genericGsonType
import com.zh.android.base.http.HttpModel
import com.zh.android.base.http.ModelConvert
import io.reactivex.Observable
import java.io.File

/**
 * @author wally
 * @date 2020/09/20
 * 上传请求
 */
class UploadRequester {
    companion object {
        /**
         * 上传文件
         */
        fun uploadFile(
            tag: String,
            file: File
        ): Observable<HttpModel<String>> {
            val type = genericGsonType<HttpModel<String>>()
            val request: PostRequest<HttpModel<String>> = OkGo.post(ApiUrl.UPLOAD_FILE)
            return request.tag(tag)
                .params("file", file)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }

        /**
         * 上传多个文件
         */
        fun uploadFiles(
            tag: String,
            files: List<File>
        ): Observable<HttpModel<List<String>>> {
            val type = genericGsonType<HttpModel<List<String>>>()
            val request: PostRequest<HttpModel<List<String>>> = OkGo.post(ApiUrl.UPLOAD_FILES)
            return request.tag(tag)
                .addFileParams("files", files)
                .converter(ModelConvert(type))
                .adapt(ObservableBody())
        }
    }
}