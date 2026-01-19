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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 父级分类信息VO
 *
 * @author FrozenWatermelon
 * @date 2020-10-28 15:27:24
 */
@Data
public class ParentCategoryVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "分类id" )
    private Long categoryId;

    @Schema(description = "父ID" )
    private Long parentId;

    @Schema(description = "分类名称" )
    private String categoryName;
}
