package com.yami.shop.bean.param;

import com.yami.shop.bean.app.dto.ShopCartDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 选择优惠券
 * @author FrozenWatermelon
 * @date 2020/12/17
 */
@Data
@AllArgsConstructor
public class ChooseCouponParam {

    @Schema(description = "用户是否改变了优惠券的选择，如果用户改变了优惠券的选择，则完全根据传入参数进行优惠券的选择" )
    private Integer userChangeCoupon;

    @Schema(description = "优惠券id数组" )
    private List<Long> couponUserIds;

    @Schema(description = "购物车信息" )
    List<ShopCartDto> shopCarts;

}
