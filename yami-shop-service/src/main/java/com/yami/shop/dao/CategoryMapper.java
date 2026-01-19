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
import com.yami.shop.bean.model.Category;
import com.yami.shop.bean.vo.search.CategorySearchVO;
import com.yami.shop.common.util.PageParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author yami
 */
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据父分类ID获取所有该父分类ID下的子分类列表
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> listByParentId(@Param("parentId") Long parentId);

    /**
     * 根据一级分类ID查找所有该分类下的所有三级分类列表
     *
     * @param parentId 一级分类ID
     * @return 三级分类列表
     */
    List<Category> listThreeByParentId(@Param("parentId") Long parentId);

    /**
     * 根据分类ID获取该分类的父分类ID
     *
     * @param parentId 分类ID
     * @return 父分类ID
     */
    Long getParentCategoryByParentId(@Param("parentId") Long parentId);

    /**
     * 根据分类查找条件参数查找分类列表
     *
     * @param category 分类查找条件参数
     * @return 分类列表
     */
    List<Category> categoryList(@Param("category") Category category);

    /**
     * 根据分类查找条件参数查找分类列表[语言]
     *
     * @param category 分类查找条件参数
     * @return 分类列表
     */
    List<Category> categoryLangList(@Param("category") Category category);

    /**
     * 分页（重载） - 根据分类查找条件参数查找分类列表[语言]
     *
     * @param pageParam
     * @param category
     * @return
     */
    IPage<Category> categoryLangList(PageParam<Category> pageParam, @Param("category") Category category);

    /**
     * 根据条件查找分类列表
     *
     * @param lang     当前选择语言
     * @param maxGrade 最大级别
     * @param parentId 父分类ID
     * @param status   状态
     * @param shopId   店铺ID
     * @return 分类列表
     */
    List<Category> listByLang(@Param("lang") Integer lang,
                              @Param("defaultLang") Integer defaultLang,
                              @Param("maxGrade") Integer maxGrade,
                              @Param("parentId") Long parentId,
                              @Param("status") Integer status,
                              @Param("shopId") Long shopId);

    /**
     * 根据分类ID与店铺ID获取分类信息
     *
     * @param categoryId 分类ID
     * @param shopId     店铺ID
     * @return 分类信息
     */
    Category getCategory(@Param("categoryId") Long categoryId, @Param("shopId") Long shopId);

    /**
     * 根据名称查找名称相同的分类数量，如果设置了category_id参数，则等于category_id等于该参数的分类不参与运算
     *
     * @param category 分类查找参数
     * @return 相同名称的数量
     */
    Integer getCategoryName(@Param("category") Category category);

    /**
     * 根据条件参数获取分类列表
     *
     * @param grade  最大级别
     * @param status 状态
     * @param shopId 店铺ID
     * @return 分类列表
     */
    List<Category> listByGrade(@Param("grade") Integer grade,
                               @Param("status") Integer status,
                               @Param("shopId") Long shopId,
                               @Param("lang") Integer lang);

    /**
     * 根据条件参数获取分类列表
     *
     * @param shopId   店铺ID
     * @param parentId 父分类ID
     * @return 分类列表
     */
    List<Category> listSelect(@Param("shopId") Long shopId, @Param("parentId") Long parentId);

    /**
     * 获取分类及上级分类列表
     *
     * @param categoryIds
     * @return
     */
    List<Category> listAndParentByIds(@Param("categoryIds") Set<Long> categoryIds);

    /**
     * 获取当前节点所有父节点的分类，以及当前分类节点的父级节点的父级节点的分类
     *
     * @param categoryIds 当前分类节点ids
     * @return 所有父级节点
     */
    List<Category> getParentCategoryByCategoryId(@Param("categoryIds") List<Long> categoryIds);

    /**
     * 获取整个平台的佣金比例
     *
     * @return 整个平台的佣金比例
     */
    List<Category> listRate();

    /**
     * 根据分类id列表，获取分类列表
     *
     * @param categoryIds
     * @return
     */
    List<Category> getListByCategoryIds(@Param("categoryIds") Set<Long> categoryIds);

    /**
     * 根据店铺id与父分类id获取分类id列表
     *
     * @param shopId
     * @param parentId
     * @return
     */
    List<Long> listCategoryIdByShopIdAndParentId(@Param("shopId") Long shopId, @Param("parentId") Long parentId);

    /**
     * 根据店铺id和父分类id获取分类列表
     *
     * @param shopId
     * @param parentId
     * @param grade
     * @return
     */
    List<Category> getListByShopIdAndParentId(@Param("shopId") Long shopId, @Param("parentId") Long parentId, @Param("grade") Integer grade);

    /**
     * 获取分类及分类国际化列表
     *
     * @param shopId
     * @return
     */
    List<Category> listCategoryAndLangList(@Param("shopId") Long shopId);

    /**
     * 获取分类及上级分类的信息
     *
     * @param categoryId
     * @return
     */
    List<Category> getCategoryAndParent(@Param("categoryId") Long categoryId);

    /**
     * 获取Es数据形式的分类集合
     * @param lang 语言
     * @param categoryIds 分类id集合
     * @return Es数据形式的分类集合
     */
    List<CategorySearchVO> listEsCategoryByCategoryIds(@Param("lang") Integer lang,
                                                       @Param("categoryIds") List<Long> categoryIds);
}
