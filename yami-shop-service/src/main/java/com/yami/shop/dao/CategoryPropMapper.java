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
import com.yami.shop.bean.model.CategoryProp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yami
 */
public interface CategoryPropMapper extends BaseMapper<CategoryProp> {

    /**
     * 批量插入分类商品关联信息
     * @param categoryId 分类ID
     * @param propIds 商品ID列表
     */
    void insertCategoryProp(@Param("categoryId") Long categoryId, @Param("propIds") List<Long> propIds);

    /**
     * 根据分类ID删除分类商品关联信息
     * @param categoryId 分类ID
     */
    void deleteByCategoryId(Long categoryId);

    /**
     * 根据商品ID删除分类商品关联信息
     * @param propId 商品ID
     */
    void deleteByPropId(Long propId);
}
