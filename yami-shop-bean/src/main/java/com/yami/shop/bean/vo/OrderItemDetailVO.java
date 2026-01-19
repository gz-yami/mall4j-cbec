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
 * @author Pineapple
 * @date 2021/6/9 9:25
 */
@Data
public class OrderItemDetailVO {

    @Schema(description = "订单项id" )
    private Long orderItemId;

    @Schema(description = "店铺名称" )
    private String shopName;

    @Schema(description = "商品名称" )
    private String prodName;

    @Schema(description = "规格名称" )
    private String skuName;

    @Schema(description = "分类名称" )
    private String categoryName;

    @Schema(description = "分类id" )
    private Long categoryId;

    @Schema(description = "购物车产品个数" )
    private Integer prodCount;

    @Schema(description = "商品总金额" )
    private Double productTotalAmount;

    @Schema(description = "赠送主订单项id" )
    private Long giveawayOrderItemId;

    /**
     * 商品实际金额 = 商品总金额 - 分摊的优惠金额
     */
    @Schema(description = "商品实际金额" )
    private Double actualTotal;

    /**
     * 商家优惠金额[shareReduce-platformShareReduce]
     */
    @Schema(description = "商家优惠金额" )
    private Double multishopReduce;

    @Schema(description = "分摊的优惠金额" )
    private Double shareReduce;

    /**
     * 分销金额[推广员佣金+上级推广员佣金]
     */
    @Schema(description = "分销金额" )
    private Double distributionAmount;

    @Schema(description = "使用积分" )
    private Long useScore;

    @Schema(description = "积分抵扣金额" )
    private Double scoreAmount;

    @Schema(description = "会员折扣金额" )
    private Double memberAmount;

    @Schema(description = "商家优惠券优惠金额" )
    private Double shopCouponAmount;

    @Schema(description = "满减优惠金额" )
    private Double discountAmount;

    @Schema(description = "商家运费减免金额" )
    private Double freeFreightAmount;

    @Schema(description = "会员运费减免金额" )
    private Double platformFreeFreightAmount;

    @Schema(description = "店铺改价优惠金额" )
    private Double shopChangeFreeAmount;

    @Schema(description = "店铺改价平台优惠减少金额" )
    private Double platformShopChangeAmount;

    @Schema(description = "退款金额" )
    private Double refundAmount;

    @Schema(description = "退货数量" )
    private Integer refundCount;

    @Schema(description = "套餐优惠金额" )
    private Double comboAmount;

    @Schema(description = "秒杀优惠金额" )
    private Double seckillAmount;

    @Schema(description = "拼团优惠金额" )
    private Double groupAmount;
}
