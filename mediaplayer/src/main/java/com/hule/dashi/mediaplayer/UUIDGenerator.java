package com.hule.dashi.mediaplayer;

import java.util.UUID;

/**
 * <b>Package:</b> com.hule.dashi.mediaplayer <br>
 * <b>Create Date:</b> 2019-08-05  17:31 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 音频UUID生成器 <br>
 */
public class UUIDGenerator {
    /**
     * 生成唯一的UUID
     */
    public String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}