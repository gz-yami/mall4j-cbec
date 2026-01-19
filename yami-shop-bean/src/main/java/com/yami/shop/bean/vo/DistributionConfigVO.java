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

import com.yami.shop.bean.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author lhd
 * @date 2021-09-06 14:19:49
 */
@Data
public class DistributionConfigVO {

    @Schema(description = "分销开关： 0:关闭 1:开启" )
    private Integer distributionSwitch;

// 申请条件配置-------------------------------------------------------------

    @Schema(description = "申请条件-条件审核判定： 0:人工判定 1:系统判定 2.无需审核")
    private Integer autoCheck;

    @Schema(description = "申请条件-条件审核判定：购买指定商品，确认收货任意一件商品后计入" )
    private List<Long> prodIdList;

    @Schema(description = "商品列表" )
    private List<Product> productVOList;

    @Schema(description = "申请条件-条件审核判定：消费金额大于等于expenseAmount元，实付金额+积分抵扣+余额抵扣总金额，收货后计入(单位: 元)" )
    private Double expenseAmount;

    @Schema(description = "申请条件-条件审核判定：消费笔数大于等于expenseNumber次,下单次数，收货后计入" )
    private Integer expenseNumber;

    @Schema(description = "申请条件-申请所需信息：是否需要真实姓名 true 需要 false不需要" )
    private Boolean realName;

    @Schema(description = "申请条件-申请所需信息：是否需要身份证号码 true 需要 false不需要" )
    private Boolean identityCardPic;

    @Schema(description = "申请条件-申请所需信息：是否需要身份证照片 true 需要 false不需要" )
    private Boolean identityCardNumber;

    @Schema(description = "提现发放方式： 0.无需审核直接发放，1.审核后系统发放，2.审核后人工发放" )
    private Integer withdrawal;

// 提现申请配置-------------------------------------------------------------

    @Schema(description = "提现频率(天)" )
    private Integer frequency;

    @Schema(description = "单笔提现最高（元）" )
    private Double amountMax;

    @Schema(description = "单笔提现最低 （元）" )
    private Double amountMin;

    @Schema(description = "打款说明" )
    private String paymentExplain;

    @Schema(description = "提现次数" )
    private Integer number;
}
