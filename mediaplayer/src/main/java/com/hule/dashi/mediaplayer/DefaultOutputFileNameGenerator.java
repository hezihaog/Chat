package com.hule.dashi.mediaplayer;

import java.util.UUID;

/**
 * <b>Package:</b> com.hule.dashi.mediaplayer <br>
 * <b>Create Date:</b> 2019/3/10  5:29 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 默认文件名称生成器 <br>
 */
public class DefaultOutputFileNameGenerator implements OutputFileNameGenerator {

    @Override
    public String generatorName(String filePrefix, String fileSuffix) {
        return filePrefix + get32UUID() + fileSuffix;
    }

    private static String get32UUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}