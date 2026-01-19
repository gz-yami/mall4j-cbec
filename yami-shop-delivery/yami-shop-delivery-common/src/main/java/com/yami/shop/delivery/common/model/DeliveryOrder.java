/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.delivery.common.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.shop.bean.app.dto.DeliveryDto;
import com.yami.shop.delivery.common.param.OrderItemParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 *
 * @author lhd
 * @date 2020-04-22 15:46:05
 */
@Data
@TableName("tz_delivery_order")
public class DeliveryOrder implements Serializable{
    private static final long serialVersionUID = 1L;

    @TableId
    @Schema(description = "订单物流包裹id" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderDeliveryId;

    @Schema(description = "订单号" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderNumber;

    @Schema(description = "快递公司编号" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long dvyId;

    @Schema(description = "收件人手机号" )
    private String receiverMobile;

    @Schema(description = "快递单号" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String dvyFlowId;

    @Schema(description = "快递公司名称" , requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(exist = false)
    private String dvyName;

    @Schema(description = "物流状态 1正常 -1删除" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "包裹商品总数" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer productNums;

    @Schema(description = "创建时间" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Date createTime;

    @Schema(description = "更新时间" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Date updateTime;

    @Schema(description = "删除时间" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Date deleteTime;

    @Schema(description = "发货方式(1.快递 3.无需物流 4.同城配送)" )
    private Integer deliveryType;

    @TableField(exist = false)
    @Schema(description = "订单类型（1团购订单 2秒杀订单）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer orderType;

    @TableField(exist = false)
    @Schema(description = "包裹所有商品" )
    private List<OrderItemParam> orderItems;

    @TableField(exist = false)
    @Schema(description = "物流信息" )
    private DeliveryDto deliveryDto;
}
