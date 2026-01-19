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

import com.yami.shop.bean.vo.VirtualRemarkVO;
import com.yami.shop.bean.vo.WarehouseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author LGH
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ShopCartItemDto extends ProductItemDto implements Serializable {
    private static final long serialVersionUID = -8284981156242930909L;

    public ShopCartItemDto() {
    }

    public ShopCartItemDto(Long prodId, Long skuId, Integer prodCount) {
        this.setProdId(prodId);
        this.setSkuId(skuId);
        this.setProdCount(prodCount);
    }


    public ShopCartItemDto(String userId, Long prodId, Long skuId, Integer mainCount) {
        this.userId = userId;
        this.setProdId(prodId);
        this.setSkuId(skuId);
        this.setProdCount(mainCount);

    }

    public Object userDeliveryInfoVo;

    @Schema(description = "购物车ID" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long basketId;

    @Schema(description = "套餐id" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long comboId;

    @Schema(description = "用户id")
    private String userId;

    @Schema(description = "是否为主商品（套餐）" )
    private Integer isMainProd;

    @Schema(description = "套餐数量" )
    private Integer comboCount;

    @Schema(description = "店铺ID" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shopId;

    @Schema(description = "平台分类ID")
    private  Long categoryId;

    @Schema(description = "店铺名称" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String shopName;

    @Schema(description = "商品原价" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double oriPrice;

    @Schema(description = "等级优惠金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double levelReduce;

    @Schema(description = "商家优惠券优惠金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double shopCouponAmount;

    @Schema(description = "满减优惠金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double discountAmount;

    @Schema(description = "套餐优惠金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double comboAmount;

    @Schema(description = "会员运费减免金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double platformFreeFreightAmount;

    @Schema(description = "商家运费减免金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double freeFreightAmount;

    @Schema(description = "推广员使用的推销卡号" )
    private String distributionCardNo;

    @Schema(description = "加入购物车的时间" )
    private Date basketDate;

    @Schema(description = "是否收藏" )
    private Boolean isCollection;

    @Schema(description = "同城配送启用状态 :  1启用 0未启用 " )
    private Integer shopCityStatus;

    @Schema(description = "商品类别 0.实物商品 1. 虚拟商品 2.组合商品" )
    private Integer mold;

    @Schema(description = "虚拟商品留言备注" )
    private List<VirtualRemarkVO> virtualRemarkList;

    @Schema(description = "核销次数类型 -1.多次核销 0.无需核销 1.单次核销" )
    private Integer writeOffNum;

    @Schema(description = "多次核销次数 -1.无限次" )
    private Integer writeOffMultipleCount;

    @Schema(description = "核销有效期 -1.长期有效 0.自定义  x.x天内有效" )
    private Integer writeOffTime;

    @Schema(description = "核销开始时间" )
    private Date writeOffStart;

    @Schema(description = "核销结束时间" )
    private Date writeOffEnd;

    @Schema(description = "是否可以退款 1.可以 0不可以" )
    private Integer isRefund;

    @Schema(description = "主购物车id（套餐）" )
    private Long parentBasketId;

    @Schema(description = "商品类型" )
    private Integer prodType;

    @Schema(description = "虚拟商品留言备注json" )
    private String virtualRemark;

    @Schema(description = "统一运费" )
    private Double deliveryAmount;

    @Schema(description = "标识id" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer itemId;

    @Schema(description = "是否在配送范围内" )
    private Boolean isDelivery;

    @Schema(description = "店铺会员优惠金额")
    private Double shopMemberAmount;

    @Schema(description = "是否使用会员价(0:不使用 1:使用)")
    private Integer useMember;

    @Schema(description = "会员等级")
    private Integer level;

    @Schema(description = "商品店铺会员单价(单个商品原价，进行会员折扣))")
    private Double memberAmount;

    @Schema(description = "商品店铺会员总价(商品促销优惠后的总价格，再进行会员折扣)")
    private Double memberTotalAmount;

    @Schema(description = "商品实际库存")
    private Integer actualStock;

    @Schema(description = "库存点id 自提：自提点id， 其他：仓库id")
    private Long stockPointId;

    @Schema(description = "库存点类型 1仓库 2门店 ")
    private Integer stockPointType;

    @Schema(description = "活动id" )
    private Long activityId;

    @Schema(description = "活动类型 具体类型参考枚举类:OrderActivityType" )
    private Integer activityType;

    @Schema(description = "赠品退货价")
    private Double giveawayRefundPrice;

    @Schema(description = "赠品列表")
    private List<ShopCartItemDto> giveawayShopCartItemList;

    @Schema(description = "组合商品信息")
    private List<ShopCartItemDto> comboShopCartItems;

    @Schema(description = "主sku,组合商品独有")
    private Long mainComboSkuId;

    @Schema(description = "主prod,组合商品独有")
    private Long mainComboProdId;

    @Schema(description = "仓库列表")
    private List<WarehouseVO> warehouseList;

    @Schema(description = "剩余可售卖库存")
    private Integer afterStock;
}
