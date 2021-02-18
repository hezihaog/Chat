package com.zh.android.chat.service.module.base.web.http

import com.zh.android.chat.service.db.web.entity.WebCollectEntity
import com.zh.android.chat.service.module.base.web.db.WebDbMaster
import io.reactivex.Observable

/**
 * @author wally
 * @date 2021/02/07
 */
class WebPresenter {
    /**
     * 添加一个收藏
     */
    fun saveCollect(userId: String, title: String, url: String) {
        WebDbMaster.saveCollect(userId, title, url)
    }

    /**
     * 删除一个收藏
     */
    fun deleteCollectById(id: Int): Observable<Boolean> {
        return Observable.create {
            WebDbMaster.deleteCollectById(id)
            it.onNext(true)
        }
    }

    /**
     * 收藏或取消收藏
     */
    fun toggleCollect(userId: String, title: String, url: String): Observable<Boolean> {
        return Observable.create {
            val entity = WebDbMaster.getCollectByUrl(userId, url)
            //已收藏，取消收藏
            if (entity != null) {
                WebDbMaster.deleteCollectById(entity.id)
            } else {
                //未收藏，增加收藏
                WebDbMaster.saveCollect(userId, title, url)
            }
            it.onNext(true)
        }
    }

    /**
     * 判断某个Url，是否已经被收藏了
     */
    fun isCollect(userId: String, url: String): Observable<Boolean> {
        return Observable.create {
            it.onNext(WebDbMaster.isCollect(userId, url))
        }
    }

    /**
     * 根据url，查找收藏记录
     */
    fun getCollectByUrl(userId: String, url: String): Observable<WebCollectEntity?> {
        return Observable.create {
            val entity = WebDbMaster.getCollectByUrl(userId, url)
            if (entity == null) {
                it.onComplete()
            } else {
                it.onNext(entity)
            }
        }
    }

    /**
     * 获取某个用户Id，收藏记录列表
     */
    fun getCollectList(userId: String): Observable<List<WebCollectEntity>> {
        return Observable.create {
            it.onNext(WebDbMaster.getCollectList(userId))
        }
    }
}