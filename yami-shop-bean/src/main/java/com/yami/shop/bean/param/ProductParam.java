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

import com.yami.shop.bean.dto.SkuAdminDTO;
import com.yami.shop.bean.model.ProdParameter;
import com.yami.shop.bean.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 商品参数
 *
 * @author LGH
 */
@Data
@Schema(description = "商品参数")
public class ProductParam {

    @Schema(description = "产品id")
    private Long prodId;

    @Schema(description = "店铺id")
    private Long shopId;

    @Schema(description = "用户id")
    private String userId;

    @Schema(description = "商品状态（-1:删除、0:商家下架、1:上架、2:违规下架、3:平台审核）")
    private Integer status;

    @Schema(description = "sku状态（0 禁用 1 启用）")
    private Integer skuStatus;

    @Schema(description = "预售状态 1：开启 0：未开启")
    private Integer preSellStatus;

    @Schema(description = "预售发货时间")
    private Date preSellTime;

    @Schema(description = "商品名称")
    private String prodName;

    @Schema(description = "商品卖点")
    private String brief;

    @Schema(description = "商品详情")
    private String content;

    @Schema(description = "商品类型(0普通商品 1拼团 2秒杀 3积分 4套餐 5活动)")
    private Integer prodType;

    @Schema(description = "非商品类型集合(0普通商品 1拼团 2秒杀 3积分 4套餐 5活动)")
    private List<Integer> notProdTypes;

    @NotNull(message = "请输入商品价格")
    @Schema(description = "商品价格")
    private Double price;

    @NotNull(message = "请输入商品原价")
    @Schema(description = "商品原价")
    private Double oriPrice;

    @Schema(description = "商品类别 0.实物商品 1. 虚拟商品 2.组合商品")
    private Integer mold;

    @Schema(description = "非商品类别 0.实物商品 1. 虚拟商品 2.组合商品")
    private Integer notMold;

    @Schema(description = "商品ids")
    private List<Long> prodIds;

    @Schema(description = "skuIds")
    private List<Long> skuIds;

    @NotNull(message = "请输入商品库存")
    @Schema(description = "库存量")
    private Integer totalStocks;

    @NotBlank(message = "请选择图片上传")
    @Schema(description = "商品图片")
    private String pic;

    @Schema(description = "商品视频")
    private String video;

    @NotBlank(message = "请选择图片上传")
    @Schema(description = "商品图片")
    private String imgs;

    @NotNull(message = "请选择商品分类")
    @Schema(description = "商品分类")
    private Long categoryId;

//    @NotNull(message = "请选择本店商品分类")
    @Schema(description = "本店商品分类")
    private Long shopCategoryId;

    @Schema(description = "品牌Id")
    private Long brandId;

    @Schema(description = "员工Id")
    private Long employeeId;

    @Schema(description = "1：商品名称 2：商品编码")
    private Integer prodKeyType;

    @Schema(description = "搜索商品关键词(0:商品名称 1：商品编码)")
    private String prodKey;

    @Schema(description = "供货商id")
    private Long supplierId;

    @Schema(description = "sku列表字符串")
    private List<SkuAdminDTO> skuList;

    @Schema(description = "店铺名称")
    private String shopName;

    @Schema(description = "当前语言")
    private Integer lang;

    @Schema(description = "配送方式")
    private Product.DeliveryModeVO deliveryModeVo;

    @Schema(description = "运费模板id（0：统一包邮  -1：统一运费  其他：运费模板id）")
    private Long deliveryTemplateId;

    @Schema(description = "统一运费的运费金额")
    private Double deliveryAmount;

    @Schema(description = "是否筛掉分销商品 1是 0否")
    private Integer isDistribution;

    @Schema(description = "是否置顶，1.置顶 0.不置顶")
    private Integer isTop;

    @Schema(description = "积分价格")
    private Integer scorePrice;

    @Schema(description = "排序")
    private Integer seq;

    @Schema(description = "配送方式 0用户自提, 1店铺配送, 2同城配送")
    private Integer deliveryMode;

    @Schema(description = "虚拟商品的留言备注")
    private String virtualRemark;

    @Schema(description = "核销次数类型 -1.多次核销 0.无需核销 1.单次核销")
    @Min(value = -1, message = "只能为-1,0或1")
    @Max(value = 1, message = "只能为-1,0或1")
    private String writeOffNum;

    @Schema(description = "多次核销次数 -1.无限次")
    private Integer writeOffMultipleCount;

    @Schema(description = "核销有效期 -1.长期有效 0.自定义  x.x天内有效")
    @Max(value = 9999, message = "核销有效期应该小于{max}天")
    private Integer writeOffTime;

    @Schema(description = "核销开始时间")
    private Date writeOffStart;

    @Schema(description = "核销结束时间")
    private Date writeOffEnd;

    @Schema(description = "是否可以退款 1.可以 0不可以")
    @Min(value = 0, message = "只能为0或1")
    @Max(value = 1, message = "只能为0或1")
    private Integer isRefund;

    @Schema(description = "分销业绩-推广效果：0无 1.排序号排序")
    private Integer sortParam;

    @Schema(description = "排序类型 0无 1 正序 2倒序")
    private Integer sortType;

    @Schema(description = "商品编码")
    private String partyCode;

    @Schema(description = "员工手机号")
    private String employeeMobile;

    @Schema(description = "商品参数列表")
    private List<ProdParameter> prodParameterList;

    @Schema(description = "使用语言 0中文 1中英文")
    private Integer useLang;

    @Schema(description = "默认语言")
    private Integer defaultLang;

    @Schema(description = "是否筛选掉活动商品(0:否, 1:是)")
    private Integer isActive;

    @Schema(description = "活动id")
    private Long groupActivityId;

    @Schema(description = "商品语言列表")
    private List<ProdLangParam> prodLangList;

    @Schema(description = "库存预警 1开启 2关闭")
    private Integer isStockWarning;


    @Schema(description = "是否为全部实物商品的组合商品 1.是 0.否" )
    private Integer isAllEntity;
}
