/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.app.param;

import com.yami.shop.bean.app.dto.DvyTypeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author Yami
 */
@Data
@Schema(description = "订单参数")
public class OrderParam {

    @Schema(description = "立即购买时提交的商品项" )
    private OrderItemParam orderItem;

    @Schema(description = "地址ID，0为默认地址" , requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "地址不能为空")
    private Long addrId;

    @Schema(description = "用户是否改变了优惠券的选择，如果用户改变了优惠券的选择，则完全根据传入参数进行优惠券的选择" )
    private Integer userChangeCoupon;

    @Schema(description = "优惠券id数组" )
    private List<Long> couponIds;

    @Schema(description = "用户优惠券id数组" )
    private List<Long> couponUserIds;

    @Schema(description = "用户是否选择积分抵现(0不使用 1使用 默认不使用)" )
    private Integer isScorePay;

    @Schema(description = "多店铺的商品配送方式", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<DvyTypeDTO> dvyTypes;

    @Schema(description = "用户是否自己选择使用多少积分，为空则为默认全部使用" )
    private Long userUseScore;

    @Schema(description = "是否预售订单 1：是 0：不是" )
    private Integer preSellStatus;

}
