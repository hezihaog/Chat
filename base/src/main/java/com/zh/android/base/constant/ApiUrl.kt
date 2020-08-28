package com.zh.android.base.constant


/**
 * Package: com.linghit.base.constant <br></br>
 * Create Date: 2019-07-01  14:10 <br></br>
 * @author: zihe <br></br>
 * Description: 存放请求Url <br></br>
 */
object ApiUrl {
    /**
     * 心跳间隔时间
     */
    const val WS_HE_INTERVAL_TIME = 15

    /**
     * 平台号
     */
    const val PLATFORM = "23"

    /**
     * 默认分页获取数量
     */
    const val PAGE_SIZE = 20

    /**
     * 第一页，页码
     */
    const val FIRST_PAGE = 1

    /**
     * 请求成功Code
     */
    const val CODE_SUCCESS = 0

    /**
     * 主机地址
     */
    private val HOST: String
        get() = "192.168.1.102"

    /**
     * Url基础部分
     */
    private val BASE_URL: String
        get() {
            return "http://${HOST}:9000"
        }

    /**
     * WebSocket连接Url
     */
    val WS_URL: String
        get() {
            return "ws://${HOST}:9001/ws"
        }

    //---------------------------- 登录 ----------------------------

    /**
     * 登录
     */
    val LOGIN
        get() = "$BASE_URL/user/login"

    /**
     * 注册
     */
    val REGISTER
        get() = "$BASE_URL/user/register"

    //---------------------------- 用户 ----------------------------

    /**
     * 获取用户信息
     */
    val GET_USER_INFO = "${BASE_URL}/user/getUserInfo"

    /**
     * 根据用户名搜索用户信息
     */
    val FIND_BY_USERNAME = "${BASE_URL}/user/findByUsername"

    /**
     * 更新昵称
     */
    val UPDATE_NICKNAME = "${BASE_URL}/user/updateNickname"

    //---------------------------- 好友 ----------------------------

    /**
     * 获取用户的好友列表
     */
    val GET_USER_FRIEND_LIST = "${BASE_URL}/friend/getUserFriendList"

    /**
     * 发送好友请求
     */
    val SEND_FRIEND_REQUEST = "${BASE_URL}/friend/sendRequest"

    /**
     * 查找某个用户Id的所有好友请求
     */
    val GET_USER_ALL_FRIEND_REQUEST = "${BASE_URL}/friend/getUserAllFriendRequest"

    /**
     * 接受好友请求
     */
    val ACCEPT_FRIEND_REQUEST = "${BASE_URL}/friend/acceptFriendReq"

    /**
     * 忽略好友请求
     */
    val IGNORE_FRIEND_REQUEST = "${BASE_URL}/friend/ignoreFriendReq"

    //---------------------------- 聊天 ----------------------------
    /**
     * 获取聊天记录列表
     */
    val GET_CHAT_RECORD_LIST = "${BASE_URL}/chatrecord/getChatRecordList"
}