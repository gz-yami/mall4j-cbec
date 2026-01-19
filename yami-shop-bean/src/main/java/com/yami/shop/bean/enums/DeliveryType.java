/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.enums;

import java.util.Objects;

/**
 * 商品物流选择
 * @author Yami
 */
public enum DeliveryType {

    /**
     * 线上发货
     */
    ONLINE(0, "线上发货", "yami.delivery.type.online"),
    /**
     * 快递
     */
    EXPRESS(1, "快递配送", "yami.delivery.type.delivery"),
    /**
     * 自提
     */
    STATION(2,"用户自提", "yami.delivery.type.station"),
    /**
     * 无需快递
     */
    NO_EXPRESS(3,"无需快递", "yami.delivery.type.notDelivery"),
    /**
     * 同城配送
     */
    SAME_CITY(4,"同城配送", "yami.delivery.type.sameCity"),
    ;

    private final Integer value;
    private final String desc;
    private final String i18nTag;

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public String getI18nTag() {
        return i18nTag;
    }

    DeliveryType(Integer value, String desc, String i18nTag) {
        this.value = value;
        this.desc = desc;
        this.i18nTag = i18nTag;
    }

    public static String getDescription(Integer value){
        DeliveryType deliveryType = getInstance(value);
        return Objects.isNull(deliveryType) ? null : deliveryType.getDesc();
    }

    public static DeliveryType getInstance(Integer value) {
        DeliveryType[] enums = values();
        for (DeliveryType deliveryType : enums) {
            if (deliveryType.getValue().equals(value)){
                return deliveryType;
            }
        }
        return null;
    }
}



