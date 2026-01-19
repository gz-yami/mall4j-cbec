package com.yami.shop.bean.vo;

import com.yami.shop.bean.app.dto.DvyTypeDTO;
import com.yami.shop.bean.dto.TransportDto;
import com.yami.shop.bean.model.UserAddr;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author 菠萝凤梨
 */
@Data
@Schema(description = "运费信息")
public class UserDeliveryInfoVO {

    @Schema(description = "地址ID，0为默认地址")
    private Long addrId;

    @Schema(description = "多店铺的商品配送方式")
    private List<DvyTypeDTO> dvyTypes;

    @Schema(description = "用户选择的自提点id")
    private Long stationId;

    @Schema(description = "用户地址信息" )
    private UserAddr userAddr;

    @Schema(description = "免运费金额" )
    private Double totalFreeTransFee;

    @Schema(description = "总运费" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double totalTransFee;

    @Schema(description = "同城配送可用状态 : 1 可用 -1 不在范围内 -2 商家没有配置同城配送信息 -3 起送费不够" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer shopCityStatus;

    @Schema(description = "同城配送起送费" )
    private Double startDeliveryFee;

    @Schema(description = "秒杀活动id")
    private Long seckillId;

    @Schema(description = "同城配送可用状态 : 1 可用 -1 不在范围内 -2 商家没有配置同城配送信息 -3 起送费不够" , requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ShopCityStatusVO> shopCityStatusVoS;

    @Schema(description = "多店铺的同城配送起送费" )
    private Map<Long, Double> shopStartDeliveryFees;

    /**
     * 商品物流模板 - 判断是否可配送时获取的数据，保存到当前对象中，用于运费计算（不用重复查询模板了）
     */
    Map<Long, TransportDto> transportMap;


    private Map<Long, ShopTransFeeVO> shopIdWithShopTransFee;


    /**
     * 店铺仓库集合
     */
    private Map<Long, List<WarehouseVO>> warehouseMap;
}
