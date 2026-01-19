/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.common.enums;

import java.util.Objects;

/**
 * 支付类型
 * @author yami
 */
public enum PayType {

    /** 积分支付*/
    SCOREPAY(0,"积分支付"),

    /** 余额支付*/
    BALANCE(9,"余额支付"),

    /** paypal支付 */
    PAYPAL(10,"paypal支付"),

    /** paypal信用卡支付 */
    PAYPAL_CREDIT(12,"paypal信用卡支付");



    private final Integer num;

    private final String payTypeName;

    public Integer value() {
        return num;
    }

    public String payTypeName() {return payTypeName;}


    PayType(Integer num, String payTypeName){
        this.num = num;
        this.payTypeName = payTypeName;
    }

    public static PayType instance(Integer value) {
        PayType[] enums = values();
        for (PayType statusEnum : enums) {
            if (statusEnum.value().equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }
}
