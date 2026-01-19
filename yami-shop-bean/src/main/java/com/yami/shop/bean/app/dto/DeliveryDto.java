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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Yami
 */
@Data
public class DeliveryDto {

    @Schema(description = "物流公司名称" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyName;

    @Schema(description = "物流公司官网" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyHomeUrl;

    @Schema(description = "物流订单号" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String dvyFlowId;

    @Schema(description = "物流状态 0:没有记录 1:已揽收 2:运输途中 201:达到目的城市 3:已签收 4:问题件" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer state;

    @Schema(description = "查询出的物流信息" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private List<DeliveryInfoDto> traces;

    @Schema(description = "快递公司LOGO")
    private String logo;

}
