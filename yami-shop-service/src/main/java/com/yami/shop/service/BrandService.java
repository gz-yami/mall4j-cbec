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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.dto.BrandShopDTO;
import com.yami.shop.bean.model.Brand;
import com.yami.shop.bean.vo.search.BrandSearchVO;
import com.yami.shop.common.util.PageParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 *
 * @author lgh
 * @date 2018/07/05
 */
public interface BrandService extends IService<Brand> {

    /**
     * 分页获取品牌信息列表
     * @param page 分页参数
     * @param brand
     * @return 品牌信息列表分页数据
     */
    IPage<Brand> page(PageParam<Brand> page, Brand brand);

    /**
     * 根据品牌信息id获取品牌信息
     *
     * @param brandId 品牌信息id
     * @return 品牌信息
     */
    Brand getByBrandId(Long brandId);

    /**
     * 根据品牌信息id获取品牌信息
     *
     * @param brandId 品牌信息id
     * @return 品牌信息
     */
    Brand getInfo(Long brandId);

    /**
     * 保存品牌信息
     * @param brand 品牌信息
     */
    void saveBrand(Brand brand);

    /**
     * 更新品牌信息
     * @param brandDTO 品牌信息
     * @return
     */
    List<Long> updateBrand(Brand brandDTO);

    /**
     * 根据品牌id，删除品牌
     * @param brandId
     * @return
     */
    List<Long> deleteById(Long brandId);

    /**
     * 更新品牌状态（启用或禁用）
     * @param brand
     * @return
     */
    List<Long> updateBrandStatus(Brand brand);

//    /**
//     * 根据分类id，获取品牌列表
//     * @param categoryId 分类id
//     * @param lang 语言
//     * @return 品牌id
//     */
//    List<Brand> listByCategory(Long categoryId, Integer lang);

//    /**
//     * 清楚分类缓存
//     * @param categoryIds
//     */
//    void removeCache(List<Long> categoryIds);

    /**
     * 批量插入品牌列表
     * @param brandShopList
     */
    void insertBatchByBrandShopList(List<BrandShopDTO> brandShopList);

    /**
     * 获取品牌列表
     * @param brand
     * @return
     */
    List<Brand> listByParams(Brand brand);

    /**
     * 根据分类id与品牌名称获取分类下的品牌与店铺签约的品牌
     * @param page
     * @param categoryId
     * @param brandName
     * @param shopId
     * @return
     */
    IPage<Brand> pageAvailableBrandByCategoryIdAndBrandNameAndShopId(PageParam<Brand> page, Long categoryId, String brandName, Long shopId);

    /**
     * 根据店铺id更新该店铺下的品牌的店铺id和品牌状态
     * @param oldShopId
     * @param status
     * @param newShopId
     */
    void updateShopIdAndStatusByShopId(Long oldShopId, Integer status, Long newShopId);
    /**
     * 根据分类id及品牌名称，获取品牌列表
     * @param categoryId
     * @param brandName
     * @return
     */
    List<Brand> listByCategoryIdAndName(Long categoryId, String brandName);

    /**
     * 获取指定品牌信息
     * @param brandIds
     * @return
     */
    List<Brand> listByBrandIds(Set<Long> brandIds);

    /**
     * 获取es数据形式的品牌集合
     * @param brandIds 品牌id集合
     * @return es数据形式的品牌集合
     */
    List<BrandSearchVO> listEsBrandByBrandIds(List<Long> brandIds);

}
