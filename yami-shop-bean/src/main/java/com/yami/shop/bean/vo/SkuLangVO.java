package com.yami.shop.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author gaozijie
 * @since 2024-01-23
 */
@Data
public class SkuLangVO {

    @Schema(description = "skuId")
    private Long skuId;

    @Schema(description = "语言类型(0:中, 1:英)")
    private Integer lang;

    @Schema(description = "属性")
    private String properties;

    @Schema(description = "sku名称")
    private String skuName;

    @Schema(description = "商品名称")
    private String prodName;
}
