/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.app.dto;

import com.yami.shop.bean.enums.OrderType;
import com.yami.shop.bean.vo.EsProdUpdateVO;
import com.yami.shop.bean.vo.VirtualRemarkVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 多个店铺订单合并在一起的合并类
 * "/confirm" 使用
 *
 * @author Yami
 */
@Data
public class ShopCartOrderMergerDto implements Serializable {

    @Schema(description = "实际总值" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double actualTotal;

    @Schema(description = "商品总值" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double total;

    @Schema(description = "商品总数" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalCount;

    @Schema(description = "订单优惠金额(所有店铺优惠金额和使用积分抵现相加)" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double orderReduce;

    @Schema(description = "订单所需积分" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long scorePrice;

    @Schema(description = "过滤掉的商品项" , requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ShopCartItemDto> filterShopItems;

    @Schema(description = "整个订单可以使用的积分数" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long totalUsableScore;

    @Schema(description = "整个订单最多可以使用的积分数" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long maxUsableScore;

    @Schema(description = "用户使用积分数量" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long usableScore;

    @Schema(description = "积分抵扣金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double totalScoreAmount;

    @Schema(description = "购物积分抵现比例" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double shopUseScore;

    @Schema(description = "等级折扣金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double totalLevelAmount;

    @Schema(description = "免运费金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double freeTransFee;

    @Schema(description = "商家免运费金额")
    private Double shopFreeTransFee;

    @Schema(description = "总运费" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double totalTransFee;

    @Schema(description = "地址Dto" )
    private UserAddrDto userAddr;

    @Schema(description = "每个店铺的购物车信息" , requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ShopCartOrderDto> shopCartOrders;

    @Schema(description = "用户是否选择积分抵现(0不使用 1使用 默认不使用)" )
    private Integer isScorePay;


    @Schema(description = "用户选择的自提id" )
    private Long stationId;

    @Schema(description = "同城配送可用状态 : 1 可用 -1 不在范围内 -2 商家没有配置同城配送信息 -3 起送费不够" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer shopCityStatus;

    @Schema(description = "同城配送起送费" )
    private Double startDeliveryFee;

    @Schema(description = "是否预售订单 1：是 0：不是" )
    private Integer preSellStatus;

    @Schema(description = "订单店铺优惠金额(所有店铺优惠金额)" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double orderShopReduce;

    @Schema(description = "商品类别 0.实物商品 1. 虚拟商品 2.组合商品" )
    private Integer mold;

    @Schema(description = "秒杀商品skuId" )
    private Long seckillSkuId;

    @Schema(description = "秒杀id" )
    private Long seckillId;

    @Schema(description = "虚拟商品留言备注" )
    private List<VirtualRemarkVO> virtualRemarkList;

    @Schema(description = "订单类型" )
    private OrderType orderType;

    @Schema(description = "用户id")
    private String userId;

    /**
     * 语言代码
     */
    private String locale;

    /**
     * 订单中的商品列表
     */
    private List<EsProdUpdateVO> prodList;

    @Schema(description = "多店铺的商品配送方式" )
    private List<DvyTypeDTO> dvyTypes;

    /**
     * 店铺会员优惠金额
     */
    private Double shopMemberAmount;
}
