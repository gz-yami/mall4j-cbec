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
 * @author Yami
 */
@Data
public class MemberOverviewListParam {

    private Long currentDay;

    @Schema(description = "累积会员数" )
    private Integer totalMember;

    @Schema(description = "新增会员数" )
    private Integer newMember;

    @Schema(description = "支付会员数" )
    private Integer payMember;

    @Schema(description = "领券会员数" )
    private Integer couponMember;

    @Schema(description = "会员支付金额" )
    private Double memberPayAmount;

    @Schema(description = "会员支付订单数" )
    private Integer memberPayOrder;

    @Schema(description = "会员客单价" )
    private Double memberSingleAmount;
}
