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

import com.baomidou.mybatisplus.annotation.TableField;
import com.yami.shop.bean.model.OrderItem;
import com.yami.shop.bean.model.UserAddrOrder;
import com.yami.shop.delivery.common.model.DeliveryOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Lanhai
 */
@Data
public class OrderParam implements Serializable {
    private static final long serialVersionUID = 6222259729062826852L;

    @Schema(description = "订单ID" )
    private Long orderId;

    @Schema(description = "店铺id" )
    private Long shopId;

    @Schema(description = "产品名称,多个产品将会以逗号隔开" )
    private String prodName;

    @Schema(description = "订购用户ID" )
    private String userId;

    @Schema(description = "订购流水号" )
    private String orderNumber;

    @Schema(description = "总值" )
    private Double total;

    @Schema(description = "实际总值" )
    private Double actualTotal;

    @Schema(description = "支付方式 1 微信支付 2 支付宝" )
    private Integer payType;

    @Schema(description = "用户备注" )
    private String remarks;

    @Schema(description = "卖家备注" )
    private String shopRemarks;

    @Schema(description = "订单状态 1:待付款 2:待发货 3:待收货 4:待评价 5:成功 6:失败 7:待成团" )
    private Integer status;

    @Schema(description = "配送类型 1:快递 2:自提 3：无需快递 4:同城配送" )
    private Integer dvyType;

    @Schema(description = "配送方式ID" )
    private Long dvyId;

    @Schema(description = "物流单号" )
    private String dvyFlowId;

    @Schema(description = "订单运费" )
    private Double freightAmount;

    @Schema(description = "用户订单地址Id" )
    private Long addrOrderId;

    @Schema(description = "订单商品总数" )
    private Integer productNums;

    @Schema(description = "订购时间" )
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "订单更新时间" )
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Schema(description = "付款时间" )
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    @Schema(description = "发货时间" )
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dvyTime;

    @Schema(description = "完成时间" )
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finallyTime;

    @Schema(description = "取消时间" )
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date cancelTime;

    @Schema(description = "是否已经支付，1：已经支付过，0：没有支付过" )
    private Integer isPayed;

    @Schema(description = "用户订单删除状态，0：没有删除， 1：回收站， 2：永久删除" )
    private Integer deleteStatus;

    @Schema(description = "订单退款状态（1:申请退款 2:退款成功 3:部分退款成功 4:退款失败）" )
    private Integer refundStatus;

    @Schema(description = "商家运费减免金额" )
    private Double freeFreightAmount;

    @Schema(description = "会员运费减免金额" )
    private Double platformFreeFreightAmount;

    @Schema(description = "平台优惠总额" )
    private Double platformAmount;

    @Schema(description = "优惠总额" )
    private Double reduceAmount;

    @Schema(description = "订单类型 1团购订单,2秒杀订单,3积分订单,4虚拟商品订单" )
    private Integer orderType;

    @Schema(description = "订单关闭原因 (1:超时未支付 2:退款关闭 4:买家取消 15:已通过货到付款交易)" )
    private Integer closeType;

    @Schema(description = "订单类别 0.实物商品订单 1. 虚拟商品订单" )
    private Integer orderMold;

    @Schema(description = "虚拟商品的留言备注" )
    private String virtualRemark;

    @Schema(description = "店铺名称" )
    @TableField(exist = false)
    private String shopName;

    @Schema(description = "订单项列表" )
    @TableField(exist = false)
    private List<OrderItem> orderItems;

    @Schema(description = "用户订单地址" )
    @TableField(exist = false)
    private UserAddrOrder userAddrOrder;

    @Schema(description = "退款订单编号" )
    @TableField(exist = false)
    private String refundSn;

    @Schema(description = "订单所用积分" )
    @TableField(exist = false)
    private Long score;

    @Schema(description = "买家昵称" )
    @TableField(exist = false)
    private String nickName;

    @Schema(description = "退款状态" )
    @TableField(exist = false)
    private Integer returnMoneySts;

    @Schema(description = "退款类型" )
    @TableField(exist = false)
    private Integer refundType;

    @Schema(description = "亏损金额" )
    @TableField(exist = false)
    private Double lossAmount;

    @Schema(description = "包裹信息" )
    @TableField(exist = false)
    private List<DeliveryOrder> deliveryExpresses;

    @Schema(description = "买家手机号" )
    @TableField(exist = false)
    private String userMobile;

    @Schema(description = "自提点名称" )
    private String stationName;
}
