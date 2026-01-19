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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 汇率获取配置
 * @author yami
 */
@Data
public class ExchangeRate {

    /** **
     * 汇率获取url
     * https://app.exchangerate-api.com/
     */
    private String url;

}
