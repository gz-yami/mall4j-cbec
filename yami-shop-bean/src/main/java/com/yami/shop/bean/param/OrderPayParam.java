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
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author Yami
 */
@Data
@Accessors(chain = true)
public class OrderPayParam {

    @Schema(description = "支付金额" )
    private Double payActualTotal;

    @Schema(description = "昨天24小时支付金额" )
    private List<Double> payYesterdayActualTotal;

    @Schema(description = "今日支付金额" )
    private List<Double> nowActualTotal;

    @Schema(description = "支付客户数" )
    private Integer payUserCount;

    @Schema(description = "较前一日支付客户数变化比率" )
    private Double yesterdayPayUserRate;

    @Schema(description = "客单价" )
    private Double onePrice;

    @Schema(description = "较前一日客单价变化比率" )
    private Double yesterdayOnePriceRate;

    @Schema(description = "成功退款金额" )
    private Double refund;

    @Schema(description = "较前一日退款金额变化比率" )
    private Double yesterdayRefundRate;

    @Schema(description = "支付订单数" )
    private Integer payOrderCount;

    @Schema(description = "较前一日支付订单数变化比率" )
    private Double yesterdayPayOrderRate;

    @Schema(description = "支付时间hour" )
    private String dates;

    @Schema(description = "支付天数" )
    private Date payDay;
}
