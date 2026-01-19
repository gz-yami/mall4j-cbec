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

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Yami
 */
@Data
public class ProdEffectRespParam {

    @Schema(description = "商品id" )
    private Long prodId;

    @Schema(description = "商品名称" )
    private String prodName;

    @Schema(description = "商品图片" )
    private String prodUrl;

    @Schema(description = "商品状态" )
    private String prodStatus;

    @Schema(description = "店铺名称" )
    private String shopName;

    @Schema(description = "商品价格" )
    private Double price;

    @Schema(description = "曝光次数" )
    private Integer expose;

    @Schema(description = "曝光人数" )
    private Integer exposePersonNum;

    @Schema(description = "加购人数" )
    private Integer addCartPerson;

    @Schema(description = "加购件数" )
    private Integer addCart;

    @Schema(description = "下单人数" )
    private Integer placeOrderPerson;

    @Schema(description = "支付人数" )
    private Integer payPerson;

    @Schema(description = "单品转化率" )
    private Double singleProdRate;

    @Schema(description = "下单商品件数" )
    private Integer placeOrderNum;

    @Schema(description = "支付商品件数" )
    private Integer payNum;

    @Schema(description = "商品下单金额" )
    private Double placeOrderAmount;

    @Schema(description = "商品支付金额" )
    private Double payAmount;

    @Schema(description = "申请退款订单数" )
    private Integer refundNum;

    @Schema(description = "申请退款人数" )
    private Integer refundPerson;

    @Schema(description = "成功退款订单数" )
    private Integer refundSuccessNum;

    @Schema(description = "成功退款人数" )
    private Integer refundSuccessPerson;

    @Schema(description = "成功退款金额" )
    private Double refundSuccessAmount;

    @Schema(description = "退款率" )
    private Double refundSuccessRate;

    @Hidden
    private Integer status;
}
