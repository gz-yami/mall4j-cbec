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
import java.util.Date;
import java.util.List;

/**
 * 单个店铺的订单信息
 * @author Yami
 */
@Data
public class ShopCartOrderDto implements Serializable{

    @Schema(description = "店铺id" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shopId;

    @Schema(description = "店铺名称" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String shopName;

    @Schema(description = "实际总值" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double actualTotal;

    @Schema(description = "商品总值" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double total;

    @Schema(description = "商品总数" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalCount;

    @Schema(description = "商家包邮减免运费金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double freeTransFee;

    @Schema(description = "会员运费减免金额" )
    private Double platformFreeFreightAmount;

    @Schema(description = "运费" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double transFee;

    @Schema(description = "促销活动优惠金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double discountReduce;

    @Schema(description = "优惠券优惠金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double couponReduce;

    @Schema(description = "积分优惠金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double scoreReduce;

    @Schema(description = "使用积分" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long useScore;

    @Schema(description = "等级优惠券金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double levelReduce;

    @Schema(description = "套餐优惠金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double shopComboAmount;

    @Schema(description = "店铺优惠金额(促销活动 + 优惠券 + 积分优惠金额 + 套餐优惠金额 + 其他)" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double shopReduce;

    @Schema(description = "订单备注信息" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String remarks;

    @Schema(description = "购物车商品（满减）" , requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ShopCartItemDiscountDto> shopCartItemDiscounts;

    @Schema(description = "订单编号" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderNumber;

    @Schema(description = "同城配送可用状态 : 0 不可用 1 可用 -1 不在范围内 -2 商品没有配置同城配送信息 -3 起送费不够" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer shopCityStatus;

    @Schema(description = "同城配送起送费" , requiredMode = Schema.RequiredMode.REQUIRED)
    private double startDeliveryFee;

    @Schema(description = "核销次数类型 -1.多次核销 0.无需核销 1.单次核销" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer writeOffNum;

    @Schema(description = "多次核销次数 -1.无限次" )
    private Integer writeOffMultipleCount;

    @Schema(description = "核销开始时间" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Date writeOffStart;

    @Schema(description = "核销结束时间" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Date writeOffEnd;

    @Schema(description = "是否可以退款 1.可以 0.不可以" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer isRefund;

    @Schema(description = "订单类型，0.普通订单" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer orderType;

    @Schema(description = "配送类型 1:快递 2:自提 3:无需快递 4:同城配送" )
    private Integer dvyType;

}
