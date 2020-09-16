package com.zh.android.mqtt;

/**
 * <b>Package:</b> com.tongwei.mqtt <br>
 * <b>Create Date:</b> 2019-09-04  14:58 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> MQTT配置 <br>
 */
public class MqttOption {
    private String serverUrl;
    private String username;
    private String passWord;
    private String clientId;
    private String[] topics;
    private int retryIntervalTime;
    private int connectionTimeout;
    private int keepAliveInterval;

    private MqttOption(Builder builder) {
        this.serverUrl = builder.serverUrl;
        this.username = builder.username;
        this.passWord = builder.passWord;
        this.clientId = builder.clientId;
        this.topics = builder.topics == null ? new String[0] : builder.topics;
        this.retryIntervalTime = builder.retryIntervalTime == 0 ? 2 : builder.retryIntervalTime;
        this.connectionTimeout = builder.connectionTimeout <= 0 ? 10 : builder.connectionTimeout;
        this.keepAliveInterval = builder.keepAliveInterval <= 0 ? 10 : builder.keepAliveInterval;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassWord() {
        return passWord;
    }

    public String getClientId() {
        return clientId;
    }

    public String[] getTopics() {
        return topics;
    }

    public int getRetryIntervalTime() {
        return retryIntervalTime;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public static class Builder {
        /**
         * 服务端地址
         */
        private String serverUrl;
        /**
         * 账号
         */
        private String username;
        /**
         * 密码
         */
        private String passWord;
        /**
         * 客户端Id，必须唯一
         */
        private String clientId;
        /**
         * 需要订阅的主题，连接成功后，会自动订阅
         */
        private String[] topics;
        /**
         * 重试间隔时间，单位为秒
         */
        private int retryIntervalTime;
        /**
         * 连接超时时间，单位秒
         */
        private int connectionTimeout;
        /**
         * 保持活动时间，超过时间没有消息收发将会触发ping消息确认，单位秒
         */
        private int keepAliveInterval;

        public Builder setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassWord(String passWord) {
            this.passWord = passWord;
            return this;
        }

        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder setTopics(String[] topics) {
            this.topics = topics;
            return this;
        }

        public void setRetryIntervalTime(int retryIntervalTime) {
            this.retryIntervalTime = retryIntervalTime;
        }

        public Builder setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder setKeepAliveInterval(int keepAliveInterval) {
            this.keepAliveInterval = keepAliveInterval;
            return this;
        }

        public MqttOption build() {
            return new MqttOption(this);
        }
    }
}