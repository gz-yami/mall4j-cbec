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
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Yami
 */
@Data
@Schema(description = "pc订单支付后返回信息")
public class OrderPayInfoParam {


    @Schema(description = "商品名称" )
    private List<String> prodNameList;

    @Schema(description = "收货人姓名" )
    private String receiver;

    @Schema(description = "收货地址" )
    private String userAddr;

    @Schema(description = "收货人手机号" )
    private String mobile;

    @Schema(description = "订单状态 1：为待支付 " )
    private Integer status;

    @Schema(description = "订单过期时间" )
    private Date endTime;

    @Schema(description = "总共需要支付金额" )
    private Double totalFee;

    @Schema(description = "总共需要支付积分" )
    private Integer totalScore;
}
