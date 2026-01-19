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

import com.yami.shop.bean.model.CategoryLang;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author lth
 * @Date 2021/4/26 9:28
 */
@Data
public class CategoryShopVO {

    @Schema(description = "主键id" )
    private Long categoryShopId;

    @Schema(description = "分类id" )
    private Long categoryId;

    @Schema(description = "分类名称" )
    private String name;

    @Schema(description = "上级分类id" )
    private Long parentId;

    @Schema(description = "上级分类名称" )
    private String parentName;

    @Schema(description = "一级分类id" )
    private Long firstLevelCategoryId;

    @Schema(description = "平台扣率" )
    private Double platformRate;

    @Schema(description = "自定义扣率，为空代表采用平台扣率" )
    private Double customizeRate;

    @Schema(description = "经营资质" )
    private String qualifications;

    @Schema(description = "签约状态：1：已通过 0待审核 -1未通过" )
    private Integer status;

    @Schema(description = "分类状态 1:上架 0:下架 -1:已删除" )
    private Integer categoryStatus;

    @Schema(description = "分类语言列表" )
    private List<CategoryLang> categoryLangList;

    @Schema(description = "父级分类语言列表" )
    private List<CategoryLang> parentCategoryLangList;

}
