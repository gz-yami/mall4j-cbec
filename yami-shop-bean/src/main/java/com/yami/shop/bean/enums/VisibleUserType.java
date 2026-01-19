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
 * 可见用户类型 0.全部用户可见 1.标签用户可见 2.指定用户
 * @author lhd
 */
public enum VisibleUserType {


    /**
     * 全部用户可见
     */
    ALL(0),

    /**
     * 标签用户可见
     */
    TAG(1),

    /**
     * 指定用户可见
     */
    SPECIFY(2)

    ;

    private final Integer num;

    public Integer value() {
        return num;
    }

    VisibleUserType(Integer num) {
        this.num = num;
    }

    public static VisibleUserType instance(Integer value) {
        VisibleUserType[] enums = values();
        for (VisibleUserType statusEnum : enums) {
            if (statusEnum.value().equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }
}
