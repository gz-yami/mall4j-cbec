/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 虚拟商品留言信息
 * @author lhd
 */
@Data
public class VirtualRemarkVO {
    /**
     * 商品id
     */
    @Schema(description = "商品id" )
    private Long prodId;

    /**
     * skuid
     */
    @Schema(description = "skuId" )
    private Long skuId;

    @Schema(description = "购物车id")
    private Long basketId;

    /**
     * 留言标题
     */
    @Schema(description = "留言标题" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    /**
     * 留言内容
     */
    @Schema(description = "留言内容" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String value;

    /**
     * 是否必填
     */
    @Schema(description = "是否必填" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isRequired;

}
