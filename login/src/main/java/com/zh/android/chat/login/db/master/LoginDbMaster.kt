package com.zh.android.chat.login.db.master

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
        fun saveLoginUser(userId: String, username: String, token: String) {
            //把所有信息的登录标记都先设置为false
            makeAllLoginFlagToFalse()
            val entity = mLoginUserDao.findByUserId(userId)
            //已存在，则更新，否则新增
            if (entity == null) {
                mLoginUserDao.saveLoginUser(
                    LoginUserEntity(
                        userId,
                        username,
                        token,
                        true
                    )
                )
            } else {
                mLoginUserDao.updateLoginUser(
                    LoginUserEntity(
                        entity.id,
                        userId,
                        username,
                        token,
                        true
                    )
                )
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
            val entity = getCurrentLoginUser()
            if (entity != null) {
                entity.loginFlag = true
                mLoginUserDao.updateLoginUser(entity)
            }
        }

        /**
         * 将所有用户信息的登录标记都设置为false
         */
        @JvmStatic
        fun makeAllLoginFlagToFalse() {
            val list = mLoginUserDao.findAll()
            list.map {
                it.apply {
                    loginFlag = false
                }
            }.forEach {
                mLoginUserDao.updateLoginUser(it)
            }
        }
    }
}