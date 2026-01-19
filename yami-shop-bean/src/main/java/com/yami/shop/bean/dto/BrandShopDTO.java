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
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 品牌店铺关联信息DTO
 *
 * @author FrozenWatermelon
 * @date 2021-05-08 13:31:45
 */
@Data
public class BrandShopDTO {

    @Schema(description = "主键id" )
    private Long brandShopId;

    @Schema(description = "店铺id" )
    private Long shopId;

    @Schema(description = "品牌id" )
    private Long brandId;

    @Schema(description = "授权资质图片，以,分割" )
    @NotBlank(message = "授权资质图片不能为空")
    private String qualifications;

    @Schema(description = "类型 0：平台品牌，1：店铺自定义品牌" )
    private Integer type;

    @Schema(description = "检索首字母" )
    @Length(max = 1, message = "检索首字母长度不能超过1")
    private String firstLetter;

    @Schema(description = "排序" )
    private Integer seq;

    @Schema(description = "是否置顶 0：不置顶  1：置顶" )
    private Integer isTop;

    @Schema(description = "logo" )
    private String imgUrl;

    @Schema(description = "品牌名称" )
    private String name;

    @Schema(description = "品牌描述" )
    private String desc;

    @Schema(description = "品牌状态" )
    private Integer status;

    @Schema(description = "语言" )
    private Integer lang;

}
