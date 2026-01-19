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
public class MemberDealParam {

    @Schema(description = "成交会员数" )
    private Integer payMemberNum;

    @Schema(description = "成交会员数占比" )
    private Double payMemberNumRate;

    @Schema(description = "支付订单数" )
    private Integer payOrderNum;

    @Schema(description = "客单价" )
    private Double pricePerMember;

    @Schema(description = "支付金额" )
    private Double payAmount;

    @Schema(description = "支付金额占比" )
    private Double payAmountRate;

    public Integer getPayMemberNum() {
        return payMemberNum;
    }

    public void setPayMemberNum(Integer payMemberNum) {
        this.payMemberNum = payMemberNum;
    }

    public Double getPayMemberNumRate() {
        return payMemberNumRate;
    }

    public void setPayMemberNumRate(Double payMemberNumRate) {
        this.payMemberNumRate = payMemberNumRate;
    }

    public Integer getPayOrderNum() {
        return payOrderNum;
    }

    public void setPayOrderNum(Integer payOrderNum) {
        this.payOrderNum = payOrderNum;
    }

    public Double getPricePerMember() {
        return pricePerMember;
    }

    public void setPricePerMember(Double pricePerMember) {
        this.pricePerMember = pricePerMember;
    }

    public Double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Double payAmount) {
        this.payAmount = payAmount;
    }

    public Double getPayAmountRate() {
        return payAmountRate;
    }

    public void setPayAmountRate(Double payAmountRate) {
        this.payAmountRate = payAmountRate;
    }
}
