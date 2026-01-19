/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.bo;

import lombok.Data;

/**
 * @author FrozenWatermelon
 * @date 2020/11/12
 */
@Data
public class ProdLangBO {

    /**
     * 语言
     */
    private Integer lang;

    /**
     * 商品名称-分词搜索字段（用户端）
     */
    private String prodName;

    /**
     * 商品名称-模糊搜索字段（商家、平台端）
     */
    private String prodNameLike;

    /**
     * 卖点
     */
    private String brief;
}
