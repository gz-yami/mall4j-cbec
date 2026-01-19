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
 * @author Citrus
 * @date 2021/9/15 13:27
 */
@Data
public class TakeStockProdVO {

    @Schema(description = "盘点商品id" )
    private Long takeStockProdId;

    @Schema(description = "盘点id" )
    private Long takeStockId;

    @Schema(description = "商品id" )
    private Long prodId;

    @Schema(description = "商品名称" )
    private String prodName;

    @Schema(description = "skuId" )
    private Long skuId;

    @Schema(description = "规格名称" )
    private String skuName;

    @Schema(description = "规格编码" )
    private String partyCode;

    @Schema(description = "sku图片" )
    private String pic;

    @Schema(description = "账面库存" )
    private Integer stocks;

    @Schema(description = "实盘库存" )
    private Integer totalStock;

    @Schema(description = "盈亏类型 0盘平 1盘盈 2盘亏 -1异常" )
    private Integer ioType;

    @Schema(description = "备注" )
    private String remark;

    @Schema(description = "异常原因 1.删除商品 2.盘点期间有库存变动" )
    private Integer exceptionReason;
}
