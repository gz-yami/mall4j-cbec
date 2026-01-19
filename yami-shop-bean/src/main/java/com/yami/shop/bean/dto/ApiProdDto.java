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

import com.yami.shop.bean.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Yami
 */
@Data
@Schema(description = "商品")
public class ApiProdDto {

    @Schema(description = "店铺ID" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shopId;

    @Schema(description = "商品ID" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long prodId;

    @Schema(description = "商品名称" )
    private String prodName;

    @Schema(description = "商品价格" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;

    @Schema(description = "详细描述" )
    private String content;

    @Schema(description = "商品原价" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double oriPrice;

    @Schema(description = "注水销量" )
    private Integer waterSoldNum;

    @Schema(description = "库存量" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalStocks;

    @Schema(description = "销量" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer soldNum;

    @Schema(description = "简要描述,卖点等" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String brief;

    @Schema(description = "当前语言" )
    private Integer lang;

    @Schema(description = "0:下架、1:上架" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "商品ids" )
    private List<Long> prodIds;

    @Schema(description = "skuIds" )
    private List<Long> skuIds;

    @Schema(description = "商品视频" )
    private String video;

    @Schema(description = "商品主图" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String pic;

    @Schema(description = "商品图片列表，以逗号分割" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String imgs;

    @Schema(description = "预售状态 1：开启 0：未开启" )
    private Integer preSellStatus;

    @Schema(description = "预售发货时间" )
    private Date preSellTime;

    @Schema(description = "商品分类id" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;

    @Schema(description = "商品类型(0普通商品 1拼团 2秒杀 3积分 5活动)" )
    private Integer prodType;

    @Schema(description = "商品积分价格" )
    private Long scorePrice;

    @Schema(description = "活动id(prodType)" )
    private Long activityId;

    @Schema(description = "活动参考价" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double activityPrice;

    @Schema(description = "同城配送起送费" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double startDeliveryFee;

    @Schema(description = "配送方式" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Product.DeliveryModeVO deliveryModeVO;


    @Schema(description = "虚拟商品的留言备注" )
    private String virtualRemark;

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

    @Schema(description = "商品类别 0.实物商品 1. 虚拟商品 2.组合商品" )
    private Integer mold;

    @Schema(description = "店铺名称" )
    private String shopName;

    @Schema(description = "运费模板id（0：统一包邮  -1：统一运费  其他：运费模板id）" )
    private Long deliveryTemplateId;

    @Schema(description = "是否在配送范围内" )
    private Boolean isDelivery;

}
