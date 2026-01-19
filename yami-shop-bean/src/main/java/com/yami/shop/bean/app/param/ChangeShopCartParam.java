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
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author LGH
 */
@Data
public class ChangeShopCartParam {

    @Schema(description = "购物车ID" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long basketId;

    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID、套餐商品id" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long prodId;

    @Schema(description = "旧的skuId、套餐商品skuId 如果传过来说明在变更sku" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long oldSkuId;

    @NotNull(message = "skuId不能为空")
    @Schema(description = "skuId、套餐商品skuId" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long skuId;

    @NotNull(message = "店铺ID不能为空")
    @Schema(description = "店铺ID" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shopId;

    @NotNull(message = "商品个数不能为空")
    @Schema(description = "商品个数、套餐个数" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer count;

    @Schema(description = "分销推广人卡号" )
    private String distributionCardNo;

    @Schema(description = "套餐id" )
    private Long comboId;

    @Schema(description = "搭配商品Sku id列表" )
    private List<Long> matchingSkuIds;

    @Schema(description = "商品是否勾选 true：勾选 " )
    private Integer isCheck;

    @Schema(description = "满减活动id" )
    private Long discountId;

    @Schema(description  = "是否使用会员优惠 1.使用 0.不使用")
    private Integer useMember;

}
