package com.zh.android.chat.service.module.conversation.model

import com.zh.android.chat.service.module.mine.model.User
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
     * 发消息的用户信息
     */
    val fromUser: User,
    /**
     * 接收消息的用户信息
     */
    val toUser: User,
    /**
     * 类型
     */
    val type: Int,
    /**
     * 是否已读
     */
    val hasRead: Int,
    /**
     * 消息创建时间
     */
    val createTime: String,
    /**
     * 文字消息内容
     */
    val text: TextVO? = null,
    /**
     * 图片消息内容
     */
    val image: ImageVO? = null,
    /**
     * 语音消息内容
     */
    val voice: VoiceVO? = null,
    /**
     * 是否正在播放，UI属性
     */
    val isPlayingVoice: Boolean = false
) : Serializable {
    companion object {
        private const val serialVersionUID = -8591987583696258173L
    }

    /**
     * 文字信息
     */
    class TextVO(
        /**
         * 文字内容
         */
        val content: String
    ) : Serializable {
        companion object {
            private const val serialVersionUID = -8591987583696258173L
        }
    }

    /**
     * 图片信息
     */
    class ImageVO(
        /**
         * 图片地址
         */
        var image: String
    ) : Serializable {
        companion object {
            private const val serialVersionUID = -8591987583696258173L
        }
    }

    /**
     * 语音信息
     */
    class VoiceVO(
        /**
         * 语音文件地址
         */
        val mediaSrc: String,
        /**
         * 语音时长
         */
        val mediaTime: Int
    ) : Serializable {
        companion object {
            private const val serialVersionUID = -8591987583696258173L
        }
    }
}