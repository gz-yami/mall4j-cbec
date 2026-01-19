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

/**
 * @author Citrus
 * @date 2021/11/25 14:33
 */
@Data
public class OrderChangeAddrParam {

    @Schema(description = "订单id" )
    private Long orderId;

    @Schema(description = "订单流水号" )
    private String orderNumber;

    @Schema(description = "区域ID" )
    private Long areaId;

    @Schema(description = "经度" )
    private Double lng;

    @Schema(description = "纬度" )
    private Double lat;
}
