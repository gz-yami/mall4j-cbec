package com.yami.shop.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author 菠萝凤梨
 */
@Data
@Schema(description = "店铺运费信息")
public class ShopTransFeeVO {

    @Schema(description = "免运费金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double freeTransFee;

    @Schema(description = "运费" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double transFee;

    @Schema(description = "运费模板id" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long deliveryTemplateId;

}
