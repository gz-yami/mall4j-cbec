/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Yami
 */
@Data
public class ShopCartDto implements Serializable {

    @Schema(description = "店铺ID" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shopId;

    @Schema(description = "店铺名称" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String shopName;

    @Schema(description = "同城配送启用状态 :  1启用 0未启用 " )
    private Integer shopCityStatus;

    @Schema(description = "购物车满减活动携带的商品" , requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ShopCartItemDiscountDto> shopCartItemDiscounts;

    @Schema(description = "店铺类型1自营店 2普通店" )
    private Integer shopType;

    @Schema(description = "实际总值(商品总值 - 优惠)" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double actualTotal;

    @Schema(description = "商品总值" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double total;

    @Schema(description = "商品积分总值" )
    private Long scoreTotal;

    @Schema(description = "店铺优惠金额(促销活动 + 优惠券 + 积分优惠金额 + 其他)" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double shopReduce;

    @Schema(description = "促销活动优惠金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double discountReduce;

    @Schema(description = "套餐优惠金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double comboReduce;


    @Schema(description = "优惠券优惠金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double couponReduce;

    @Schema(description = "数量" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalCount;

    @Schema(description = "运费" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double transFee;

    @Schema(description = "等级免运费金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double freeTransFee;

    @Schema(description = "平台分类id")
    private Long categoryId;
    @Schema(description = "当前满减/套餐下最新加入购物车的商品项" )
    private Long maxBasketId;

}
