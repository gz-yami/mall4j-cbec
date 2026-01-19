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
 * 规格组合商品管理表VO
 *
 * @author lhd
 * @date 2023-11-06 11:00:41
 */
@Data
public class SkuComboVO{
    private static final long serialVersionUID = 1L;

    @Schema(description = "规格id")
    private Long skuId;

    @Schema(description = "主商品id")
    private Long prodId;

    @Schema(description = "组合的skuid")
    private Long comboSkuId;

    @Schema(description = "图片")
    private String imgUrl;
    @Schema(description = "组合数量")
    private Integer comboCount;
    @Schema(description = "组合总价")
    private Long totalComboAmount;

    @Schema(description = "商品id")
    private Long comboProdId;
    @Schema(description = "商品名称")
    private String prodName;

    @Schema(description = "规格名称")
    private String skuName;

    @Schema(description = "原销售价")
    private Double price;

    @Schema(description = "库存")
    private Integer stock;
}
