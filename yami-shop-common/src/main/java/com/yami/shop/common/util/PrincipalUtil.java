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

import cn.hutool.core.util.StrUtil;
import com.yami.shop.common.config.Constant;
import org.springframework.util.AntPathMatcher;

import java.util.regex.Pattern;

/**
 * 正则表达式工具
 * @author LGH
 */
public class PrincipalUtil {

    /**
     * 以1开头，后面跟10位数
     */
    public static final String MOBILE_REGEXP = "1[0-9]{10}";
    /**
     * 带有星号的手机号码
     */
    public static final String MOBILE_REGEXP_WITH_ASTERISK = "^1[0-9]{2}([[0-9]*]{4})[0-9]{4}$";
    /**
     * 电话号码
     */
    public static  final String TEL_REGEXP = "([0-9]{3,4}-)?[0-9]{7,8}";
    /**
     * 带有星号的电话号码
     */
    public static  final String TEL_REGEXP_WITH_ASTERISK = "([0-9]{3,4}-)?[0-9]{3}([[0-9]*]{4})[0-9]{0,1}";
    /**
     * 微信号
     */
    public static  final String WX_NUMBER_REGEXP = "[a-zA-Z][a-zA-Z\\d_-]{5,19}$";
    /**
     * QQ号
     */
    public static  final String QQ_NUMBER_REGEXP = "[1-9][0-9]{4,14}";
    /**
     * 1. 用户名不能为纯数字 2. 由数字字母下划线 4-16位组成
     */
    public static final String USER_NAME_REGEXP = "(?!\\d+$)([a-zA-Z0-9_]{4,16})";

    /**
     * 字段名，数字字母下划线
     */
    public static final String FIELD_REGEXP = "([a-zA-Z0-9_]+)";

    /**
     * 只能输入数字字母
     */
    public static final String WITHOUT_CHINESE = "^[A-Za-z0-9]+$";

    /**
     * 由简单的字母数字拼接而成的字符串 不含有下划线，大写字母
     */
    public static final String SIMPLE_CHAR_REGEXP = "([a-z0-9]+)";

    /**
     * 邮箱正则
     */
    public static final String MAIL_REGEXP = "([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((.[a-zA-Z0-9_-]{2,3}){1,2})";

    public static boolean isMobile(String value) {
        if(StrUtil.isBlank(value)) {
            return false;
        }
        return Pattern.matches(MOBILE_REGEXP, value);
    }

    public static boolean isAsteriskMobile(String value) {
        if(StrUtil.isBlank(value)) {
            return false;
        }
        return Pattern.matches(MOBILE_REGEXP_WITH_ASTERISK, value);
    }

    public static boolean isTel(String value) {
        if(StrUtil.isBlank(value)) {
            return false;
        }
        return Pattern.matches(TEL_REGEXP, value);
    }

    public static boolean isAsteriskTel(String value) {
        if(StrUtil.isBlank(value)) {
            return false;
        }
        return Pattern.matches(TEL_REGEXP_WITH_ASTERISK, value);
    }

    public static boolean isWxNumber(String value) {
        if(StrUtil.isBlank(value)) {
            return false;
        }
        return Pattern.matches(WX_NUMBER_REGEXP, value);
    }

    public static boolean isQqNumber(String value) {
        if(StrUtil.isBlank(value)) {
            return false;
        }
        return Pattern.matches(QQ_NUMBER_REGEXP, value);
    }

    public static boolean isUserName(String value) {
        if(StrUtil.isBlank(value)) {
            return false;
        }
        return Pattern.matches(USER_NAME_REGEXP, value);
    }

    public static boolean isMail(String value) {
        if(StrUtil.isBlank(value)) {
            return false;
        }
        return Pattern.matches(MAIL_REGEXP, value);
    }

    /**
     * 是否符合字段规则
     * @param value 输入值
     * @return 匹配结果
     */
    public static boolean isField(String value) {
        return isMatching(FIELD_REGEXP, value);
    }

    public static boolean isMatching(String regexp, String value) {
        if (StrUtil.isBlank(value)) {
            return false;
        }
        return Pattern.matches(regexp, value);
    }

    /**
     * 是否是由简单的字母数字拼接而成的字符串
     * @param value 输入值
     * @return 匹配结果
     */
    public static boolean isSimpleChar(String value) {
        return isMatching(SIMPLE_CHAR_REGEXP, value);
    }

    /**
     * 是否符合原数据库手机号码
     * @param phone 传入的手机号码
     * @param dbPhone 数据库存储的手机号码
     * @param canNull 手机号是否能为空
     * @return 匹配结果
     */
    public static boolean isDbPhone(String phone, String dbPhone, Boolean canNull) {
        if (StrUtil.isBlank(phone) || StrUtil.isBlank(dbPhone)) {
            return canNull;
        }
        if (!phone.contains(Constant.ASTERISK)) {
            // 不含星号默认修改成功
            return true;
        }
        // 最多的星号数量
        int limit = 4;
        AntPathMatcher pathMatcher = new AntPathMatcher();
        if (((phone.length() - phone.replace(Constant.ASTERISK, "").length()) <= limit)
                && pathMatcher.match(phone, dbPhone)) {
            return true;
        }
        return false;
    }
}
