/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Yami
 */
@Data
public class AreaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "地区id" )
    private Long areaId;

    @Schema(description = "地区名称" )
    private String areaName;

    @Schema(description = "英文地区名称" )
    private String areaNameEn;

    @Schema(description = "地区上级id" )
    private Long parentId;

    @Schema(description = "地区层级" )
    private Integer level;

    @Schema(description = "是否选择" )
    private Integer check;

    @Schema(description = "下级地址集合" )
    private List<AreaDto> areas;

    @Schema(description = "下级地址的areaId" )
    private List<Long> areaIds;
}
