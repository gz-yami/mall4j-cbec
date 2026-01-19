package com.yami.shop.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: zsm
 * @date: 2023/3/10 14:44
 */
@Data
public class ShopCityStatusVO {
    @Schema(description = "店铺id" )
    private Long shopId;

    @Schema(description = "同城配送可用状态 : 1 可用 -1 不在范围内 -2 商家没有配置同城配送信息 -3 起送费不够" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer shopCityStatus;
}
