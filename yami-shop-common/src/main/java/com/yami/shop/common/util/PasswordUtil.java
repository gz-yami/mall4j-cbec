/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.common.util;

import com.yami.shop.common.exception.YamiShopBindException;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * 密码检验
 * @author yami
 */
@Slf4j
public class PasswordUtil {

    /**
     * 密码由字母、数字和特殊符号三种字符组成8-20位半角字符，区分大小写(检验时)
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*(\\W|_))[A-Za-z\\d(\\W|_)]{8,20}$");

    public static void check(String password) {
        if (!checkFormat(password)) {
            // 这个异常是提示前端的，要在页面判断密码格式是否正确
            log.error("密码：{}", password);
            throw new YamiShopBindException("yami.password.manager.exception.formatError");
        }
    }

    public static boolean checkFormat(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

}
