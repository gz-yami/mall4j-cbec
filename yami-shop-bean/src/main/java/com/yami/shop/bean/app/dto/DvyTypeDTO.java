package com.yami.shop.bean.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: zsm
 * @date: 2023/3/7 15:08
 */
@Data
public class DvyTypeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    public DvyTypeDTO() {
    }

    public DvyTypeDTO(Long shopId, Integer dvyType) {
        this.shopId = shopId;
        this.dvyType = dvyType;
    }

    @NotNull(message = "店铺id不能为空")
    private Long shopId;

    @Schema(description = "用户选择的自提点id")
    private Long stationId;
    @NotNull(message = "配送方式不能为空")
    @Schema(description = "配送类型 1:快递 2:自提 3：无需快递 4:同城配送" )
    private Integer dvyType;

    @Schema(description = "纬度 - 自提参数")
    private Double lat;

    @Schema(description = "经度 - 自提参数")
    private Double lng;
}
