package com.zh.android.chat.service.db.login.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.zh.android.chat.service.db.login.entity.LoginUserEntity;

import java.util.List;

/**
 * @author wally
 * @date 2020/11/14
 * 登录用户表Dao
 */
@Dao
public interface LoginUserDao {
    /**
     * 保存登录用户信息
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveLoginUser(LoginUserEntity entity);

    /**
     * 更新登录用户信息
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateLoginUser(LoginUserEntity entity);

    /**
     * 根据用户Id，查询登录用户信息
     */
    @Query("SELECT * FROM tb_login_user WHERE user_id = :userId")
    LoginUserEntity findByUserId(String userId);

    /**
     * 获取所有登录用户信息
     */
    @Query("SELECT * FROM tb_login_user")
    List<LoginUserEntity> findAll();

    /**
     * 获取当前登录的用户信息
     */
    @Query("SELECT * FROM tb_login_user WHERE login_flag = :flag")
    List<LoginUserEntity> findByLoginFlag(boolean flag);
}