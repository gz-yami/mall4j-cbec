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

/**
 * 配送类型
 * @author Yami
 */
public enum DvyType {

    /**
     * 线上发货
     */
    ONLINE(0, "yami.delivery.type.online"),
    /**
     * 快递
     */
    DELIVERY(1, "yami.delivery.type.delivery"),
    /**
     * 自提
     */
    STATION(2, "yami.delivery.type.station"),

    /**
     * 无需快递
     */
    NOT_DELIVERY(3, "yami.delivery.type.notDelivery"),
    /**
     * 同城配送
     */
    SAME_CITY(4, "yami.delivery.type.sameCity")
    ;

    private final Integer num;

    private final String i18nTag;

    public Integer value() {
        return num;
    }

    public String getI18nTag() {
        return i18nTag;
    }

    DvyType(Integer num, String i18nTag) {
        this.num = num;
        this.i18nTag = i18nTag;
    }

    public static DvyType instance(Integer value) {
        DvyType[] enums = values();
        for (DvyType statusEnum : enums) {
            if (statusEnum.value().equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }
}
