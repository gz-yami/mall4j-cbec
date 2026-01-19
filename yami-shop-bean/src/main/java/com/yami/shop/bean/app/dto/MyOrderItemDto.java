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

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author LGH
 */
@Schema(description = "我的订单-订单项")
@Data
public class MyOrderItemDto {

    @Schema(description = "商品图片" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String pic;

    @Schema(description = "商品名称" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String prodName;

    @Schema(description = "销售属性组合字符串,格式是p1:v1;p2:v2" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String properties;


    @Schema(description = "评论时间" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Date recTime;

    @Schema(description = "付款时间" )
    private Date payTime;

    @Schema(description = "订单号" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderNumber;

    @Schema(description = "商品数量" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer prodCount;

    @Schema(description = "商品价格" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;

    @Schema(description = "产品购买花费积分" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer useScore;

    @Schema(description = "skuId" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long skuId;

    @Schema(description = "skuName" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String skuName;

    @Schema(description = "订单项id" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderItemId;

    @Schema(description = "商品id" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long prodId;

    @Schema(description = "评论状态： 0 未评价  1 已评价" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer commSts;

    @Schema(description = "退款单类型（1:整单退款,2:单个物品退款）" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer refundType;

    @Schema(description = "退款状态 1.买家申请 2.卖家接受 3.买家发货 4.卖家收货 5.退款成功 6.买家撤回申请 7.商家拒绝 -1.退款关闭" )
    private Integer returnMoneySts;

    @Schema(description = "赠品列表" )
    private List<MyOrderItemDto> giveawayList;

    @Schema(description = "订单类型1团购订单 2秒杀订单 3积分订单" )
    private Integer orderType;

    /**
     * 赠品主订单项id
     */
    @Schema(description = "赠品主订单项id" )
    private Long giveawayOrderItemId;

    @Schema(description = "主商品关联退款赠品id" )
    private String returnGiveawayIds;

    @Schema(description = "预售时间" )
    private Date preSellTime;

    @Schema(description = "预售状态 1：开启 0：未开启")
    private Integer preSellStatus;

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "活动类型 5.赠品 6.组合")
    private Integer activityType;

    @Schema(description = "商品类别 0.实物商品 1. 虚拟商品 2.组合商品")
    private Integer mold;

    @Schema(description = "订单项子组合商品列表" , requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(exist = false)
    private List<MyOrderItemDto> comboList;

    @Schema(description = "商品实际金额 = 商品总金额 - 分摊的优惠金额" )
    private Double actualTotal;
}
