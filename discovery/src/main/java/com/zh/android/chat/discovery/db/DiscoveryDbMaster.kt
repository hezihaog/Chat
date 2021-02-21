package com.zh.android.chat.discovery.db

import com.zh.android.chat.service.db.greendao.QrCodeScanHistoryEntityDao
import com.zh.android.chat.service.db.greendao.core.GreenDaoManager
import com.zh.android.chat.service.db.greendao.qr.entity.QrCodeScanHistoryEntity
import java.util.*

/**
 * @author wally
 * @date 2021/02/21
 * 发现模块数据库
 */
class DiscoveryDbMaster {
    companion object {
        /**
         * 二维码扫描Dao
         */
        private val mQrCodeScanHistoryEntityDao =
            GreenDaoManager.getInstance().daoSession.qrCodeScanHistoryEntityDao

        /**
         * 保存一条扫描记录
         * @param userId 用户Id
         * @param qrCodeContent 二维码内容
         */
        @JvmStatic
        fun saveScanHistory(userId: String, qrCodeContent: String) {
            //先查询是否已经存在
            var entity = mQrCodeScanHistoryEntityDao.queryBuilder()
                .where(
                    QrCodeScanHistoryEntityDao.Properties.UserId.eq(userId),
                    QrCodeScanHistoryEntityDao.Properties.QrCodeContent.eq(qrCodeContent)
                )
                .build()
                .unique()
            //不存在时，才插入一条记录
            if (entity == null) {
                entity = QrCodeScanHistoryEntity(null, Date(), userId, qrCodeContent)
                mQrCodeScanHistoryEntityDao.insert(entity)
            }
        }

        /**
         * 删除某条指定id的扫描记录
         */
        @JvmStatic
        fun deleteScanHistoryById(userId: String, id: Long) {
            val list = mQrCodeScanHistoryEntityDao.queryBuilder()
                .where(
                    QrCodeScanHistoryEntityDao.Properties.UserId.eq(userId),
                    QrCodeScanHistoryEntityDao.Properties.Id.eq(id)
                )
                .build()
                .list()
            //存在时，才删除
            if (list.isNotEmpty()) {
                val entity = list[0]
                mQrCodeScanHistoryEntityDao.delete(entity)
            }
        }

        /**
         * 根据用户Id，查询扫描历史列表
         */
        @JvmStatic
        fun getScanHistoryList(userId: String): List<QrCodeScanHistoryEntity> {
            return mQrCodeScanHistoryEntityDao.queryBuilder()
                .where(
                    QrCodeScanHistoryEntityDao.Properties.UserId.eq(userId)
                )
                //倒序排列
                .orderDesc(QrCodeScanHistoryEntityDao.Properties.Id)
                .build()
                .list() ?: listOf()
        }
    }
}