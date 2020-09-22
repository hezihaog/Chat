package com.zh.android.base.constant


/**
 * Package: com.linghit.base.constant <br></br>
 * Create Date: 2019-07-01  14:10 <br></br>
 * @author: zihe <br></br>
 * Description: 存放请求Url <br></br>
 */
object ApiUrl {
    /**
     * 登录验证码过期时间，单位秒
     */
    const val LOGIN_AUTH_CODE_EXPIRE_TIME = 60

    /**
     * 二维码的scheme
     */
    const val QR_CODE_SCHEME = "chat"

    /**
     * 二维码的path
     */
    const val QR_CODE_USER_PATH = "/user/qrcode"

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
    //get() = "192.168.43.110"
    //get() = "api.chat.com"

    /**
     * 图片服务器地址
     */
    private val IMAGE_HOST: String
        get() = "192.168.211.131"
    //get() = "image.chat.com"

    /**
     * Url基础部分
     */
    private val BASE_URL: String
        get() {
            return "http://${HOST}:9002"
        }

    /**
     * 图片服务器地址
     */
    private val IMAGE_URL: String
        get() {
            return "http://${IMAGE_HOST}/"
        }

    /**
     * 拼接出完整的图片地址Url
     */
    fun getFullImageUrl(imageUrl: String?): String {
        if (imageUrl.isNullOrBlank()) {
            return ""
        }
        //已经拼接过了，不需要再拼接
        if (imageUrl.startsWith(IMAGE_URL)) {
            return imageUrl
        }
        //没有拼接，再拼接返回
        return IMAGE_URL + imageUrl
    }

    /**
     * WebSocket连接Url
     */
    val WS_URL: String
        get() {
            return "ws://${HOST}:9001/ws"
        }

    //---------------------------- 文件 ----------------------------

    /**
     * 上传文件
     */
    val UPLOAD_FILE = "$BASE_URL/file/uploadFile"

    /**
     * 上传多个图片
     */
    val UPLOAD_FILES = "$BASE_URL/file/uploadFiles"

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

    /**
     * 获取验证码
     */
    val GET_AUTH_CODE
        get() = "$BASE_URL/user/getAuthCode"

    /**
     * 验证码登录
     */
    val LOGIN_BY_AUTH_CODE
        get() = "$BASE_URL/user/loginByAuthCode"

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

    /**
     * 上传头像
     */
    val UPLOAD_AVATAR = "${BASE_URL}/user/uploadAvatar"

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
    val GET_CHAT_RECORD_LIST = "${BASE_URL}/chatRecord/getChatRecordList"

    /**
     * 获取用户的所有会话
     */
    val GET_ALL_CONVERSATION = "${BASE_URL}/chatRecord/getAllConversation"

    //---------------------------- 动态 ----------------------------

    /**
     * 获取动态列表
     */
    val GET_MOMENT_LIST = "${BASE_URL}/moment/getMomentList"

    /**
     * 获取动态详情
     */
    val GET_MOMENT_DETAIL = "${BASE_URL}/moment/getMomentDetail"

    /**
     * 获取动态点赞列表
     */
    val GET_MOMENT_LIKE_LIST = "${BASE_URL}/moment/getMomentLikeList"

    /**
     * 获取动态的评论列表
     */
    val GET_MOMENT_COMMENT_LIST = "${BASE_URL}/moment/getMomentCommentList"

    /**
     * 点赞动态
     */
    val LIKE_MOMENT = "${BASE_URL}/moment/likeMoment"

    /**
     * 取消点赞动态
     */
    val REMOVE_LIKE_MOMENT = "${BASE_URL}/moment/removeLikeMoment"

    /**
     * 发布动态
     */
    val ADD_MOMENT = "${BASE_URL}/moment/addMoment"

    /**
     * 转发动态
     */
    val FORWARD_MOMENT = "${BASE_URL}/moment/forwardMoment"

    /**
     * 获取动态转发列表
     */
    val GET_MOMENT_FORWARD_LIST = "${BASE_URL}/moment/getMomentForwardList"
}