/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Yami
 */
@Data
@TableName("tz_basket")
public class Basket implements Serializable {

    @TableId
    @Schema(description = "购物车id" )
    private Long basketId;

    @Schema(description = "店铺id" )
    private Long shopId;

    @Schema(description = "商品id" )
    private Long prodId;

    @Schema(description = "skuId" )
    private Long skuId;

    @Schema(description = "用户ID" )
    private String userId;

    @Schema(description = "购物车产品个数" )
    private Integer basketCount;

    @Schema(description = "购物时间" )
    private Date basketDate;

    @Schema(description = "满减活动ID" )
    private Long discountId;

    @Schema(description = "分销推广人卡号" )
    private String distributionCardNo;

    @Schema(description = "套餐id" )
    private Long comboId;

    @Schema(description = "套餐数量" )
    private Integer comboCount;

    @Schema(description = "主购物车id（套餐）" )
    private Long parentBasketId;

    @Schema(description = "商品是否勾选 true：勾选 " )
    private Integer isChecked;

    @Schema(description  = "是否使用会员优惠 1.使用 0.不使用")
    private Integer useMember;
}
