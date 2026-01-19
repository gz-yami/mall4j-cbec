/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.vo;

import com.yami.shop.bean.dto.AreaDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * VO
 *
 * @author FrozenWatermelon
 * @date 2023-11-08 11:11:44
 */
@Data
public class WarehouseVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "仓库id")
    private Long warehouseId;

    @Schema(description = "店铺id")
    private Long shopId;

    @Schema(description = "仓库名称")
    private String warehouseName;

    @Schema(description = "仓库类型（0默认仓库，1区域仓库）")
    private Integer type;

    @Schema(description = "仓库地址")
    private String address;

    @Schema(description = "管理人")
    private String manage;

    @Schema(description = "管理电话")
    private String phone;

    /**
     * 系统类型见SysTypeEnum 0.普通用户系统 1.商家端 2平台端 3供应商端
     */
    @Schema(description = "系统类型")
    private Integer sysType;

    @Schema(description = "供应城市项")
    private List<AreaDto> cityList;

    @Schema(description = "供应地址id")
    private List<Long> areaIds;

    @Schema(description = "库存")
    private Integer stock;


    @Schema(description = "省ID")
    private Long provinceId;

    @Schema(description = "省")
    private String province;

    @Schema(description = "城市ID")
    private Long cityId;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区ID")
    private Long areaId;

    @Schema(description = "区")
    private String area;

    @Schema(description = "创建时间" )
    private Date createTime;

    @Schema(description = "更新时间" )
    private Date updateTime;

    @Schema(description = "库存模式 1共享总部库存 2独立销售库存")
    private Integer stockMode;

    @Schema(description = "状态 -1.已删除 1.正常状态")
    private Integer status;
}
