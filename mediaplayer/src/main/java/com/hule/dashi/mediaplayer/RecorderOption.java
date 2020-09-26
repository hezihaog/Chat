package com.hule.dashi.mediaplayer;

import android.text.TextUtils;

/**
 * <b>Package:</b> com.hule.dashi.mediaplayer <br>
 * <b>Create Date:</b> 2019/2/19  4:04 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 录音配置 <br>
 */
public class RecorderOption {
    private String outputDirPath;
    private String filePrefix;
    private String fileSuffix;
    private OutputFileNameGenerator fileNameGenerator;
    private long maxDuration;
    private long minDuration;
    private boolean isDebug;

    private RecorderOption(Builder builder) {
        this.outputDirPath = builder.outputDirPath;
        this.filePrefix = TextUtils.isEmpty(builder.filePrefix) ? "voice_" : builder.filePrefix;
        this.fileSuffix = TextUtils.isEmpty(builder.fileSuffix) ? ".aac" : builder.fileSuffix;
        this.fileNameGenerator = builder.fileNameGenerator == null ?
                new DefaultOutputFileNameGenerator() : builder.fileNameGenerator;
        //语音默认最大长度为一分钟
        this.maxDuration = builder.maxDuration <= 0 ? 60 * 1000 : builder.maxDuration;
        //语音默认最短长度为1秒
        this.minDuration = builder.minDuration <= 0 ? 1000 : builder.minDuration;
        this.isDebug = builder.isDebug;
    }

    public String getOutputDirPath() {
        return outputDirPath;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public OutputFileNameGenerator getFileNameGenerator() {
        return fileNameGenerator;
    }

    public long getMaxDuration() {
        return maxDuration;
    }

    public long getMinDuration() {
        return minDuration;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public static class Builder {
        /**
         * 输出目录路径
         */
        private String outputDirPath;
        /**
         * 文件前缀
         */
        private String filePrefix;
        /**
         * 文件后缀
         */
        private String fileSuffix;
        /**
         * 文件名字生成器
         */
        private OutputFileNameGenerator fileNameGenerator;
        /**
         * 最大录制时长，单位为毫秒，例如一次录制最多60秒
         */
        private long maxDuration;
        /**
         * 最小录制时长，单位为毫秒，例如最小录制时长为1秒
         */
        private long minDuration;
        /**
         * 是否是调试模式
         */
        private boolean isDebug;

        public Builder setOutputDirPath(String outputDirPath) {
            this.outputDirPath = outputDirPath;
            return this;
        }

        public Builder setFilePrefix(String filePrefix) {
            this.filePrefix = filePrefix;
            return this;
        }

        public Builder setFileSuffix(String fileSuffix) {
            this.fileSuffix = fileSuffix;
            return this;
        }

        public void setFileNameGenerator(OutputFileNameGenerator fileNameGenerator) {
            this.fileNameGenerator = fileNameGenerator;
        }

        public Builder setMaxDuration(long maxDuration) {
            this.maxDuration = maxDuration;
            return this;
        }

        public Builder setMinDuration(long minDuration) {
            this.minDuration = minDuration;
            return this;
        }

        public Builder setDebug(boolean debug) {
            isDebug = debug;
            return this;
        }

        public RecorderOption build() {
            return new RecorderOption(this);
        }
    }
}