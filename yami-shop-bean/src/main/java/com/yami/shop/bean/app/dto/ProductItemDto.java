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

import com.yami.shop.bean.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author LGH
 */
@Data
public class ProductItemDto implements Serializable {

    @Schema(description = "产品名称" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String prodName;

    @Schema(description = "产品个数" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer prodCount;

    @Schema(description = "分类id" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;

    @Schema(description = "产品图片路径" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String pic;

    @Schema(description = "产品价格" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;

    @Schema(description = "产品套餐单价价格" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Double comboPrice;

    @Schema(description = "产品所需积分" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long scorePrice;

    @Schema(description = "产品购买花费积分" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer useScore;

    @Schema(description = "商品总金额" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Double productTotalAmount;

    @Schema(description = "店铺id" )
    private Long shopId;

    @Schema(description = "产品ID" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long prodId;

    @Schema(description = "skuId" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long skuId;

    @Schema(description = "规格名称" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String skuName;

    @Schema(description = "销售属性组合字符串,格式是p1:v1;p2:v2" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String properties;

    @Schema(description = "basketId" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long basketId;

    @Schema(description = "商品实际金额 = 商品总金额 - 分摊的优惠金额 - 分摊的积分抵现金额" )
    private Double actualTotal;

    @Schema(description = "分摊的积分抵现金额" )
    private Double scorePayReduce;

    @Schema(description = "满减满折优惠id，0不主动参与活动（用户没有主动参与该活动），-1主动不参与活动" )
    private Long discountId;

    @Schema(description = "分摊的优惠金额" )
    private Double shareReduce;

    @Schema(description = "是否预售订单 1：是 0：不是" )
    private Integer preSellStatus;

    @Schema(description = "预售发货时间" )
    private Date preSellTime;

    @Schema(description = "能否分摊优惠券优惠金额(1可以 0不可以)" )
    private Integer isShareReduce;

    @Schema(description = "能否分摊积分抵现优惠金额(1可以 0不可以)" )
    private Integer isScoreReduce;

    @Schema(description = "平台分摊的优惠金额" )
    private Double platformShareReduce;

    @Schema(description = "配送方式json" )
    private String deliveryMode;

    @Schema(description = "运费模板id" )
    private Long deliveryTemplateId;

    @Schema(description = "配送方式" )
    private Product.DeliveryModeVO deliveryModeVO;

    @Schema(description = "重量" )
    private Double weight;

    @Schema(description = "体积" )
    private Double volume;

    @Schema(description = "购物车是否勾选" )
    private Integer isChecked;
    @Schema(description = "赠品金额" )
    private Double giveawayAmount;
}
