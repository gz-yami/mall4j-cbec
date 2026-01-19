/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.app.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import com.yami.shop.bean.model.Brand;
import com.yami.shop.bean.model.ProdLang;
import com.yami.shop.bean.model.ProdParameter;
import com.yami.shop.bean.model.Product;
import com.yami.shop.bean.param.LiveRoomParam;
import com.yami.shop.bean.vo.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Yami
 */
@Data
@Schema(description = "商品")
public class ProductVO {

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

    @Schema(description = "0:下架、1:上架" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

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

    @Schema(description = "sku列表" )
    private List<SkuVO> skuList;

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

    @Schema(description = "商品直播间列表" , requiredMode = Schema.RequiredMode.REQUIRED)
    private List<LiveRoomParam> liveRoomParams;

    @Schema(description = "虚拟商品的留言备注" )
    private String virtualRemark;

    @Schema(description = "运费信息" )
    private UserDeliveryInfoVO userDeliveryInfo;

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

    @Schema(description = "商品参数列表" )
    private List<ProdParameter> prodParameterList;


    @Schema(description = "运费模板id（0：统一包邮  -1：统一运费  其他：运费模板id）" )
    private Long deliveryTemplateId;

    @Schema(description = "是否在配送范围内" )
    private Boolean isDelivery;

    @Schema(description = "店铺会员等级")
    private Integer level;

    @Schema(description = "最优惠的会店铺员等级")
    private Integer mostLevel;

    @Schema(description = "最优惠的平台会员等级")
    private Integer mostPlatformLevel;

    @Schema(description = "统一运费的运费金额" )
    private Double deliveryAmount;

    @Schema(description = "商品多语言")
    private List<ProdLangVO> prodLangVOList;

    @Schema(description = "员工Id")
    private Long employeeId;

    @Schema(description = "员工手机号")
    private String employeeMobile;

    @Schema(description = "录入时间" )
    private Date createTime;

    @Schema(description = "修改时间" )
    private Date updateTime;

    @Schema(description = "上架时间" )
    private Date putawayTime;

    @Schema(description = "活动库存" )

    private Integer activityTotalStocks;

    @Schema(description = "是否置顶，1.置顶 0.不置顶" )
    private Integer isTop;

    @Schema(description = "排序号" )
    private Integer seq;

    @Schema(description = "所选的货币金额" )
    private Double selCurrencyPrice;

    @Version
    @Schema(description = "版本" )
    private Integer version;

    @Schema(description = "使用语言 0中文 1中英文" )
    private Integer useLang;


    @Schema(description = "事件状态" )
    private Integer eventStatus;


    @Schema(description = "语言列表信息" )
    private List<ProdLang> prodLangList;


    @Schema(description = "品牌" )
    private Brand brand;


    @Schema(description = "语言" )
    private Integer lang;


    @Schema(description = "商品中文名称" )
    private String prodNameCn;


    @Schema(description = "商品英文名称" )
    private String prodNameEn;


    @Schema(description = "分类信息" )
    private CategoryVO categoryVO;


    @Schema(description = "店铺分类信息" )
    private CategoryVO shopCategoryVO;

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
