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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author lhd
 */
@Schema(description = "订单详细信息")
@Data
public class OrderDetailVO {

    @Schema(description = "店铺名称" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String shopName;

    @Schema(description = "订单运费" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Double freightAmount;

    @Schema(description = "店铺运费减免金额" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Double freeFreightAmount;

    @Schema(description = "会员运费减免金额" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Double platformFreeFreightAmount;

    @Schema(description = "订单项详细信息" )
    private List<OrderItemDetailVO> orderItemDetailList;
}
