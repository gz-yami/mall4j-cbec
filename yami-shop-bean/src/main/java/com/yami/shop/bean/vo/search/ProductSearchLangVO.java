/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.vo.search;

import lombok.Data;

/**
 * @author FrozenWatermelon
 * @date 2020/11/12
 */
@Data
public class ProductSearchLangVO {

    /**
     * 商品id
     */
    private Long prodId;

    /**
     * 语言
     */
    private Integer lang;

    /**
     * 商品名称
     */
    private String prodName;

    /**
     * 卖点
     */
    private String brief;

    /**
     * 中文分类名称
     */
    private String categoryName;

    /**
     * 英文分类名称
     */
    private String brandName;
}
