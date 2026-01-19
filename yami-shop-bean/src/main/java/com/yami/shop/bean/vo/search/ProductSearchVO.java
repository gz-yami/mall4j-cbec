/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.vo.search;

import com.yami.shop.bean.bo.CategoryBO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 商品信息
 *
 * @author YXF
 */
@Data
public class ProductSearchVO {

    @Schema(description = "商品id" )
    private Long prodId;

    @Schema(description = "商品名称" )
    private String prodName;

    @Schema(description = "卖点" )
    private String brief;

    @Schema(description = "商品售价" )
    private Double price;

    @Schema(description = "市场价" )
    private Double oriPrice;

    @Schema(description = "活动价(秒杀、团购活动的商品价格)" )
    private Double activityPrice;

    @Schema(description  = "会员价")
    private Double memberPrice;

    @Schema(description = "活动商品原价" )
    private Double activityOriginalPrice;

    @Schema(description = "积分价" )
    private Double scorePrice;

    @Schema(description = "会员价")
    private Double mostPlatformMemberAmount;

    @Schema(description = "商品介绍主图" )
    private String pic;

    @Schema(description = "商品介绍图" )
    private String imgs;

    @Schema(description = "商品类型(0普通商品 1拼团 2秒杀 3积分)" )
    private Integer prodType;

    @Schema(description = "商品类别 0.实物商品 1. 虚拟商品 2.组合商品" )
    private Integer mold;

    @Schema(description = "预售状态 1：开启 0：未开启" )
    private Integer preSellStatus;

    @Schema(description = "店铺名称 搜索华为的时候，可以把华为的旗舰店搜索出来" )
    private String shopName;

    @Schema(description = "店铺id" )
    private Long shopId;

    @Schema(description = "店铺logo" )
    private String shopImg;

    @Schema(description = "店铺类型1自营店" )
    private Integer shopType;

    @Schema(description = "商品状态" )
    private Integer status;

    @Schema(description = "库存" )
    private Integer totalStocks;

    @Schema(description = "销量" )
    private Integer soldNum;

    @Schema(description = "实际销量" )
    private Integer actualSoldNum;

    @Schema(description = "注水销量" )
    private Integer waterSoldNum;

    @Schema(description = "评论数" )
    private Integer commentNum;

    @Schema(description = "好评率" )
    private Double positiveRating;

    @Schema(description = "配送方式" )
    private String deliveryMode;

    @Schema(description = "商品创建时间" )
    private Long createTime;

    @Schema(description = "商品更新时间" )
    private Long updateTime;

    @Schema(description = "商品序号" )
    private Integer seq;

    @Schema(description = "是否置顶" )
    private Integer isTop;

    @Schema(description = "活动id" )
    private Long activityId;

    @Schema(description = "平台一级分类id" )
    private Long primaryCategoryId;

    @Schema(description = "平台二级分类id" )
    private Long secondaryCategoryId;

    @Schema(description = "平台三级分类信息" )
    private CategoryBO category;

    @Schema(description = "品牌id" )
    private Long brandId;

    @Schema(description = "平台分类id" )
    private Long categoryId;

    @Schema(description = "店铺分类id" )
    private Long shopCategoryId;

    @Schema(description = "活动开始时间" )
    private Long activityStartTime;

    @Schema(description = "活动进行中" )
    private Boolean activityInProgress;


    @Schema(description = "商品语言列表" )
    private List<ProductSearchLangVO> prodLangList;

    @Schema(description = "最优惠的平台会员价")
    private Double mostMemberAmount;

    @Schema(description = "活动状态")
    private Integer activityStatus;
}
