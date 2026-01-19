package com.yami.shop.common.bean;

import lombok.Data;

/**
 * 阿里视频直播配置
 * @author TRACK
 */
@Data
public class AliLive {
    /**
     * 推流域名
     */
    private String pushDomain;

    /**
     * 播流域名
     */
    private String pullDomain;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 推流鉴权url key
     */
    private String pushAuthKey;

    /**
     * 播流鉴权url key
     */
    private String pullAuthKey;

    /**
     * 是否配置https 1是0否
     */
    private Integer isHttps;
}
