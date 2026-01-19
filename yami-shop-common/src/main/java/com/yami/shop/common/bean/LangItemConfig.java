/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.common.bean;

import lombok.Data;

/**
 * Paypal支付配置
 * @author cl
 */
@Data
public class LangItemConfig {

    /**
     *  PayPal 的 clientId
     */
    private Integer lang;

    /**
     *  名称
     */
    private String name;

    /**
     *  是否为主语言
     */
    private Integer master;


    /**
     *  语言代码
     */
    private String language;


    /**
     *  是否隐藏语言 1：隐藏 0：显示
     */
    private Integer hide;

}
