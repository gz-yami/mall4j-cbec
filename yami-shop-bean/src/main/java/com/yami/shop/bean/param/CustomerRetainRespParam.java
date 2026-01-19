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

import java.math.BigDecimal;

/**
 * @author Yami
 */
@Data
public class CustomerRetainRespParam {

    @Schema(description = "当前月" )
    private String currentMonth;

    @Schema(description = "新访问/成交客户数" )
    private Integer newCustomers;

    @Schema(description = "第1月留存" )
    private Integer firstMonthRemain;
    @Schema(description = "第1月留存率" )
    private BigDecimal firstMonthRemainRate;

    @Schema(description = "第2月留存" )
    private Integer secondMonthRemain;
    @Schema(description = "第2月留存率" )
    private BigDecimal secondMonthRemainRate;

    @Schema(description = "第3月留存" )
    private Integer thirdMonthRemain;
    @Schema(description = "第3月留存率" )
    private BigDecimal thirdMonthRemainRate;

    @Schema(description = "第4月留存" )
    private Integer fourthMonthRemain;
    @Schema(description = "第4月留存率" )
    private BigDecimal fourthMonthRemainRate;

    @Schema(description = "第5月留存" )
    private Integer fifthMonthRemain;
    @Schema(description = "第5月留存率" )
    private BigDecimal fifthMonthRemainRate;

    @Schema(description = "第6月留存" )
    private Integer sixthMonthRemain;
    @Schema(description = "第6月留存率" )
    private BigDecimal sixthMonthRemainRate;

}
