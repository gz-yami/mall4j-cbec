/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.security.common.manager;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.yami.shop.common.exception.YamiShopBindException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author 菠萝凤梨
 * @date 2022/1/19 16:02
 */
@Component
public class PasswordManager {
    private static final Logger logger = LoggerFactory.getLogger(PasswordManager.class);

    /**
     * 用于aes签名的key，16位
     */
    @Value("${auth.password.signKey:-mall4j-password}")
    public String passwordSignKey;

    private final static Long TEN_MINUTES = 600000L;

    public String decryptPassword(String data) {
        SecureUtil.disableBouncyCastle();
        AES aes = new AES(passwordSignKey.getBytes(StandardCharsets.UTF_8));
        String decryptStr;
        String decryptPassword;
        try {
            decryptStr = aes.decryptStr(data);
            long currentTimeMillis = System.currentTimeMillis();
            long timestamp = Long.parseLong(decryptStr.substring(0, 13));
            // 签名时间大于十分钟，提示请求超时
            if (timestamp + TEN_MINUTES < currentTimeMillis) {
                throw new YamiShopBindException("yami.password.manager.exception.outTime");
            }
            decryptPassword = decryptStr.substring(13);
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw new YamiShopBindException("yami.password.manager.exception.aesDecodeError", e);
        }
        return decryptPassword;
    }
}
