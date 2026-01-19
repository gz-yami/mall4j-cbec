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

import com.yami.shop.bean.model.OrderVirtualInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Yami
 */
@Data
@Schema(description = "我的订单")
public class MyOrderDto {

    @Schema(description = "订单项" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private List<MyOrderItemDto> orderItemDtos;

    @Schema(description = "订单号" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderNumber;

    @Schema(description = "总价" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Double actualTotal;

    @Schema(description = "使用积分" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer userScore;

    @Schema(description = "订单状态" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "订单类型(0普通订单 1团购订单 2秒杀订单)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer orderType;

    @Schema(description = "订单类别 0.实物商品订单 1. 虚拟商品订单" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer orderMold;

    @Schema(description = "订单退款状态（1:申请退款 2:退款成功 3:部分退款成功 4:退款失败）" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer refundStatus;

    @Schema(description = "配送类型 1:快递 2:自提 3：无需快递" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer dvyType;

    @Schema(description = "店铺名称" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String shopName;

    @Schema(description = "商品名称")
    private String prodName;

    @Schema(description = "店铺id" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shopId;

    @Schema(description = "订单运费" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Double freightAmount;

    @Schema(description = "订单创建时间" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Date createTime;

    @Schema(description = "订单支付时间" )
    private Date payTime;

    @Schema(description = "商品总数" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer productNums;

    @Schema(description = "用户自提信息" )
    private String orderNumbers;

    @Schema(description = "用户备注信息" )
    private String remarks;

    @Schema(description = "秒杀订单秒杀信息" )
    private Long seckillId;

    @Schema(description = "支付方式 (1:微信小程序支付 2:支付宝 3微信扫码支付 4 微信h5支付 5微信公众号支付 6支付宝H5支付 7支付宝APP支付 8微信APP支付 9余额支付 10全球PayPal支付)" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer payType;

    @Schema(description = "订单发票id" )
    private Long orderInvoiceId;

    @Schema(description = "核销码" )
    private String writeOffCode;

    @Schema(description = "核销次数类型 -1.多次核销 0.无需核销 1.单次核销" )
    private String writeOffNum;

    @Schema(description = "核销开始时间" )
    private String writeOffStart;

    @Schema(description = "核销结束时间" )
    private String writeOffEnd;

    @Schema(description = "虚拟商品的留言备注" )
    private String virtualRemark;

    @Schema(description = "虚拟商品信息" )
    private List<OrderVirtualInfo> orderVirtualInfoList;

    @Schema(description = "自提点id" )
    private Long stationId;

    @Schema(description = "发货数量" )
    private Integer deliveryCount;


    @Schema(description = "预售时间" )
    private Date preSaleTime;

    @Schema(description = "是否以评价(0.未评价1.已评价)" )
    private Integer isComm;
}
