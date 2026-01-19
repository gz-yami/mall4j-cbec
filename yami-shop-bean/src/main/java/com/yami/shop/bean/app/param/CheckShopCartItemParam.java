package com.yami.shop.bean.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author FrozenWatermelon
 * @date 2021-02-03 15:47:32
 */
@Data
public class CheckShopCartItemParam {

    @NotNull
    @Schema(description = "购物车ID" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long basketId;

    @NotNull
    @Schema(description = "商品是否勾选 1:勾选 0:未勾选" )
    private Integer isChecked;
}
