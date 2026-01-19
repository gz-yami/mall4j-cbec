package com.yami.shop.bean.enums;

import lombok.AllArgsConstructor;

/**
 * 商品类别 0.实物商品 1. 虚拟商品 2.组合商品
 * @author: zsm
 * @date: 2023/5/10 17:04
 */
@AllArgsConstructor
public enum ProdMoldEnum {
    /**
     * 实物商品
     */
    REAL(0, "实物商品"),

    /**
     * 虚拟商品
     */
    VIRTUAL(1, "虚拟商品"),

    /**
     * 组合商品
     */
    COMBO(2, "组合商品");
    private final Integer mold;
    private final String desc;

    public Integer value() {
        return this.mold;
    }

    public String desc() {
        return this.desc;
    }
}
