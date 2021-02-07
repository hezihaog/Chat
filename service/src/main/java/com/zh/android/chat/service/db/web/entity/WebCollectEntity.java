package com.zh.android.chat.service.db.web.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * @author wally
 * @date 2021/02/07
 * Web网页收藏实体
 */
@Entity(tableName = "tb_web_collect")
public class WebCollectEntity implements Serializable {
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
     * 网页标题
     */
    @ColumnInfo(name = "title")
    private String title;

    /**
     * 网页Url
     */
    @ColumnInfo(name = "url")
    private String url;

    public WebCollectEntity() {
    }

    public WebCollectEntity(String userId, String title, String url) {
        this.userId = userId;
        this.title = title;
        this.url = url;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "WebCollectEntity{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}