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
 * 商品分析data数据
 */
/**
 * @author Yami
 */
@Data
public class ProdAnalysisDataParam {

    @Schema(description = "时间 ,例如：2020-07-04" )
    private String currentDay;

    @Schema(description = "新增商品数" )
    private Long newProd;

    @Schema(description = "被访问商品数" )
    private Long visitedProd;

    @Schema(description = "动销商品数" )
    private Long dynamicSale;

    @Schema(description = "商品曝光数" )
    private Long expose;

    @Schema(description = "商品浏览量" )
    private Long browse;

    @Schema(description = "商品访客数" )
    private Long visitor;

    @Schema(description = "加购件数" )
    private Long addCart;

    @Schema(description = "下单件数" )
    private Long orderNum;

    @Schema(description = "支付件数" )
    private Long payNum;

    @Schema(description = "分享访问次数" )
    private Long shareVisit;

}
