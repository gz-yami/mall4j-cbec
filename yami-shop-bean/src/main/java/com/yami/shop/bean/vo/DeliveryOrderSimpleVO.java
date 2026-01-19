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

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.shop.bean.app.dto.DeliveryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 *
 * @author lhd
 * @date 2024-04-22 15:46:05
 */
@Data
public class DeliveryOrderSimpleVO implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId
    @Schema(description = "订单物流包裹id" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderDeliveryId;

    @Schema(description = "订单号" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderNumber;

    @Schema(description = "订单号" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;

    @Schema(description = "快递公司编号" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long dvyId;

    @Schema(description = "收件人手机号" )
    private String receiverMobile;

    @Schema(description = "快递单号" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String dvyFlowId;

    @Schema(description = "快递公司名称" , requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(exist = false)
    private String dvyName;

    @Schema(description = "发货方式(1.快递 3.无需物流 4.同城配送)" )
    private Integer deliveryType;

    @Schema(description = "邮编" )
    private Integer postalCode;
}
