package com.yami.shop.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author gaozijie
 * @since 2024-01-23
 */
@Data
public class ProdLangVO {

    @Schema(description = "商品id")
    private Long prodId;

    @Schema(description = "语言(0:中, 1:英)")
    private Integer lang;

    @Schema(description = "商品名称")
    private String prodName;

    @Schema(description = "简介")
    private String brief;

    @Schema(description = "内容")
    private String content;
}
