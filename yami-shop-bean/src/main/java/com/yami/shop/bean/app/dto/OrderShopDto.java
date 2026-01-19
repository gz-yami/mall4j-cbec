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

import com.yami.shop.bean.dto.UserAddrOrderDto;
import com.yami.shop.bean.model.OrderVirtualInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 订单下的每个店铺
 *
 * @author YaMi
 */
@Data
public class OrderShopDto implements Serializable {


    @Schema(description = "订单id" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;

    @Schema(description = "订单编号" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderNumber;
    /**
     * 店铺ID
     **/
    @Schema(description = "店铺id" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shopId;


    /**
     * 店铺名称
     **/
    @Schema(description = "店铺名称" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String shopName;

    @Schema(description = "实际总值" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double actualTotal;

    @Schema(description = "商品总值" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double total;

    @Schema(description = "商品总数" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalNum;

    @Schema(description = "地址Dto" , requiredMode = Schema.RequiredMode.REQUIRED)
    private UserAddrOrderDto userAddrDto;

    @Schema(description = "支付方式" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer payType;

    @Schema(description = "产品信息" , requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OrderItemDto> orderItemDtos;

    @Schema(description = "运费" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double transfee;

    @Schema(description = "优惠总额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double reduceAmount;

    @Schema(description = "促销活动优惠金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double discountMoney;

    @Schema(description = "店铺优惠金额" )
    private Double shopAmount;

    @Schema(description = "店铺优惠券优惠金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double shopCouponMoney;
    @Schema(description = "积分抵扣金额" )
    private Double scoreAmount;

    @Schema(description = "会员折扣金额" )
    private Double memberAmount;

    @Schema(description = "店铺套餐优惠金额" )
    private Double shopComboAmount;

    @Schema(description = "会员运费减免金额" )
    private Double platformFreeFreightAmount;

    @Schema(description = "店铺改价优惠金额" )
    private Double shopChangeFreeAmount;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "订单创建时间" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "订单付款时间" )
    private Date payTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "订单发货时间" )
    private Date dvyTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "订单完成时间" )
    private Date finallyTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "订单取消时间" )
    private Date cancelTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "订单更新时间" )
    private Date updateTime;

    /**
     * 预售时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "预售时间" )
    private Date preSaleTime;

    /**
     * 订单备注信息
     */
    @Schema(description = "订单备注信息" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String remarks;

    /**
     * 配送类型
     */
    @Schema(description = "配送类型 1:快递 2:自提 3：无需快递" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer dvyType;

    /**
     * 订单类型1团购订单 2秒杀订单
     */
    @Schema(description = "订单类型（1团购订单 2秒杀订单）" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer orderType;

    /**
     * 订单状态
     */
    @Schema(description = "订单状态" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "能否退款" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean canRefund = false;

    @Schema(description = "能否整单退款" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean canAllRefund = false;

    @Schema(description = "当前可退款金额" )
    private Double canRefundAmount;

    @Schema(description = "订单积分" )
    private Long orderScore = 0L;

    @Schema(description = "秒杀id" )
    private Long seckillId;

    @Schema(description = "店铺免运费金额" )
    private Double freeTransfee;

    @Schema(description = "订单发票id" )
    private Long orderInvoiceId;

    @Schema(description = "订单退款状态（1:申请退款 2:退款成功 3:部分退款成功 4:退款失败）" )
    private Integer refundStatus;

    @Schema(description = "订单类别 0.实物商品订单 1. 虚拟商品订单" )
    private Integer orderMold;

    @Schema(description = "订单留言信息" )
    private String virtualRemark;

    @Schema(description = "订单卡券信息" )
    private List<OrderVirtualInfo> virtualInfoList;

    @Schema(description = "核销次数类型 -1.多次核销 0.无需核销 1.单次核销" )
    private Integer writeOffNum;

    @Schema(description = "多次核销次数 -1.无限次" )
    private Integer writeOffMultipleCount;

    @Schema(description = "核销开始时间" )
    private Date writeOffStart;

    @Schema(description = "核销结束时间" )
    private Date writeOffEnd;

    @Schema(description = "是否可以退款 1.可以 0不可以" )
    private Integer isRefund;

    @Schema(description = "店铺会员折扣金额" )
    private Double shopMemberAmount;
}
