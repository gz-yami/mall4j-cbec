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


/**
 * 装修商品排序
 * @author lhd
 * @date 2023/01/30
 */
public enum EsRenovationProductSortEnum {

    /**
     * 上架时间升序
     */
    CREATE_TIME_ASC(0, "createTime"),

    /**
     * 上架时间降序
     */
    CREATE_TIME_DESC(1, "createTime"),

    /**
     * 销量倒序
     */
    SALE_NUM_DESC(2, "saleNum"),

    /**
     * 销量正序
     */
    SALE_NUM_ASC(3, "saleNum"),

    /**
     * 评论数量倒序
     */
    COMMENT_NUM_DESC(4, "commentNum"),

    /**
     * 评论数量正序
     */
    COMMENT_NUM_ASC(5, "commentNum"),
    ;

    private final Integer value;

    private final String param;


    public Integer value() {
        return value;
    }

    EsRenovationProductSortEnum(Integer value, String param) {
        this.value = value;
        this.param = param;
    }

    public String param() {
        return param;
    }
}
