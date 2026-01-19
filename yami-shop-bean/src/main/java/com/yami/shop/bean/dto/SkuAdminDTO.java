/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.yami.shop.bean.model.SkuLang;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Yami
 */
@Data
public class SkuAdminDTO implements Serializable {
    /**
     * sku ID
     */
    @Schema(description = "sku ID" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long skuId;

    /**
     * 商品ID
     */
    @Schema(description = "商品ID" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long prodId;

    /**
     * 销售属性组合字符串,格式是p1:v1;p2:v2
     */
    @Schema(description = "销售属性组合字符串,格式是p1:v1;p2:v2" )
    private String properties;

    /**
     * 原价
     */
    @Schema(description = "原价" )
    private Double oriPrice;

    /**
     * 价格
     */
    @Schema(description = "价格" )
    private Double price;
    /**
     * 积分价格
     */
    @Schema(description = "积分价格" )
    private Long skuScore;

    /**
     * 更新时，变化的库存
     */
    @Schema(description = "更新时，变化的库存" )
    @TableField(exist = false)
    private Integer changeStock;


    /**
     * 修改时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "修改时间" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Date updateTime;

    /**
     * 记录时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "记录时间" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Date recTime;

    /**
     * 商家编码
     */
    @Schema(description = "商家编码" )
    private String partyCode;

    /**
     * 商品条形码
     */
    @Schema(description = "商品条形码" )
    private String modelId;

    /**
     * sku图片
     */
    @Schema(description = "sku图片" )
    private String pic;

    /**
     * sku名称
     */
    @Schema(description = "sku名称" )
    private String skuName;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称" )
    private String prodName;

    /**
     * 版本号
     */
    @Schema(description = "版本号" )
    private Integer version;

    /**
     * 重量
     */
    @Schema(description = "重量" )
    private Double weight;

    /**
     * 体积
     */
    @Schema(description = "体积" )
    private Double volume;

    /**
     * 库存预警
     */
    @Schema(description = "库存预警")
    private Integer stockWarning;
    /**
     * 状态：0禁用 1 启用
     */
    @Schema(description = "状态：0禁用 1 启用" )
    private Integer status;

    /**
     * 0 正常 1 已被删除
     */
    @Schema(description = "0 正常 1 已被删除" )
    private Integer isDelete;

    /**
     * 语言信息
     */
    @Schema(description = "语言信息" )
    private List<SkuLang> skuLangList;

    @Schema(description = "商品状态" )
    private Integer prodStatus;

    @Schema(description = "店铺id" )
    private Long shopId;


    @Schema(description = "是否参加了套餐或赠品活动，1是0否" )
    private Integer isParticipate;

    /**
     * 库存
     */
    @Schema(description = "可售卖库存" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer stocks;

    /**
     * 实际库存
     */
    @Schema(description = "实际库存" )
    private Integer actualStock;


}
