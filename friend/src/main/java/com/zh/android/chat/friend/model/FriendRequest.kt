package com.zh.android.chat.friend.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @author wally
 * @date 2020/08/27
 * 好友请求实体
 */
data class FriendRequest(
    /**
     * 好友记录Id
     */
    val id: String,
    //下面是申请人的信息
    val username: String,
    /**
     * 头像略缩图
     */
    val picSmall: String,
    /**
     * 头像
     */
    val picNormal: String,
    /**
     * 昵称
     */
    val nickname: String,
    /**
     * 二维码
     */
    @SerializedName(value = "qrcode")
    val qrCode: String,
    /**
     * 客户端Id
     */
    val clientId: String,
    /**
     * 签名
     */
    val sign: String,
    /**
     * 手机号
     */
    val phone: String

) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}