package com.zh.android.chat.service.module.base.web.db

import com.zh.android.chat.service.db.AppDatabase
import com.zh.android.chat.service.db.web.entity.WebCollectEntity

/**
 * @author wally
 * @date 2021/02/07
 */
class WebDbMaster {
    companion object {
        /**
         * Web收藏Dao
         */
        private val mWebCollectDao = AppDatabase.getInstance().webCollectDao

        /**
         * 添加一个收藏
         */
        @JvmStatic
        fun saveCollect(userId: String, title: String, url: String) {
            var entity = mWebCollectDao.getCollectByUrl(userId, url)
            if (entity == null) {
                //没有收藏，新增
                entity = WebCollectEntity(userId, title, url)
                mWebCollectDao.saveCollect(entity)
            } else {
                entity.run {
                    this.userId = userId
                    this.title = title
                    this.url = url
                }
                mWebCollectDao.updateCollect(entity)
            }
        }

        /**
         * 删除一个收藏
         */
        @JvmStatic
        fun deleteCollectById(id: Int) {
            val entity = mWebCollectDao.getCollectById(id)
            entity?.let {
                mWebCollectDao.deleteCollect(it)
            }
        }

        /**
         * 判断某个Url，是否已经被收藏了
         */
        @JvmStatic
        fun isCollect(userId: String, url: String): Boolean {
            val entity = mWebCollectDao.getCollectByUrl(userId, url)
            return entity != null
        }

        /**
         * 根据url，查找收藏记录
         */
        @JvmStatic
        fun getCollectByUrl(userId: String, url: String): WebCollectEntity? {
            return mWebCollectDao.getCollectByUrl(userId, url)
        }

        /**
         * 获取某个用户Id，收藏记录列表
         */
        @JvmStatic
        fun getCollectList(userId: String): List<WebCollectEntity> {
            return mWebCollectDao.getCollectList(userId)
        }
    }
}