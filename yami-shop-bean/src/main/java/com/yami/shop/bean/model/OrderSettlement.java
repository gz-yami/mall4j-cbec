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

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Yami
 */
@Data
@TableName("tz_order_settlement")
@Schema(description = "订单结算表")
public class OrderSettlement implements Serializable {

    @TableId
    private Long settlementId;

    @Schema(description = "用户系统内部的订单号" )
    private String payNo;

    @Schema(description = "订单号" )
    private String orderNumber;

    @Schema(description = "支付方式 1 微信支付 2 支付宝" )
    private Integer payType;

    @Schema(description = "支付金额" )
    private Double payAmount;

    @Schema(description = "支付积分" )
    private Long payScore;

    @Schema(description = "用户id" )
    private String userId;

    @Schema(description = "创建时间" )
    private Date createTime;

    @Schema(description = "支付状态" )
    private Integer payStatus;

    @Schema(description = "版本号" )
    private Integer version;

}
