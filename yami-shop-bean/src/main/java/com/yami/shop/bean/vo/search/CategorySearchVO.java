/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.vo.search;

import com.yami.shop.bean.bo.CategoryLangBO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分类信息VO
 * @author Yami
 */
@Data
public class CategorySearchVO{
    private static final long serialVersionUID = 1L;

    @Schema(description = "分类id" )
    private Long categoryId;

    @Schema(description = "分类名称" )
    private String name;

    /**
     * 分类名称列表
     */
    private List<CategoryLangBO> categoryLangList;
}
