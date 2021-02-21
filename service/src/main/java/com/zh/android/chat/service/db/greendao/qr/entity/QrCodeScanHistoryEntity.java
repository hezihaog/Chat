package com.zh.android.chat.service.db.greendao.qr.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

import org.greenrobot.greendao.annotation.Generated;

import java.util.Date;

/**
 * @author wally
 * @date 2021/02/21
 * 二维码扫描历史数据实体类
 */
@Entity(nameInDb = "tb_qr_code_history")
public class QrCodeScanHistoryEntity {
    /**
     * 主键Id
     */
    @Property(nameInDb = "id")
    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "create_time")
    @NotNull
    private Date createTime = new Date();

    /**
     * 用户Id
     */
    @Property(nameInDb = "user_id")
    @NotNull
    private String userId;

    /**
     * 二维码内容
     */
    @Property(nameInDb = "qr_code_content")
    @NotNull
    private String qrCodeContent;

    @Generated(hash = 602783606)
    public QrCodeScanHistoryEntity(Long id, @NotNull Date createTime,
            @NotNull String userId, @NotNull String qrCodeContent) {
        this.id = id;
        this.createTime = createTime;
        this.userId = userId;
        this.qrCodeContent = qrCodeContent;
    }

    @Generated(hash = 1653184698)
    public QrCodeScanHistoryEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQrCodeContent() {
        return this.qrCodeContent;
    }

    public void setQrCodeContent(String qrCodeContent) {
        this.qrCodeContent = qrCodeContent;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}