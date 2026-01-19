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

import com.yami.shop.bean.model.CategoryLang;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Yami
 */
@Data
public class DerivativeCategoryDto implements Serializable {

    @Schema(description = "类目ID" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;

    @Schema(description = "店铺id" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shopId;

    @Schema(description = "父节点" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long parentId;

    @Schema(description = "产品类目名称" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoryName;

    @Schema(description = "类目图标" )
    private String icon;

    @Schema(description = "类目的显示图片" )
    private String pic;

    @Schema(description = "排序" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer seq;

    @Schema(description = "分类扣率" )
    private Double deductionRate;

    @Schema(description = "默认是1，表示正常状态,0为下线状态" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "记录时间" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Date recTime;

    @Schema(description = "分类层级" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer grade;

    @Schema(description = "更新时间" )
    private Date updateTime;

    @Schema(description = "分类国际化列表")
    private List<CategoryLang> categoryLangList;

}
