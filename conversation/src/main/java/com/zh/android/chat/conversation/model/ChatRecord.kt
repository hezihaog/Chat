package com.zh.android.chat.conversation.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @author wally
 * @date 2020/08/28
 * 聊天记录信息实体
 */
data class ChatRecord(
    /**
     * 聊天消息Id
     */
    val id: String,
    /**
     * 发消息的用户的Id
     */
    @SerializedName(value = "userid")
    val userId: String,
    /**
     * 接收消息的用户的Id
     */
    @SerializedName(value = "friendid")
    val friendId: String,
    /**
     * 是否已读
     */
    val hasRead: Int,
    /**
     * 消息创建时间
     */
    @SerializedName(value = "createtime")
    val createTime: String,
    /**
     * 消息内容
     */
    val message: String
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }
}