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
public class CustomerRfmRespTableParam {

    @Schema(description = "频次" )
    private Integer frequency;

    @Schema(description = "支付金额" )
    private Double payAmount1;
    @Schema(description = "购买客户数" )
    private Integer payBuyers1;
    private Double payBuyers1Rate;
    @Schema(description = "客单价" )
    private Double priceSingle1;

    private Double payAmount2;
    private Integer payBuyers2;
    private Double payBuyers2Rate;
    private Double priceSingle2;

    private Double payAmount3;
    private Integer payBuyers3;
    private Double payBuyers3Rate;
    private Double priceSingle3;


    private Double payAmount4;
    private Integer payBuyers4;
    private Double payBuyers4Rate;
    private Double priceSingle4;

    private Double payAmount5;
    private Integer payBuyers5;
    private Double payBuyers5Rate;
    private Double priceSingle5;

    private Double payAmountTotal;
    private Integer payBuyersTotal;
    private Double payBuyersTotalRate;
    private Double priceSingleTotal;

    @Schema(description = "近期时间；1：R<=30 2：30<R<=90 3：90<R<=180 4：180<R<=365 5：R>365" )
    private Integer recency;
    private String recencyName;

    /**
     * 总金额
     */
    private Double amount;

    /**
     * 用户数量
     */
    private Integer userNum;


}
