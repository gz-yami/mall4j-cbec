/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.model.Category;
import com.yami.shop.bean.model.CategoryBrand;

import java.util.List;

/**
 *
 *
 * @author lgh
 * @date 2018/07/13
 */
public interface CategoryBrandService extends IService<CategoryBrand> {

    /**
     * 根据品牌id或者关联的分类列表
     * @param brandId
     * @return
     */
    List<Long> getCategoryIdBrandId(Long brandId);

    /**
     * 获取品牌绑定的分类信息
     * @param brandId 品牌id
     * @return 分类列表
     */
    List<Category> getCategoryByBrandId(Long brandId);

    /**
     * 保存品牌信息
     * @param brandId
     * @param categoryIds
     */
    void saveByCategoryIds(Long brandId, List<Long> categoryIds);

    /**
     * 更新品牌信息
     * @param brandId
     * @param categoryIds
     */
    void updateByCategoryIds(Long brandId, List<Long> categoryIds);

    /**
     * 根据品牌id删除品牌分类关联信息
     * @param brandId
     */
    void deleteByBrandId(Long brandId);
}
