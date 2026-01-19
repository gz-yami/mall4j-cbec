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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.shop.bean.bo.BrandBO;
import com.yami.shop.bean.dto.BrandShopDTO;
import com.yami.shop.bean.model.Brand;
import com.yami.shop.bean.vo.search.BrandSearchVO;
import com.yami.shop.common.util.PageAdapter;
import com.yami.shop.common.util.PageParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author yami
 */
public interface BrandMapper extends BaseMapper<Brand> {

    /**
     * 获取品牌信息列表
     *
     * @param page 分页信息
     * @param brand 品牌信息
     * @return 品牌信息列表
     */
    List<Brand> list(@Param("page") PageAdapter page, @Param("brand") Brand brand);

    /**
     * 获取品牌数量
     *
     * @param brand 品牌信息
     * @return 品牌信息列表
     */
    Long listTotal(@Param("brand") Brand brand);

    /**
     * 根据品牌信息id获取品牌信息
     *
     * @param brandId 品牌信息id
     * @return 品牌信息
     */
    Brand getByBrandId(@Param("brandId") Long brandId);

    /**
     * 获取品牌在商品中使用的数量
     *
     * @param brandId
     * @return 使用该品牌的商品数量
     */
    int getUseNum(@Param("brandId") Long brandId);

    /**
     * 更新品牌状态（启用或禁用）
     *
     * @param brand
     */
    void updateBrandStatus(@Param("brand") Brand brand);

    /**
     * 根据分类id，获取品牌列表(分类中的推荐品牌)
     * @param categoryId
     * @param name
     * @return
     */
    List<Brand> listByCategoryIdAndName(@Param("categoryId") Long categoryId, @Param("name") String name);

    /**
     * 批量插入品牌列表
     * @param brandShopList
     */
    void insertBatchByBrandShopList(@Param("brandList") List<BrandShopDTO> brandShopList);

    /**
     * 获取品牌列表
     * @param brand
     * @return
     */
    List<Brand> listByParams(@Param("brand") Brand brand);

    /**
     * 获取分类及语言信息
     *
     * @param brandIds
     * @return
     */
    List<BrandBO> listAndLangBO(@Param("brandIds") List<Long> brandIds);

    /**
     * 根据店铺id与品牌名称,分类id获取已经签约的品牌列表
     * @param page
     * @param categoryId
     * @param brandName
     * @param shopId
     * @return
     */
    IPage<Brand> pageSigningByShopIdAndBrandNameAndCategoryId(@Param("page") PageParam<Brand> page, @Param("categoryId") Long categoryId, @Param("brandName") String brandName, @Param("shopId") Long shopId);

    /**
     * 根据店铺id更新该店铺下的品牌的店铺id和品牌状态
     * @param oldShopId
     * @param status
     * @param newShopId
     */
    void updateShopIdAndStatusByShopId(@Param("oldShopId") Long oldShopId, @Param("status") Integer status, @Param("newShopId") Long newShopId);

    /**
     * 获取指定品牌列表
     * @param brandIds
     * @return
     */
    List<Brand> listByBrandIds(@Param("brandIds") Set<Long> brandIds);

    /**
     * 获取es数据形式的品牌集合
     * @param lang 语言
     * @param brandIds 品牌id集合
     * @return es数据形式的品牌集合
     */
    List<BrandSearchVO> listEsBrandByBrandIds(@Param("lang") Integer lang,
                                              @Param("brandIds") List<Long> brandIds);
}
