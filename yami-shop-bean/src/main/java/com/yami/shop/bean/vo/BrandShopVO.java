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

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 品牌店铺关联信息VO
 *
 * @author FrozenWatermelon
 * @date 2021-05-08 13:31:45
 */
@Data
public class BrandShopVO {

    @Schema(description ="brandShopId")
    private Long  brandShopId;

    @Schema(description = "店铺id" )
    private Long shopId;

    @Schema(description = "品牌id" )
    private Long brandId;

    @Schema(description = "授权资质图片，以,分割" )
    private String qualifications;

    @Schema(description = "类型 0：平台品牌，1：店铺自定义品牌" )
    private Integer type;

    @Schema(description = "索引首字母" )
    private String firstLetter;

    @Schema(description = "logo" )
    private String imgUrl;

    @Schema(description = "品牌名称" )
    private String name;

    @Schema(description = "品牌状态" )
    private Integer brandStatus;

    @TableField(exist = false)
    @Schema(description = "平台分类" )
    private List<CategoryVO> categories;

}
