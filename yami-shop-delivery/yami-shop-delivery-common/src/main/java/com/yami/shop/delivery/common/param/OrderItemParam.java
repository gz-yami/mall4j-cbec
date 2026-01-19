/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.delivery.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Lanhai
 */
@Data
public class OrderItemParam implements Serializable {

    private static final long serialVersionUID = 7307405761190788407L;

    /**
     * 购物车产品个数
     */
    @Schema(description = "购物车产品个数" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer prodCount;

    /**
     * 产品名称
     */
    @Schema(description = "产品名称" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String prodName;

    /**
     * 产品主图片路径
     */
    @Schema(description = "产品主图片路径" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String pic;
    @Schema(description = "skuId" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long skuId;

    @Schema(description = "销售属性组合字符串,格式是p1:v1;p2:v2" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String properties;

    @Schema(description = "订单项编号" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderItemId;

    @Schema(description = "产品价格" )
    private Double price;

    @Schema(description = "赠送主订单项id")
    private Long giveawayOrderItemId;

    @Schema(description = "活动类型 5.赠品 6.组合商品")
    private Long activityType;

    @Schema(description = "活动关联id")
    private Long activityId;

    @Schema(description = "类型： 0普通商品，1赠品")
    private Integer type;

    @Schema(description = "订单类型（1团购订单 2秒杀订单）" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer orderType;

    @Schema(description = "预售时间" )
    private Date preSaleTime;

    @Schema(description = "预售状态 1：开启 0：未开启")
    private Integer preSellStatus;
}
