/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yami.shop.bean.model.Category;
import com.yami.shop.bean.model.CategoryBrand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yami
 */
public interface CategoryBrandMapper extends BaseMapper<CategoryBrand> {

    /**
     * 批量插入分类品牌关联信息
     * @param categoryId 分类ID
     * @param brandIds 品牌ID列表
     */
    void insertCategoryBrand(@Param("categoryId") Long categoryId, @Param("brandIds") List<Long> brandIds);

    /**
     * 根据分类ID删除分类品牌关联信息
     * @param categoryId 分类ID
     */
    void deleteByCategoryId(Long categoryId);

    /**
     * 根据品牌id获取关联的分类id
     *
     * @param brandId 品牌id
     * @return 分类id列表
     */
    List<Long> getCategoryIdsByBrandId(@Param("brandId") Long brandId);

    /**
     * 获取品牌分类信息
     * @param brandId 品牌id
     * @return 分类信息
     */
    List<Category> getCategoryByBrandId(@Param("brandId") Long brandId);
}
