package com.yami.shop.common.bean;

import lombok.Data;

/**
 * 邮件配置信息
 * @author gaozijie
 * @since 2024-03-25
 */
@Data
public class MailConfig {
    /**
     * 服务主机（例：stmp.qq.com）
     */
    private String host;

    /**
     * 发送者邮箱账号（例：xxxxxx@qq.com）
     */
    private String username;

    /**
     * 邮箱授权码
     */
    private String password;

    /**
     * 发送者昵称
     */
    private String nickname;
}
