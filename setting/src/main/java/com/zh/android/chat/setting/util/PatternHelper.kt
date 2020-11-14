package com.zh.android.chat.setting.util

import android.text.TextUtils
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.setting.util.SecurityUtil.encrypt

/**
 * @author wally
 * @date 2020/11/09
 */
open class PatternHelper {
    var message: String? = null
        private set
    private var storagePwd: String? = null
    private var tmpPwd: String? = null
    private var times = 0
    var isFinish = false
        private set
    var isOk = false
        private set

    fun validateForSetting(hitIndexList: List<Int>?) {
        isFinish = false
        isOk = false
        if (hitIndexList == null || hitIndexList.size < MAX_SIZE) {
            tmpPwd = null
            message = sizeErrorMsg
            return
        }
        //1. draw first time
        if (TextUtils.isEmpty(tmpPwd)) {
            tmpPwd = convert2String(hitIndexList)
            message = reDrawMsg
            isOk = true
            return
        }
        //2. draw second times
        if (tmpPwd == convert2String(hitIndexList)) {
            message = settingSuccessMsg
            saveToStorage(tmpPwd)
            isOk = true
            isFinish = true
        } else {
            tmpPwd = null
            message = diffPreErrorMsg
        }
    }

    fun validateForChecking(hitIndexList: List<Int>?) {
        isOk = false
        if (hitIndexList == null || hitIndexList.size < MAX_SIZE) {
            times++
            isFinish = times >= MAX_SIZE
            message = pwdErrorMsg
            return
        }
        storagePwd = fromStorage
        if (!TextUtils.isEmpty(storagePwd) && storagePwd == convert2String(hitIndexList)) {
            message = checkingSuccessMsg
            isOk = true
            isFinish = true
        } else {
            times++
            isFinish = times >= MAX_SIZE
            message = pwdErrorMsg
        }
    }

    private val remainTimes: Int
        get() = if (times < 5) MAX_TIMES - times else 0
    private val reDrawMsg: String = "请再次绘制解锁图案"
    private val settingSuccessMsg: String = "手势解锁图案设置成功！"
    private val checkingSuccessMsg: String = "解锁成功！"
    private val sizeErrorMsg: String = String.format("至少连接个%d点，请重新绘制", MAX_SIZE)
    private val diffPreErrorMsg: String = "与上次绘制不一致，请重新绘制"
    private val pwdErrorMsg: String
        get() = String.format("密码错误，还剩%d次机会", remainTimes)

    private fun convert2String(hitIndexList: List<Int>): String {
        return hitIndexList.toString()
    }

    private fun saveToStorage(gesturePwd: String?) {
        val encryptPwd = encrypt(gesturePwd!!)
        //保存私密锁和开关状态
        getLoginService()?.savePatternLockStr(encryptPwd)
        getLoginService()?.saveIsOpenPatternLock(true)
    }

    private val fromStorage: String?
        get() {
            val str = getLoginService()?.getPatternLockStr()
            return if (str.isNullOrBlank()) {
                ""
            } else {
                SecurityUtil.decrypt(str)
            }
        }

    companion object {
        const val MAX_SIZE = 4
        const val MAX_TIMES = 5
    }
}