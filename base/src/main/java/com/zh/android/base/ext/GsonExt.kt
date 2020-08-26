package com.zh.android.base.ext

import com.google.gson.reflect.TypeToken
import com.zh.android.base.util.json.JsonProxy
import java.lang.reflect.Type

/**
 * <b>Package:</b> com.linghit.base.ext <br>
 * <b>Create Date:</b> 2019-07-01  14:51 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> Gson拓展 <br>
 */

/**
 * 拓展Gson的TypeToken
 */
inline fun <reified T> genericGsonTypeToken(): TypeToken<T> = object : TypeToken<T>() {}

/**
 * 泛型实体拓展Gson的TypeToken的type
 * @return Type
 */
inline fun <reified T> genericGsonType(): Type = genericGsonTypeToken<T>().type

/**
 * Map转Json
 */
fun HashMap<String, String>.toJson(): String {
    return JsonProxy.get().toJson(this)
}