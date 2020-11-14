package com.zh.android.chat.service.db.login.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * @author wally
 * @date 2020/11/14
 * 登录用户表实体
 */
@Entity(tableName = "tb_login_user")
public class LoginUserEntity implements Serializable {
    private static final long serialVersionUID = -887228982222599777L;

    /**
     * 主键Id
     */
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    /**
     * 用户Id
     */
    @ColumnInfo(name = "user_id")
    private String userId;

    /**
     * 用户名
     */
    @ColumnInfo(name = "user_name")
    private String username;

    /**
     * 昵称
     */
    @ColumnInfo(name = "nickname")
    private String nickname;

    /**
     * 头像
     */
    @ColumnInfo(name = "avatar")
    private String avatar;

    /**
     * 令牌
     */
    @ColumnInfo(name = "token")
    private String token;

    /**
     * 登录标记，只有最后一个登录的用户信息为true，其他都为false
     */
    @ColumnInfo(name = "login_flag")
    private Boolean loginFlag;

    /**
     * 私密锁字符串
     */
    @ColumnInfo(name = "pattern_lock_str")
    private String patternLockStr;

    /**
     * 是否开启私密锁
     */
    @ColumnInfo(name = "is_open_pattern_lock")
    private Boolean isOpenPatternLock;

    public LoginUserEntity() {
    }

    public LoginUserEntity(String userId, String username, String nickname, String avatar, String token, Boolean loginFlag) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.token = token;
        this.loginFlag = loginFlag;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getLoginFlag() {
        return loginFlag;
    }

    public void setLoginFlag(Boolean loginFlag) {
        this.loginFlag = loginFlag;
    }

    public String getPatternLockStr() {
        return patternLockStr;
    }

    public void setPatternLockStr(String patternLockStr) {
        this.patternLockStr = patternLockStr;
    }

    public Boolean getOpenPatternLock() {
        return isOpenPatternLock;
    }

    public void setOpenPatternLock(Boolean openPatternLock) {
        isOpenPatternLock = openPatternLock;
    }

    @Override
    public String toString() {
        return "LoginUserEntity{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", token='" + token + '\'' +
                ", loginFlag=" + loginFlag +
                ", patternLockStr='" + patternLockStr + '\'' +
                ", isOpenPatternLock=" + isOpenPatternLock +
                '}';
    }
}