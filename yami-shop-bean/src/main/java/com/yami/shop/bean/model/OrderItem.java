/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Yami
 */
@Data
@TableName("tz_order_item")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 7307405761190788407L;

    @Schema(description = "订单项ID" )
    @TableId(type = IdType.AUTO)
    private Long orderItemId;

    @Schema(description = "店铺id" )
    private Long shopId;

    @Schema(description = "订单编号" )
    private String orderNumber;

    @Schema(description = "产品ID" )
    private Long prodId;

    @Schema(description = "产品SkuID" )
    private Long skuId;

    @Schema(description = "分类id" )
    private Long categoryId;

    @Schema(description = "购物车产品个数" )
    private Integer prodCount;

    @Schema(description = "产品名称" )
    private String prodName;

    @Schema(description = "sku名称" )
    private String skuName;

    @Schema(description = "销售属性组合字符串,格式是p1:v1;p2:v2" )
    private String properties;

    @Schema(description = "产品主图片路径" )
    private String pic;

    @Schema(description = "产品价格" )
    private Double price;

    @Schema(description = "用户Id" )
    private String userId;

    @Schema(description = "商品总金额" )
    private Double productTotalAmount;

    @Schema(description = "购物时间" )
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date recTime;

    @Schema(description = "评论状态： 0 未评价  1 已评价" )
    private Integer commSts;

    @Schema(description = "推广员使用的推销卡号" )
    private String distributionCardNo;

    @Schema(description = "加入购物车的时间" )
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date basketDate;

    @Schema(description = "商品实际金额 = 商品总金额 - 分摊的优惠金额" )
    private Double actualTotal;

    @Schema(description = "分摊的优惠金额" )
    private Double shareReduce;

    @Schema(description = "使用积分" )
    private Long useScore;

    @Schema(description = "状态 -1待发货 0全部发货 其他数量为剩余待发货数量" )
    private Integer status;

    @Schema(description = "订单确认收货获取的积分" )
    private Long gainScore;

    @Schema(description = "分销佣金" )
    private Double distributionAmount;

    @Schema(description = "上级分销佣金" )
    private Double distributionParentAmount;

    @Schema(description = "积分抵扣金额" )
    private Double scoreAmount;

    @Schema(description = "会员折扣金额" )
    private Double memberAmount;

    @Schema(description = "商家优惠券优惠金额" )
    private Double shopCouponAmount;

    @Schema(description = "满减优惠金额" )
    private Double discountAmount;

    @Schema(description = "店铺改价优惠金额" )
    private Double shopChangeFreeAmount;

    @Schema(description = "店铺套餐优惠金额" )
    private Double comboAmount;

    @Schema(description = "赠品金额" )
    private Double giveawayAmount;

    @Schema(description = "运费减免金额" )
    private Double freeFreightAmount;

    @Schema(description = "会员运费减免金额" )
    private Double platformFreeFreightAmount;

    @Schema(description = "单个orderItem的配送类型 1:快递 2:自提 3：无需快递" )
    private Integer dvyType;

    @Schema(description = "是否有赠品" )
    private Integer isGiveaway;

    @Schema(description = "赠送主订单项id" )
    private Long giveawayOrderItemId;
    @Schema(description = "库存点id 自提：自提点id， 其他：仓库id")
    private Long stockPointId;

    @Schema(description = "库存点类型")
    @TableField(exist = false)
    private Integer stockPointType;

    @Schema(description = "主商品关联退款赠品id" )
    @TableField(exist = false)
    private String returnGiveawayIds;

    @Schema(description = "发货改变的数量" )
    @TableField(exist = false)
    private Integer changeNum;

    /**
     * 退款编号（退款编号为null时，说明订单为正常状态）
     */
    @Schema(description = "退款编号" )
    @TableField(exist = false)
    private String refundSn;

    @Schema(description = "退款id" )
    @TableField(exist = false)
    private Long refundId;

    @Schema(description = "退款单类型（1:整单退款,2:单个物品退款）" )
    @TableField(exist = false)
    private Integer refundType;

    @Schema(description = "退款状态" )
    @TableField(exist = false)
    private Integer returnMoneySts;

    @Schema(description = "订单减少金额" )
    @TableField(exist = false)
    private Double chageAmount;

    @Schema(description = "产品中文名称" ,requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(exist = false)
    private String prodNameCn;

    @Schema(description = "产品英文名称" ,requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(exist = false)
    private String prodNameEn;

    @Schema(description = "sku中文名称" ,requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(exist = false)
    private String skuNameCn;

    @Schema(description = "sku英文名称" ,requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(exist = false)
    private String skuNameEn;

    @Schema(description = "分类名称" ,requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(exist = false)
    private String categoryName;

    @Schema(description = "支付金额版本号" )
    private Integer changeAmountVersion;
    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 活动类型 OrderActivityType
     */
    private Integer activityType;


    /**
     * 商品类别 0.实物商品 1. 虚拟商品 2.组合商品
     */
    private Integer mold;

    @Schema(description = "订单项赠品列表" , requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(exist = false)
    private List<OrderItem> giveawayList;

    @Schema(description = "订单项子组合商品列表" , requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(exist = false)
    private List<OrderItem> comboList;

    /**
     * 预售时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date preSellTime;

    @Schema(description = "支付时间" )
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date payTime;

    @Schema(description = "商家补贴的优惠金额" )
    @TableField(exist = false)
    private Double shopShareReduce;


    @Schema(description = "剩余可售卖库存" )
    @TableField(exist = false)
    private Integer afterStock;
}
