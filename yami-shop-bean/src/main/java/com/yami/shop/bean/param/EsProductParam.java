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

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 商品信息
 *
 * @author YXF
 * @date 2020-12-23 15:27:24
 */
@Data
public class EsProductParam {

    @Schema(description = "商品id" )
    private Long prodId;

    @Schema(description = "页面传递过来的全文匹配关键字" )
    private String keyword;

    @Schema(description = "商品类型(0普通商品 1拼团 2秒杀 3积分 4套餐 5活动)" )
    private Integer prodType;

    @Schema(description = "商品类别 0.实物商品 1. 虚拟商品 2.组合商品 " )
    private Integer mold;

    @Schema(description = "不是指定的商品类别 0.实物商品 1. 虚拟商品 2.组合商品 " )
    private Integer notMold;

    @Schema(description = "是否筛选掉活动商品" )
    private Integer isActive;

    @Schema(description = "预售状态 1：开启 0：未开启" )
    private Integer preSellStatus;

    @Schema(description = "店铺名称" )
    private String shopName;

    @Schema(description = "店铺id" )
    private Long shopId;

    @Schema(description = "店铺类型1自营店 2普通店" )
    private Integer shopType;

    @Schema(description = "商品状态" )
    private Integer status;

    @Schema(description = "是否有库存" )
    private Boolean hasStock;

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

    @Schema(description = "商品创建时间" )
    private Long createTime;

    @Schema(description = "商品更新时间" )
    private Long updateTime;

    @Schema(description = "活动开始时间" )
    private Long activityStartTime;

    @Schema(description = "品牌id列表（多个id使用逗号拼接）" )
    private String brandIds;

    @Schema(description = "是否置顶" )
    private Integer isTop;

    @Schema(description = "活动id" )
    private Long activityId;

    @Schema(description = "平台一级分类id" )
    private Long primaryCategoryId;

    @Schema(description = "平台二级分类id" )
    private Long secondaryCategoryId;

    @Schema(description = "平台三级分类id" )
    private Long categoryId;

    @Schema(description = "分类ids" )
    private List<Long> categoryIds;

    @Schema(description = "店铺分类id" )
    private Long shopCategoryId;

    @Schema(description = "价格区间查询-最低价" )
    private Long minPrice;

    @Schema(description = "价格区间查询-最高价" )
    private Long maxPrice;

    @Schema(description = "销量区间查询-最低销量" )
    private Long minSaleNum;

    @Schema(description = "销量区间查询-最高销量" )
    private Long maxSaleNum;

    @Schema(description = "spuId列表" )
    private List<Long> prodIds;

    @Schema(description = "排除该spuId列表")
    private List<Long> excludesProdIds;

    @Schema(description = "不为该spuId列表")
    private List<Long> spuIdsExclude;

    @Schema(description = "是否需要活动信息  1:需要  0:不需要" )
    private Integer needActivity;

    @Schema(description = "配送方式" )
    private Integer deliveryMode;

    /**
     * 参考EsProductSortEnum
     */
    @Schema(description = "排序：0创建时间正序 1创建时间倒序,2销量倒序,3销量正序,4商品价格倒序,5商品价格正序,7市场价倒序," +
            "8市场价正序,10商品库存倒序,11商品库存正序,12商品序号倒序,13商品序号正序,14评论数量倒序,15评论数量正序,16根据置顶状态排序," +
            "17实际销量倒序,18实际销量正序,19注水销量倒序,20注水销量正序,21发布时间倒序,22发布时间正序")
    private Integer sort;

    /**
     * 用户端显示该商品(0:不显示， 1：显示)
     */
    private Boolean appDisplay;

    @Schema(description = "不匹配的商品类型(0普通商品 1拼团 2秒杀 3积分 4套餐 5活动)" )
    private List<Integer> mustNotProdTypes;

    /**
     * 不匹配的平台一级分类id
     */
    private Long notPrimaryCategoryId;

    /**
     * 响应数据字段数组
     */
    private String[] fetchSource;

    @Schema(description = "是否获取删除商品，装修+商品es定时任务使用" )
    private Boolean getDelete;

    @Schema(description ="展示商品类型 0.展示指定的商品ids 1.展示瀑布流商品集合")
    private Integer showSpuType;

    /**
     * 装修商品时间排序规则参考,EsTimeRangeEnum
     */
    @Schema(description ="装修商品时间排序规则 0.全部 1.最近一年 2.最近三个月 3.最近一个月 4.最近一个星期")
    private Integer esTimeRange;

    /**
     * 装修商品排序规则参考,EsRenovationProductSortEnum
     */
    @Schema(description = "装修商品排序规则 0.上架时间升序 1.上架时间降序 2.销量倒序 3.销量正序 4.评论数倒序  5.评论数正序")
    private Integer esRenovationSpuSort;

    @Schema(description = "用户id")
    private String userId;

    @Schema(description = "图片域名")
    private String imgDomain;

    @Hidden
    @Schema(description = "品牌id集合")
    private List<Long> brandIdList;

    @Hidden
    @Schema(description = "语言类型")
    private Integer lang;

    @Hidden
    @Schema(description = "默认语言")
    private Integer defaultLang;

    @Hidden
    @Schema(description = "排序参数名")
    private String sortParamName;

    @Hidden
    @Schema(description = "排序顺序: asc/desc")
    private String sortOrder;

    @Hidden
    @Schema(description = "是否活动信息查询")
    private boolean activityQuery;
}
