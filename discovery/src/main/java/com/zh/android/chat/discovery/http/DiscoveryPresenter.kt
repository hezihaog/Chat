package com.zh.android.chat.discovery.http

import com.zh.android.chat.discovery.db.DiscoveryDbMaster
import com.zh.android.chat.service.db.greendao.qr.entity.QrCodeScanHistoryEntity
import io.reactivex.Observable

/**
 * @author wally
 * @date 2021/02/21
 */
class DiscoveryPresenter {
    companion object {
        private val TAG = DiscoveryPresenter::class.java.simpleName
    }

    /**
     * 保存一条扫描记录
     */
    fun saveScanHistory(userId: String, qrCodeContent: String): Observable<Boolean> {
        return Observable.create {
            try {
                DiscoveryDbMaster.saveScanHistory(userId, qrCodeContent)
                it.onNext(true)
            } catch (e: Exception) {
                it.onNext(false)
            }
        }
    }

    /**
     * 删除某条指定id的扫描记录
     */
    fun deleteScanHistoryById(userId: String, id: Long): Observable<Boolean> {
        return Observable.create {
            try {
                DiscoveryDbMaster.deleteScanHistoryById(userId, id)
                it.onNext(true)
            } catch (e: Exception) {
                it.onNext(false)
            }
        }
    }

    /**
     * 根据用户Id，查询扫描历史列表
     */
    fun getScanHistoryList(userId: String): Observable<List<QrCodeScanHistoryEntity>> {
        return Observable.create {
            try {
                val list = DiscoveryDbMaster.getScanHistoryList(userId)
                it.onNext(list)
            } catch (e: Exception) {
                it.onNext(listOf())
            }
        }
    }
}