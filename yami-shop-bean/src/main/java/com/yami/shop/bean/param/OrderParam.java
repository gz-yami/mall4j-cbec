/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author Yami
 */
@Data
public class OrderParam {

    @Schema(description = "店铺id" )
    private Long shopId;

    /**
     * 订单状态 参考com.yami.shop.bean.enums.OrderStatus
     */
    @Schema(description = "订单状态" )
    private Integer status;

    /**
     * 参考orderType
     */
    @Schema(description = "订单类型: 1团购订单 2秒杀订单,3积分订单" )
    private Integer orderType;

    @Schema(description = "订单类别 0.实物商品订单 1. 虚拟商品订单" )
    private Integer orderMold;

    @Schema(description = "是否已经支付，1：已经支付过，0：，没有支付过" )
    private Integer isPayed;

    @Schema(description = "订购流水号" )
    private String orderNumber;

    @Schema(description = "开始时间" )
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @Schema(description = "结束时间" )
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @Schema(description = "店铺名称" )
    private String shopName;

    @Schema(description = "商品名称" )
    private String prodName;

    @Schema(description = "收货人姓名" )
    private String receiver;

    @Schema(description = "收货人手机号" )
    private String mobile;

    @Schema(description = "物流类型  1:快递 2:自提 3：无需快递 4：同城快递" )
    private Integer dvyType;

    @Schema(description = "订单退款状态参考refundStatus（1:申请退款 2:退款成功 3:部分退款成功 4:退款失败）" )
    private Integer refundStatus;

    @Schema(description = "自提点名称" )
    private String stationName;

    @Schema(description = "支付单号" )
    private String payNo;

    @Schema(description = "支付类型  0:积分支付 1:微信支付 3：支付宝支付 3：余额支付 4:paypal支付" )
    private Integer payType;

    @Schema(description = "语言" )
    private Integer lang;

    @Schema(description = "下单时间排序 0倒序 1正序" )
    private Integer seq;
}
