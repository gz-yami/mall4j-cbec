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

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author FrozenWatermelon
 * @date 2020/12/22
 */
@Data
public class SkuStockVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "skuId")
    private Long skuId;

    @Schema(description = "shopId")
    private Long shopId;

    @Schema(description = "orderNumber")
    private String orderNumber;

    @Schema(description = "orderId")
    private Long orderId;

    @Schema(description = "refundId")
    private Long refundId;

    @Schema(description = "库存数量")
    private Integer stock;

    @Schema(description = "锁定库存数量")
    private Integer lockStock;

    @Schema(description = "销量")
    private Integer sale;

    @Schema(description = "sku和商品的状态，1.商品为上架且sku非禁用  0.商品非上架， 或者sku禁用")
    private Integer status;

    /**
     * 库存分片序号
     * 记录扣除分片的id，回调解锁的时候找到这个分片去扣锁定库存
     */
    @Schema(description = "库存分片序号")
    private Integer zoneNo;

    @Schema(description = "库存点id")
    private Long stockPointId;

    @Schema(description = "库存点类型 1仓库 2门店")
    private Integer stockPointType;

    @Schema(description = "操作类型 LuaOperateEnum")
    private Integer operateType;

    @Schema(description = "组合商品关联的主skuId")
    private Long mainSkuId;

    @Schema(description = "组合数量")
    private Integer comboCount;

    @Schema(description = "商品id")
    private Long prodId;

    @Schema(description = "处理时间")
    private Date time;

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "用户id")
    private String userId;

    @Schema(description = "默认仓库id")
    private Long defaultPointId;

    @Schema(description = "剩余可售卖库存")
    private Integer afterStock;

    public SkuStockVO() {
    }

    public SkuStockVO(Long skuId) {
        this.skuId = skuId;
        this.stock = 0;
        this.lockStock = 0;
        this.sale = 0;
        this.stockPointId = 0L;
    }

    public SkuStockVO(Long skuId, Long stockPointId) {
        this.skuId = skuId;
        this.stockPointId = stockPointId;
        this.stock = 0;
        this.lockStock = 0;
        this.sale = 0;
    }

    public SkuStockVO(String orderNumber, Long skuId, Integer stock, Long stockPointId) {
        this.orderNumber = orderNumber;
        this.skuId = skuId;
        this.stock = stock;
        this.stockPointId = stockPointId;
    }

    public SkuStockVO(Long skuId, Integer stock, Long stockPointId, Integer stockPointType) {
        this.skuId = skuId;
        this.stock = stock;
        this.stockPointId = stockPointId;
        this.stockPointType = stockPointType;
        this.sale = 0;
    }

    public SkuStockVO(Long skuId, Integer stock, Integer lockStock, Integer sale, Long stockPointId) {
        this.skuId = skuId;
        this.stock = stock;
        this.lockStock = lockStock;
        this.sale = sale;
        this.stockPointId = stockPointId;
    }


    public SkuStockVO(Long skuId, Integer stock) {
        this.skuId = skuId;
        this.stock = stock;
        this.sale = 0;
    }
}
