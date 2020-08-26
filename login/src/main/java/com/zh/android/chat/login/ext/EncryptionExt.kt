package com.zh.android.chat.login.ext

import com.zh.android.base.util.encryption.DigestUtils


/**
 * <b>Package:</b> com.tongwei.smarttoilet.login.http <br>
 * <b>Create Date:</b> 2019-08-23  10:33 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */

/**
 * 使用Md5加密字符串
 */
val String.md5Pwd: String
    get() = DigestUtils.md5DigestAsHex(this.toByteArray())