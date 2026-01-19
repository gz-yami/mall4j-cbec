/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.app.vo;

import com.yami.shop.bean.vo.SkuComboVO;
import com.yami.shop.bean.vo.SkuLangVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Yami
 */
@Data
public class SkuVO implements Serializable {

    private static final long serialVersionUID = 6457261945829470666L;

    @Schema(description = "skuId" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long skuId;
    @Schema(description = "价格" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;

    @Schema(description = "原价" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double oriPrice;

    @Schema(description = "库存(-1表示无穷)" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer stocks;

    @Schema(description = "sku名称" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String skuName;

    @Schema(description = "商品编码")
    private String partyCode;

    @Schema(description = "图片" )
    private String pic;

    @Schema(description = "销售属性组合字符串,格式是p1:v1;p2:v2" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String properties;

    @Schema(description = "积分价格" )
    private Integer skuScore;

    @Schema(description = "产品ID" )
    private Long prodId;

    @Schema(description = "商品数量" )
    private Integer count;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "sku多语言")
    private List<SkuLangVO> skuLangVOList;

    @Schema(description = "商品名称")
    private String prodName;

    @Schema(description = "商品类别 0.实物商品 1. 虚拟商品 2.组合商品")
    private Integer spuMold;

    @Schema(description = "库存点id")
    private Long stockPointId;

    @Schema(description = "入库库存点商品库存")
    private Integer inStockPointStock;

    @Schema(description = "最优惠的平台会员价")
    private Double mostMemberAmount;

    @Schema(description = "最优惠的店铺会员价")
    private Double mostPlatformMemberAmount;

    @Schema(description = "店铺会员价")
    private Double memberAmount;

    @Schema(description = "所选的货币金额" )
    private Double selCurrencyPrice;

    @Schema(description = "skuId集合")
    private List<Long> skuIds;

    @Schema(description = "是否有共同库存")
    private Boolean hasCommonStock;

    @Schema(description = "sku的组合商品规格列表")
    private List<SkuComboVO> skuComboList;

    @Schema(description = "是否有库存" )
    private Boolean isHasStock = true;

}
