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
public class PayTopParam {

    @Schema(description = "商品id" )
    private Long prodId;

    @Schema(description = "店铺名称" )
    private String shopName;

    @Schema(description = "商品名称" )
    private String prodName;

    @Schema(description = "商品主图" )
    private String pic;

    @Schema(description = "支付金额" )
    private Double payAmount;

    @Schema(description = "支付数量" )
    private Double payCount;
}
