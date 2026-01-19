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
public class MemberOverviewParam {

    private Integer dataType;

    private Long currentDay;

    @Schema(description = "累积会员数" )
    private Integer totalMember;
    @Schema(description = "累积会员与之前的  >0 上升/ <0下降 率," )
    private Double totalMemberRate;

    @Schema(description = "新增会员数" )
    private Integer newMember;
    @Schema(description = "新增会员会员与之前的 上升/下降 率" )
    private Double newMemberRate;

    @Schema(description = "支付会员数" )
    private Integer payMember;
    @Schema(description = "支付会员数，变化率" )
    private Double payMemberRate;

    @Schema(description = "领券会员数" )
    private Integer couponMember;
    @Schema(description = "领券会员数，变化率" )
    private Double couponMemberRate;

    @Schema(description = "会员支付金额" )
    private Double memberPayAmount;
    @Schema(description = "会员支付金额，变化率" )
    private Double memberPayAmountRate;

    @Schema(description = "会员支付订单数" )
    private Integer memberPayOrder;
    @Schema(description = "会员支付订单数, 变化率" )
    private Double memberPayOrderRate;

    @Schema(description = "会员客单价" )
    private Double memberSingleAmount;
    @Schema(description = "会员客单价, 变化率" )
    private Double memberSingleAmountRate;

}
