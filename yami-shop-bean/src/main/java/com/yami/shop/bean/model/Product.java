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

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.yami.shop.bean.vo.CategoryVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Yami
 */
@Data
@TableName("tz_prod")
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId
    @Schema(description = "商品ID" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long prodId;

    @Schema(description = "店铺id" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shopId;

    @Schema(description = "在平台当中的分类id" )
    private Long categoryId;

    @Schema(description = "在店铺当中的分类id" )
    private Long shopCategoryId;

    @Schema(description = "商品名称" )
    private String prodName;

    @Schema(description = "详细描述" )
    private String content;

    @Schema(description = "简要描述,卖点等" )
    private String brief;

    @Schema(description = "原价" )
    private Double oriPrice;

    @Schema(description = "现价" )
    private Double price;

    @Schema(description = "商品视频" )
    private String video;

    @Schema(description = "商品主图" )
    private String pic;

    @Schema(description = "商品图片" )
    private String imgs;

    @Schema(description = "默认是1，表示正常状态, -1表示删除, 0下架,2平台下架,3平台审核" )
    private Integer status;

    @Schema(description = "品牌Id" )
    private Long brandId;

    @Schema(description = "配送方式json" )
    private String deliveryMode;

    @Schema(description = "运费模板id（0：统一包邮  -1：统一运费  其他：运费模板id）" )
    private Long deliveryTemplateId;

    @Schema(description = "统一运费的运费金额" )
    private Double deliveryAmount;

    @Schema(description = "录入时间" )
    private Date createTime;

    @Schema(description = "修改时间" )
    private Date updateTime;

    @Schema(description = "上架时间" )
    private Date putawayTime;

    @Schema(description = "商品类型(0普通商品 1拼团 2秒杀 3积分)" )
    private Integer prodType;

    @Schema(description = "商品积分价格" )
    private Integer scorePrice;

    @Schema(description = "活动id(对应prod_type)" )
    private Long activityId;


    @Schema(description = "员工Id")
    @TableField(exist = false)
    private Long employeeId;

    @Schema(description = "员工手机号")
    @TableField(exist = false)
    private String employeeMobile;

    @Schema(description = "活动价格" )
    @TableField(exist = false)
    private Double activityPrice;

    @Schema(description = "活动库存" )
    @TableField(exist = false)
    private Integer activityTotalStocks;

    @Schema(description = "预售状态 1：开启 0：未开启" )
    private Integer preSellStatus;

    @Schema(description = "预售发货时间" )
    private Date preSellTime;

    @Schema(description = "是否置顶，1.置顶 0.不置顶" )
    private Integer isTop;

    @Schema(description = "排序号" )
    private Integer seq;

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

    @Version
    @Schema(description = "版本" )
    private Integer version;

    @Schema(description = "使用语言 0中文 1中英文" )
    private Integer useLang;

    @TableField(exist = false)
    @Schema(description = "事件状态" )
    private Integer eventStatus;

    @TableField(exist = false)
    @Schema(description = "语言列表信息" )
    private List<ProdLang> prodLangList;

    @TableField(exist = false)
    @Schema(description = "品牌" )
    private Brand brand;

    @TableField(exist = false)
    @Schema(description = "sku列表" )
    private List<Sku> skuList;

    @TableField(exist = false)
    @Schema(description = "店铺名称" )
    private String shopName;


    @TableField(exist = false)
    @Schema(description = "语言" )
    private Integer lang;

    @TableField(exist = false)
    @Schema(description = "商品参数列表" )
    private List<ProdParameter> prodParameterList;

    @TableField(exist = false)
    @Schema(description = "已经销售数量" )
    private Integer soldNum;

    @TableField(exist = false)
    @Schema(description = "注水销量" )
    private Integer waterSoldNum;

    @TableField(exist = false)
    @Schema(description = "库存量(可售卖库存)" )
    private Integer totalStocks;

    @TableField(exist = false)
    @Schema(description = "商品中文名称" )
    private String prodNameCn;

    @TableField(exist = false)
    @Schema(description = "商品英文名称" )
    private String prodNameEn;

    @TableField(exist = false)
    @Schema(description = "分类信息" )
    private CategoryVO categoryVO;

    @TableField(exist = false)
    @Schema(description = "店铺分类信息" )
    private CategoryVO shopCategoryVO;

    @TableField(exist = false)
    @Schema(description = "平台佣金扣率")
    private Double rate;


    @Schema(description = "是否为全部实物商品的组合商品 1.是 0.否" )
    private Integer isAllEntity;

    @Data
    public static class DeliveryModeVO {
        @Schema(description = "用户自提" , requiredMode = Schema.RequiredMode.REQUIRED)
        private Boolean hasUserPickUp;

        @Schema(description = "店铺配送" , requiredMode = Schema.RequiredMode.REQUIRED)
        private Boolean hasShopDelivery;

        @Schema(description = "同城配送" , requiredMode = Schema.RequiredMode.REQUIRED)
        private Boolean hasCityDelivery;
    }
}
