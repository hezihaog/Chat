package com.zh.android.base.util;

import java.util.UUID;

/**
 * @author wally
 * @date 2020/11/09
 * UUID生成工具类
 */
public class UUIDUtil {
    private UUIDUtil() {
    }

    public static String get32UUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}