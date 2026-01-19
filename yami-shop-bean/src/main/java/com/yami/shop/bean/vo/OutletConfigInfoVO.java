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
 * @author TRACK
 */
@Data
public class OutletConfigInfoVO {

    @Schema(description = "快递公司类型")
    private Integer deliveryCompanyType;

    @Schema(description = "快递公司名称")
    private String deliveryCompanyName;

    @Schema(description = "是否配置 0否1是")
    private Integer isConfig;

    @Schema(description = "是否默认 0否1是")
    private Integer isDefault;

    @Schema(description = "商家地址列表")
    private List<OutletConfigAddrVO> shopAddrList;
}
