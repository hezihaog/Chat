package com.zh.android.chat.login.db

import com.zh.android.chat.service.db.AppDatabase
import com.zh.android.chat.service.db.login.entity.LoginUserEntity

/**
 * @author wally
 * @date 2020/11/14
 * 登录模块数据库
 */
class LoginDbMaster {
    companion object {
        /**
         * 登录用户信息Dao
         */
        private val mLoginUserDao = AppDatabase.getInstance().loginUserDao

        /**
         * 保存登录用户信息
         */
        @JvmStatic
        fun saveLoginUser(
            userId: String,
            username: String,
            nickname: String,
            avatar: String,
            token: String
        ) {
            //如果当前已经有登录，则把当前登录用户的标记去除
            val currentLoginUser = getCurrentLoginUser()
            currentLoginUser?.let {
                updateLoginUserFlag(it.userId, false)
            }
            val entity = mLoginUserDao.findByUserId(userId)
            //不存在，新增
            if (entity == null) {
                mLoginUserDao.saveLoginUser(
                    LoginUserEntity(
                        userId,
                        username,
                        nickname,
                        avatar,
                        token,
                        true
                    )
                )
            } else {
                //存在，更新
                entity.run {
                    this.userId = userId
                    this.username = username
                    this.nickname = nickname
                    this.avatar = avatar
                    this.token = token
                    this.loginFlag = true
                }
                mLoginUserDao.updateLoginUser(entity)
            }
        }

        /**
         * 获取当前登录的用户信息
         */
        @JvmStatic
        fun getCurrentLoginUser(): LoginUserEntity? {
            val list = mLoginUserDao.findByLoginFlag(true)
            return if (list.isEmpty()) {
                null
            } else {
                list[0]
            }
        }

        /**
         * 获取所有登录过的用户信息
         */
        @JvmStatic
        fun getAllLoginUser(): List<LoginUserEntity> {
            return mLoginUserDao.findAll()
        }

        /**
         * 退出登录，将登录标记设置为false
         */
        @JvmStatic
        fun logout() {
            getCurrentLoginUser()?.let {
                it.loginFlag = true
                mLoginUserDao.updateLoginUser(it)
            }
        }

        /**
         * 更新用户登录标记
         */
        @JvmStatic
        fun updateLoginUserFlag(userId: String, loginFlag: Boolean) {
            val entity = mLoginUserDao.findByUserId(userId)
            entity?.let {
                it.loginFlag = loginFlag
                mLoginUserDao.updateLoginUser(it)
            }
        }

        /**
         * 切换登录账号
         * @param newLoginUserId 新登录用户信息
         */
        fun switchLoginUser(newLoginUserId: String) {
            //先将当前登录用户的标记去掉
            getCurrentLoginUser()?.let {
                it.loginFlag = false
                mLoginUserDao.updateLoginUser(it)
            }
            //再将新的登录用户设置标记
            mLoginUserDao.findByUserId(newLoginUserId)?.let {
                it.loginFlag = true
                mLoginUserDao.updateLoginUser(it)
            }
        }

        /**
         * 保存私密锁字符串
         */
        @JvmStatic
        fun savePatternLockStr(encryptStr: String) {
            getCurrentLoginUser()?.let {
                it.patternLockStr = encryptStr
                mLoginUserDao.updateLoginUser(it)
            }
        }

        /**
         * 获取私密锁字符串
         */
        @JvmStatic
        fun getPatternLockStr(): String {
            return getCurrentLoginUser()?.patternLockStr ?: ""
        }

        /**
         * 保存是否开启私密锁
         */
        fun saveIsOpenPatternLock(isOpen: Boolean) {
            getCurrentLoginUser()?.let {
                it.openPatternLock = isOpen
                mLoginUserDao.updateLoginUser(it)
            }
        }

        /**
         * 是否开启私密锁
         */
        @JvmStatic
        fun isOpenPatternLock(): Boolean {
            return getCurrentLoginUser()?.openPatternLock ?: false
        }
    }
}