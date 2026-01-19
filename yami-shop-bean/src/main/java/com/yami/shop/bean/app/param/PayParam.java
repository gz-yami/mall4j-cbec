/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Yami
 */
@Data
@Schema(description = "支付参数")
public class PayParam {

    @NotBlank(message = "订单号不能为空")
    @Schema(description = "订单号" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderNumbers;

    @NotNull(message = "支付方式不能为空")
    @Schema(description = "支付方式 (1:微信小程序支付 2:支付宝 3微信扫码支付 4 微信h5支付 5微信公众号支付 6支付宝H5支付 7支付宝APP支付 8微信APP支付 9余额支付 10全球PayPal支付)" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer payType;

    @Schema(description = "支付完成回跳地址" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String returnUrl;

    /**
     * 枚举类：FlowSystemTypeEnum
     */
    @Schema(description = "系统类型 1:pc  2:h5  3:小程序 4:安卓  5:ios ")
    private Integer systemType;
}
