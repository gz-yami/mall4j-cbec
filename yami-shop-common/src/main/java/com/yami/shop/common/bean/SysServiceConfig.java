/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.common.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统服务条例开关配置
 * @author Citrus
 */
@Data
public class SysServiceConfig {

    @Schema(description = "是否开启服务条款" )
    private Boolean serviceTermsSwitch;

    @Schema(description = "是否开启隐私策略" )
    private Boolean privacyPolicySwitch;

    @Schema(description = "是否开启商家注册协议" )
    private Boolean merchantRegisterProtocolSwitch;

    @Schema(description = "是否开启开店协议" )
    private Boolean shopProtocolSwitch;
}
