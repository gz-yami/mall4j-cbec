/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Yami
 */
@Data
@TableName("tz_area")
public class Area implements Serializable {
    private static final long serialVersionUID = -6013320537436191451L;

    @TableId
    @Schema(description = "地区id" )
    private Long areaId;

    @Schema(description = "地区名称" )
    private String areaName;

    @Schema(description = "地区拼音名称")
    private String areaNamePinyin;

    @Schema(description = "地区名称英文")
    @NotBlank(message = "地区名称不能为空")
    private String areaNameEn;

    @Schema(description = "地区上级id" )
    @NotNull(message = "地区上级id不能为空")
    private Long parentId;
    /**
     * @see com.yami.shop.bean.enums.AreaLevelEnum
     */
    @Schema(description = "地区层级" )
    @NotNull(message = "地区层级不能为空")
    private Integer level;

    @TableField(exist=false)
    @Schema(description = "下级地址集合" )
    private List<Area> areas;

    @TableField(exist=false)
    @Schema(description = "最大级别" )
    private Integer maxGrade;

    @TableField(exist=false)
    @Schema(description = "最小级别" )
    private Integer minGrade;
}
