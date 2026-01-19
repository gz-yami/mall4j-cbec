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

/**
 * @author TRACK
 */
@Data
public class OutletConfigAddrVO {

    @Schema(description = "完整地址")
    private String printAddr;

    @Schema(description = "网点配置id")
    private Long outletConfigId;

    @Schema(description = "是否默认")
    private Integer isDefault;
}
