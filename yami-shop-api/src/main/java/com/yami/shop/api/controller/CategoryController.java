/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.api.controller;

import com.yami.shop.bean.app.dto.CategoryAndBrandInfoDto;
import com.yami.shop.bean.app.dto.CategoryDto;
import com.yami.shop.bean.model.Brand;
import com.yami.shop.bean.model.Category;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.service.BrandService;
import com.yami.shop.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LGH
 */
@RestController
@RequestMapping("/category")
@Tag(name = "分类接口")
@AllArgsConstructor
public class CategoryController {

    private final BrandService brandService;

    private CategoryService categoryService;



    @GetMapping("/categoryInfo")
    @Operation(summary = "分类信息列表" , description = "获取所有的产品分类信息，顶级分类的parentId为0,默认为顶级分类")
    @Parameters({
            @Parameter(name = "parentId", description = "分类ID" ),
            @Parameter(name = "shopId", description = "店铺id" )
    })
    public ServerResponseEntity<CategoryAndBrandInfoDto> categoryInfo(@RequestParam(value = "parentId", defaultValue = "0") Long parentId) {
        CategoryAndBrandInfoDto categoryAndBrandInfoDto = new CategoryAndBrandInfoDto();
        List<Category> categories = categoryService.listCategoryAndChildCategory(parentId, Constant.PLATFORM_SHOP_ID);
        List<CategoryDto> categoryDtos = BeanUtil.mapAsList(categories, CategoryDto.class);
        List<Brand> brands = brandService.listByCategoryIdAndName(parentId, null);
        categoryAndBrandInfoDto.setCategoryInfo(categoryDtos);
        categoryAndBrandInfoDto.setBrandInfo(brands);
        return ServerResponseEntity.success(categoryAndBrandInfoDto);
    }
}
