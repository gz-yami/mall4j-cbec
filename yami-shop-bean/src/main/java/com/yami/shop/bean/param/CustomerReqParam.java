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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author Yami
 */
@Data
public class CustomerReqParam {

    @Schema(description = "时间类型 1:自然日 2:自然周 3:自然月 4:今日实时 5:近七天 6:近30天")
    private Integer dateType;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     * 时间
     */
    @Schema(description = "不传字段")
    private Date dateTime;

    @Schema(description = "店铺id")
    private Long shopId;

    /**
     * 第三方系统id 1：微信小程序
     */
    @Schema(description = "不传字段")
    private Integer appId;

    /**
     * 粉丝=付费会员
     */
    private Integer member;

    @Schema(description = "店铺名称")
    private String shopName;

    @Schema(description = "店铺id列表，勾选导出")
    private List<Long> shopIds;

    @Schema(description = "排序类型 0无 1 正序 2倒序")
    private Integer sortType;

    @Schema(description = "订单编号")
    private String orderNumber;

    @Schema(description = "退款编号")
    private String refundSn;

    @Schema(description = "收支类型")
    private Integer ioType;

    @Schema(description = "金额类型")
    private Integer amountType;

    @Schema(description = "资金变化原因")
    private Integer reason;

}
