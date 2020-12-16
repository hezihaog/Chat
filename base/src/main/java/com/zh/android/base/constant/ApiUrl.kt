package com.zh.android.base.constant

import java.net.URLEncoder


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
    const val WS_HE_INTERVAL_TIME = 4

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
        get() = "192.168.1.6"

    /**
     * Url基础部分
     */
    private val BASE_URL: String
        get() {
            return "http://${HOST}:9003"
        }

    /**
     * 接口前缀
     */
    private val API_PREFIX: String
        get() {
            return "/api"
        }

    /**
     * V1接口
     */
    private val API_V1: String
        get() {
            return "${BASE_URL}${API_PREFIX}/v1"
        }

    /**
     * 拼接出完整的文件地址Url
     */
    fun getFullFileUrl(fileUrl: String?): String {
        if (fileUrl.isNullOrBlank()) {
            return ""
        }
        //不是服务器的图片资源，不需要拼接
        if (!fileUrl.startsWith("group")) {
            return fileUrl
        }
        //已经拼接过了，不需要再拼接
        if (fileUrl.startsWith(API_V1)) {
            return fileUrl
        }
        //没有拼接，再拼接返回
        return "${DOWNLOAD_FILE}?fileId=${URLEncoder.encode(fileUrl, "UTF-8")}"
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
    val UPLOAD_FILE = "$API_V1/file/uploadFile"

    /**
     * 上传多个文件
     */
    val UPLOAD_FILES = "$API_V1/file/uploadFiles"

    /**
     * 上传多个图片
     */
    val UPLOAD_IMAGES = "$API_V1/file/uploadImages"

    /**
     * 下载文件
     */
    val DOWNLOAD_FILE = "${API_V1}/file/downloadFile"

    //---------------------------- 登录 ----------------------------

    /**
     * 登录
     */
    val LOGIN
        get() = "$API_V1/user/login"

    /**
     * 注册
     */
    val REGISTER
        get() = "$API_V1/user/register"

    /**
     * 获取验证码
     */
    val GET_AUTH_CODE
        get() = "$API_V1/user/getAuthCode"

    /**
     * 验证码登录
     */
    val LOGIN_BY_AUTH_CODE
        get() = "$API_V1/user/loginByAuthCode"

    //---------------------------- 用户 ----------------------------

    /**
     * 获取用户信息
     */
    val GET_USER_INFO = "${API_V1}/user/getUserInfo"

    /**
     * 根据用户名搜索用户信息
     */
    val FIND_BY_USERNAME = "${API_V1}/user/findByUsername"

    /**
     * 更新昵称
     */
    val UPDATE_NICKNAME = "${API_V1}/user/updateNickname"

    /**
     * 更新头像
     */
    val UPDATE_AVATAR = "${API_V1}/user/updateAvatar"

    //---------------------------- 位置 ----------------------------

    /**
     * 更新位置信息
     */
    val UPDATE_POSITION = "${API_V1}/userPosition/updateUserPosition"

    /**
     * 获取附近的人列表
     */
    val GET_VICINITY_USER_LIST = "${API_V1}/userPosition/getVicinityUserList"

    //---------------------------- 好友 ----------------------------

    /**
     * 获取用户的好友列表
     */
    val GET_USER_FRIEND_LIST = "${API_V1}/friend/getUserFriendList"

    /**
     * 发送好友请求
     */
    val SEND_FRIEND_REQUEST = "${API_V1}/friend/sendRequest"

    /**
     * 查找某个用户Id的所有好友请求
     */
    val GET_USER_ALL_FRIEND_REQUEST = "${API_V1}/friend/getUserAllFriendRequest"

    /**
     * 接受好友请求
     */
    val ACCEPT_FRIEND_REQUEST = "${API_V1}/friend/acceptFriendReq"

    /**
     * 忽略好友请求
     */
    val IGNORE_FRIEND_REQUEST = "${API_V1}/friend/ignoreFriendReq"

    //---------------------------- 聊天 ----------------------------
    /**
     * 获取聊天记录列表
     */
    val GET_CHAT_RECORD_LIST = "${API_V1}/chatRecord/getChatRecordList"

    /**
     * 获取用户的所有会话
     */
    val GET_ALL_CONVERSATION = "${API_V1}/chatRecord/getAllConversation"

    /**
     * 删除一条聊天记录
     */
    val DELETE_CHAT_RECORD = "${API_V1}/chatRecord/deleteChatRecord"

    /**
     * 删除和指定好友的整个会话
     */
    val DELETE_CONVERSATION = "${API_V1}/chatRecord/deleteConversation"

    //---------------------------- 动态 ----------------------------

    /**
     * 获取动态列表
     */
    val GET_MOMENT_LIST = "${API_V1}/moment/getMomentList"

    /**
     * 获取视频动态列表
     */
    val GET_MOMENT_LIST_BY_VIDEO_TYPE = "${API_V1}/moment/getMomentListByVideoType"

    /**
     * 搜索动态
     */
    val SEARCH_MOMENT = "${API_V1}/moment/searchMoment"

    /**
     * 获取动态详情
     */
    val GET_MOMENT_DETAIL = "${API_V1}/moment/getMomentDetail"

    /**
     * 获取我的动态列表
     */
    val GET_MY_MOMENT_LIST = "${API_V1}/moment/getMyMomentList"

    /**
     * 获取动态点赞列表
     */
    val GET_MOMENT_LIKE_LIST = "${API_V1}/moment/getMomentLikeList"

    /**
     * 获取动态的评论列表
     */
    val GET_MOMENT_COMMENT_LIST = "${API_V1}/moment/getMomentCommentList"

    /**
     * 点赞动态
     */
    val LIKE_MOMENT = "${API_V1}/moment/likeMoment"

    /**
     * 取消点赞动态
     */
    val REMOVE_LIKE_MOMENT = "${API_V1}/moment/removeLikeMoment"

    /**
     * 发布动态
     */
    val ADD_MOMENT = "${API_V1}/moment/addMoment"

    /**
     * 转发动态
     */
    val FORWARD_MOMENT = "${API_V1}/moment/forwardMoment"

    /**
     * 获取动态转发列表
     */
    val GET_MOMENT_FORWARD_LIST = "${API_V1}/moment/getMomentForwardList"

    /**
     * 增加一条动态评论
     */
    val ADD_MOMENT_COMMENT = "${API_V1}/moment/addMomentComment"

    /**
     * 删除一条动态评论
     */
    val DELETE_MOMENT_COMMENT = "${API_V1}/moment/deleteMomentComment"

    /**
     * 删除动态
     */
    val REMOVE_MOMENT = "${API_V1}/moment/removeMoment"

    /**
     * 获取动态评论的回复列表
     */
    val GET_MOMENT_COMMENT_REPLY_LIST = "${API_V1}/moment/getMomentCommentReplyList"

    /**
     * 增加一条动态的评论的回复，或者回复的回复
     */
    val ADD_MOMENT_COMMENT_REPLY = "${API_V1}/moment/addMomentCommentReply"

    /**
     * 删除一条动态的评论的回复，或者回复的回复
     */
    val REMOVE_MOMENT_COMMENT_REPLY = "${API_V1}/moment/removeMomentCommentReply";

    /**
     * 设置动态为公开或私密
     */
    val SET_MOMENT_PUBLIC_FLAG = "${API_V1}/moment/setMomentPublicFlag";

    //---------------------------- 通知 ----------------------------

    /**
     * 获取通知列表
     */
    val GET_NOTICE_LIST = "${API_V1}/notice/getNoticeList"

    /**
     * 已读一条通知
     */
    val READ_NOTICE = "${API_V1}/notice/readNotice"

    /**
     * 已读所有通知
     */
    val READ_ALL_NOTICE = "${API_V1}/notice/readAllNotice"

    //---------------------------- 商城 ----------------------------

    /**
     * 商城首页列表
     */
    val MALL_INDEX_INFOS = "${API_V1}/mall/index/infos"

    /**
     * 获取商品分类列表
     */
    val MALL_GET_GOODS_CATEGORY = "${API_V1}/mall/goods/categories/getGoodsCategory"

    /**
     * 商品搜索
     */
    val MALL_GOODS_SEARCH = "${API_V1}/mall/goods/search"

    /**
     * 获取商品详情
     */
    val MALL_GET_GOODS_DETAIL = "${API_V1}/mall/goods/getGoodsDetail"

    /**
     * 获取我的购物车列表
     */
    val MALL_CART_ITEM_LIST = "${API_V1}/mall/shoppingCart/cartItemList"

    /**
     * 保存商品到购物车
     */
    val MALL_SAVE_SHOPPING_CART_ITEM = "${API_V1}/mall/shoppingCart/saveShoppingCartItem"

    /**
     * 更新一项购物车商品信息
     */
    val MALL_UPDATE_CART_ITEM = "${API_V1}/mall/shoppingCart/updateCartItem"

    /**
     * 删除一项购物车商品信息
     */
    val MALL_DELETE_CART_ITEM = "${API_V1}/mall/shoppingCart/deleteCartItem"

    /**
     * 获取购物车列表的数量
     */
    val MALL_CART_ITEM_LIST_COUNT = "${API_V1}/mall/shoppingCart/cartItemListCount"

    /**
     * 获取我的订单列表
     */
    val MALL_ORDER_LIST = "${API_V1}/mall/order/orderList"

    /**
     * 获取我的收货地址列表
     */
    val MALL_GET_MY_ADDRESS_LIST = "${API_V1}/mall/user/address/getMyAddressList"

    /**
     * 根据Id，获收货地址
     */
    val MALL_GET_USER_ADDRESS = "${API_V1}/mall/user/address/getUserAddress"

    /**
     * 保存用户收货地址
     */
    val MALL_SAVE_USER_ADDRESS = "${API_V1}/mall/user/address/saveUserAddress"

    /**
     * 更新用户收货地址
     */
    val MALL_UPDATE_USER_ADDRESS = "${API_V1}/mall/user/address/updateUserAddress"

    /**
     * 删除一个收货地址
     */
    val MALL_DELETE_ADDRESS = "${API_V1}/mall/user/address/deleteAddress"

    /**
     * 获取用户的默认地址
     */
    val MALL_GET_DEFAULT_USER_ADDRESS = "${API_V1}/mall/user/address/getDefaultUserAddress"

    /**
     * 获取多个购物项信息
     */
    val MALL_GET_CART_ITEMS_FOR_SETTLE = "${API_V1}/mall/shoppingCart/getCartItemsForSettle"

    /**
     * 创建订单
     */
    val MALL_SAVE_ORDER = "${API_V1}/mall/order/saveOrder"

    /**
     * 支付成功时，主动回调后端更新状态
     */
    val MALL_PAY_SUCCESS = "${API_V1}/mall/order/paySuccess"

    /**
     * 订单详情
     */
    val MALL_ORDER_DETAIL = "${API_V1}/mall/order/orderDetail"

    /**
     * 取消订单
     */
    val MALL_CANCEL_ORDER = "${API_V1}/mall/order/cancelOrder"

    /**
     * 确认收货
     */
    val MALL_FINISH_ORDER = "${API_V1}/mall/order/finishOrder"

    //---------------------------- 代办事项 ----------------------------

    /**
     * 按类型，获取Todo列表
     */
    val TODO_GET_TODO_LIST_BY_STATUS = "${API_V1}/todo/getTodoListByStatus"

    /**
     * 获取代办事项信息
     */
    val TODO_GET_TODO_BY_ID = "${API_V1}/todo/getTodoById"

    /**
     * 增加一个代办事项
     */
    val TODO_ADD_TODO = "${API_V1}/todo/addTodo"

    /**
     * 更新一个代办事项
     */
    val TODO_UPDATE_TODO = "${API_V1}/todo/updateTodo"

    /**
     * 删除一个代办事项
     */
    val TODO_DELETE_TODO = "${API_V1}/todo/deleteTodo"

    /**
     * 更新Todo的状态
     */
    val TODO_UPDATE_TODO_STATUS = "${API_V1}/todo/updateTodoStatus"
}