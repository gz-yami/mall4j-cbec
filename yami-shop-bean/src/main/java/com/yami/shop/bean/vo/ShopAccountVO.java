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
 * @author lhd
 * @date 2021/8/10 16:00
 */
@Data
public class ShopAccountVO {
    private static final long serialVersionUID = 1L;

    @Schema(description = "店铺id 0是平台" )
    private Long shopId;

    @Schema(description = "店铺名称 平台是官方店" )
    private String shopName;

    @Schema(description = "微信金额" )
    private Double wechatAmount;

    @Schema(description = "支付宝金额" )
    private Double alipayAmount;

    @Schema(description = "余额金额" )
    private Double balanceAmount;

    @Schema(description = "paypal金额" )
    private Double paypalAmount;

    @Schema(description = "积分数目" )
    private Long scoreCount;

    @Schema(description = "合计" )
    private Double total;
}
