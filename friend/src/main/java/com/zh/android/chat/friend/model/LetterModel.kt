package com.zh.android.chat.friend.model

import java.io.Serializable

/**
 * @author wally
 * @date 2020/08/27
 * 字母条目模型
 */
data class LetterModel(
    /**
     * 字母
     */
    val letter: String
) : Serializable {
    companion object {
        private const val serialVersionUID = -1L
    }
}