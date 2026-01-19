/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.bo;

import lombok.Data;

import java.util.List;

/**
 * 商品信息
 *
 * @author YXF
 * @date 2020-12-23 15:27:24
 */
@Data
public class ProductBO {
    /**
     * 商品id
     */
    private Long prodId;

    /**
     * 商品语言列表
     */
    private List<ProdLangBO> prodLangList;

    /**
     * 商品名称
     */
    private String prodName;

    /**
     * 卖点
     */
    private String brief;

    /**
     * 商品售价
     */
    private Double price;

    /**
     * 市场价
     */
    private Double oriPrice;

    /**
     * 活动价
     */
    private Double activityPrice;

    /**
     * 活动商品原价
     */
    private Double activityOriginalPrice;

    /**
     * 积分价
     */
    private Double scorePrice;

    /**
     * 商品介绍主图
     */
    private String pic;

    /**
     * 商品介绍图片
     */
    private String imgs;

    /**
     * 商品类型(0普通商品 1拼团 2秒杀 3积分)
     */
    private Integer prodType;

    /**
     * 预售状态 1：开启 0：未开启
     */
    private Integer preSellStatus;

    /**
     * 店铺名称 搜索华为的时候，可以把华为的旗舰店搜索出来
     */
    private String shopName;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺类型1自营店 2普通店
     */
    private Integer shopType;

    /**
     * 商品状态
     */
    private Integer status;

    /**
     * 是否有库存
     */
    private Boolean hasStock;

    /**
     * 库存
     */
    private Integer totalStocks;

    /**
     * 销量
     */
    private Integer soldNum;

    /**
     * 实际销量
     */
    private Integer actualSoldNum;

    /**
     * 注水销量
     */
    private Integer waterSoldNum;

    /**
     * 评论数
     */
    private Integer commentNum;

    /**
     * 好评率
     */
    private Double positiveRating;

    /**
     * 配送方式列表
     */
    private List<Integer> deliveries;

    /**
     * 商品创建时间
     */
    private Long createTime;

    /**
     * 商品更新时间
     */
    private Long updateTime;

    /**
     * 商品上架时间
     */
    private Long putawayTime;

    /**
     * 活动开始时间
     */
    private Long activityStartTime;

    /**
     * 商品序号
     */
    private Integer seq;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 平台一级分类id
     */
    private Long primaryCategoryId;

    /**
     * 平台二级分类id
     */
    private Long secondaryCategoryId;

    /**
     * 平台分类id
     */
    private Long categoryId;

    /**
     * 品牌id
     */
    private Long brandId;


    /**
     * 品牌图片
     */
    private String brandImg;


    /**
     * 店铺分类id
     */
    private Long shopCategoryId;

    /**
     * 配送方式
     */
    private String deliveryMode;

    /**
     * 用户端显示该商品(0:不显示， 1：显示)
     */
    private Boolean appDisplay;

    /**
     * 商品类别 0.实物商品 1. 虚拟商品 2.组合商品
     */
    private Integer mold;

    /**
     * 品牌
     */
    private BrandBO brand;

    /**
     * 分类
     */
    private CategoryBO category;

}
