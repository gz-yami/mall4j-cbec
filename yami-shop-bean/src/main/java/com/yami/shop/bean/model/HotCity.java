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

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chiley
 */
@Data
@TableName("tz_hot_city")
public class HotCity implements Serializable {
    /**
     * 主键
     */
    @TableId
    @Schema(description = "热门国家/地区id")
    private Long hotCityId;

    @NotNull
    @Schema(description = "国家/地区id")
    private Long areaId;

    @Schema(description = "国家/地区名称")
    private String areaName;

    @Schema(description = "国家/地区英文名称")
    private String areaNameEn;

    @Schema(description = "顺序")
    private Integer seq;

    @Schema(description = "状态 0:禁用 1:启用")
    @NotNull
    private Integer status;

    @Schema(description = "创建时间")
    protected Date createTime;

    @Schema(description = "更新时间")
    protected Date updateTime;
}
