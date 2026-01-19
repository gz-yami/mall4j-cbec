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

import com.yami.shop.bean.model.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * @author LGH
 */
@Data
public class DeliveryOrderItemParam {

    @NotBlank(message = "订单号不能为空")
    @Schema(description = "订单号" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderNumber;

    @NotBlank(message = "快递公司id不能为空")
    @Schema(description = "快递公司" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long dvyId;

    @NotBlank(message = "物流单号不能为空")
    @Schema(description = "物流单号" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String dvyFlowId;

    @NotBlank(message = "发货方式不能为空")
    @Schema(description = "发货方式" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer deliveryType;

    @Schema(description = "网点id")
    private Long outletConfigId;

    @Schema(description = "打印机id")
    private Long printerId;

    @Schema(description = "快递公司类型")
    private Integer deliveryCompanyType;

    @Schema(description = "收件人姓名")
    private String receiverName;

    @Schema(description = "收件人电话")
    private String receiverMobile;

    @Schema(description = "用户订单地址Id")
    private Long addrOrderId;

    @Schema(description = "选择的订单项" )
    private List<OrderItem> selectOrderItems;
}
