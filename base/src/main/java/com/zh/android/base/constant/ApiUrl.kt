package com.zh.android.base.constant


/**
 * Package: com.linghit.base.constant <br></br>
 * Create Date: 2019-07-01  14:10 <br></br>
 * @author: zihe <br></br>
 * Description: 存放请求Url <br></br>
 */
object ApiUrl {
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
     * Url基础部分
     */
    private val BASE_URL: String
        get() {
            return "http://192.168.1.102:9000"
        }

    //---------------------------- 业务 ----------------------------

    /**
     * 登录
     */
    val LOGIN_LOGIN
        get() = "$BASE_URL/user/login"

    /**
     * 注册
     */
    val LOGIN_REGISTER
        get() = "$BASE_URL/user/register"
}