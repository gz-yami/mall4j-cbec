package com.yami.shop.bean.vo;

import com.yami.shop.bean.app.dto.ShopCartDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author 菠萝凤梨
 */
@Data
@Schema(description = "购物车合计")
public class ShopCartWithAmountVO {

    @Schema(description = "总额" )
    private Double totalMoney;

    @Schema(description = "总计" )
    private Double finalMoney;

    @Schema(description = "促销减额" )
    private Double subtractMoney;

    @Schema(description = "总减额：促销减额 + 店铺会员总减额" )
    private Double totalReduceMoney;

    @Schema(description = "商品数量" )
    private Integer count;

    @Schema(description = "运费" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Double freightAmount;

    @Schema(description = "等级免运费金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double freeTransFee;

    @Schema(description = "多个店铺的购物车信息" )
    private List<ShopCartDto> shopCarts;

    @Schema(description = "运费信息" )
    private UserDeliveryInfoVO userDeliveryInfo;

    @Schema(description = "店铺会员总减额")
    private Double memberReduceAmount;
}
