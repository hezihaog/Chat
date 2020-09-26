package com.hule.dashi.mediaplayer;

/**
 * <b>Package:</b> com.hule.dashi.mediaplayer <br>
 * <b>Create Date:</b> 2019/3/10  5:26 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 输出文件的名字生成器 <br>
 */
public interface OutputFileNameGenerator {
    /**
     * 需要生成时回调
     *
     * @param filePrefix 文件前缀
     * @param fileSuffix 文件后缀
     * @return 生成的文件名
     */
    String generatorName(String filePrefix, String fileSuffix);
}