/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.event;

/**
 * @author xxw
 * @version 1.0
 * @description:
 * @since 2023/8/7 9:46
 */
public enum FlowSystemTypeEnum {
    /**
     * PC
     */
    PC(1),
    /**
     * H4
     */
    H5(2),
    /**
     * 小程序
     */
    APPLETS(3),
    /**
     * 安卓
     */
    ANDROID(4),
    /**
     * IOS
     */
    IOS(5);

    private final Integer id;

    public Integer value() {
        return id;
    }

    FlowSystemTypeEnum(Integer id) {
        this.id = id;
    }

    public static FlowSystemTypeEnum instance(Integer value) {
        FlowSystemTypeEnum[] enums = values();
        for (FlowSystemTypeEnum statusEnum : enums) {
            if (statusEnum.value().equals(value)) {
                return statusEnum;
            }
        }
        return H5;
    }
}
