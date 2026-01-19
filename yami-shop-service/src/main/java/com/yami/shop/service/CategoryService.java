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
import com.yami.shop.bean.model.Category;
import com.yami.shop.bean.vo.CategoryVO;
import com.yami.shop.bean.vo.search.CategorySearchVO;
import com.yami.shop.common.util.PageParam;

import java.util.List;
import java.util.Set;

/**
 * 商品分类
 * @author yami
 */
public interface CategoryService extends IService<Category> {

    /**
     * 保存分类、品牌、参数
     * @param category 分类信息
     */
    void saveCategroy(Category category);

    /**
     * 修改分类、品牌、参数
     * @param category 分类信息
     */
    void updateCategroy(Category category);

    /**
     * 删除分类、品牌、参数 以及分类对应的图片
     * @param category 分类信息
     */
    void deleteCategroy(Category category);

    /**
     * 通过分类id和店铺id获取分类信息
     * @param categoryId 分类ID
     * @param shopId 店铺ID
     * @return 分类信息
     */
    Category getCategoryByCategoryIdAndShopId(Long categoryId, Long shopId);

    /**
     * 根据店铺id和顶级分类id，获取分类
     * @param parentId 顶级分类id
     * @param shopId 店铺id
     * @return 分类信息列表
     */
    List<Category> listCategoryAndChildCategory(Long parentId, Long shopId);

    /**
     * 根据店铺id获取分类
     * @param shopId 店铺id
     * @return 分类信息列表
     */
    List<Category> listByShopId(Long shopId);

    /**
     * 根据分类ID获取该分类的父分类ID
     * @param parentId 分类ID
     * @return 父分类ID
     */
    Long getParentCategoryByParentId(Long parentId);

    /**
     * 通过顶级id和当前选择语言删除缓存
     * @param parentId
     * @param shopId
     */
    void removeCacheByParentIdAndLang(Long parentId,Long shopId);

    /**
     * 根据分类查找条件参数查找分类列表
     * @param category 分类查找条件参数
     * @return 分类列表
     */
    List<Category> categoryList(Category category);

    /**
     * 根据分类查找条件参数查找分类列表[语言]
     * @param category 分类查找条件参数
     * @return 分类列表
     */
    List<Category> categoryLangList(Category category);

    /**
     * 分页 - 根据分类查找条件参数查找分类列表[语言]
     * @param page 分页参数
     * @param category 分类查找条件
     * @return
     */
    IPage<Category> pageCategoryLangList(PageParam<Category> page, Category category);

    /**
     * 根据语言获取分类
     * @param lang 当前选择语言
     * @param maxGrade 最大级别
     * @param parentId 父分类ID
     * @param status 状态
     * @param shopId 店铺ID
     * @return 分类信息列表
     */
    List<Category> listByLang(Integer lang, Integer maxGrade, Long parentId, Integer status, Long shopId);

    /**
     * 根据名称查找名称相同的分类数量，如果设置了category_id参数，则等于category_id等于该参数的分类不参与运算
     * @param category 分类查找参数
     * @return 相同名称的数量
     */
    Integer getCategoryName(Category category);

    /**
     * 根据分类ID获取分类
     * @param categoryId 分类ID
     * @return 分类信息
     */
    Category getCategoryByCategoryId(Long categoryId);

    /**
     * 根据等级,店铺id,状态获取分类列表
     * @param grade 最大级别
     * @param status 状态
     * @param shopId 店铺id
     * @return 分类信息列表
     */
    List<Category> listByGrade(Integer grade, Integer status, Long shopId);


    /**
     * 获取整个平台的佣金比例
     * @return 整个平台的佣金比例
     */
    List<Category> listRate();

    /**
     * 清除整个平台的佣金比例缓存
     * @return 清除整个平台的佣金比例缓存
     */
    void removeListRateCache();

    /**
     * 获取当前节点所有父节点的分类ids，以及当前分类节点的父级节点的父级几点的分类ids
     * @param categoryIds 当前分类节点ids
     * @return 所有父级节点ids
     */
    List<Long> getParentIdsByCategoryId(List<Long> categoryIds);

    /**
     *  获取分类的pathName集合
     * @param categories 分类集合
     */
    void getPathNames(List<CategoryVO> categories);

    /**
     * 根据分类id列表获取分类列表
     * @param cids
     * @return
     */
    List<Category> listByCategoryIds(Set<Long> cids);

    /**
     * 根据店铺id，父分类id，级别获取分类列表
     * @param shopId
     * @param parentId
     * @param grade
     * @return
     */
    List<Category> listByShopIdAndParentIdAndGrade(Long shopId, Long parentId, Integer grade);

    /**
     * 平台可用分类-必须是启用分类且分类下包含启用的三级分类
     * 目前前端用的都是maxGrade = 2,也就是包含一到三级启用的分类，
     * 而且要判断分类下是否包含三级分类也要查询所有分类进行判断，所以此处不需要传参了，
     * 默认返回所有符合条件的分类
     * @return
     */
    List<Category> platformCategory();

    /**
     * 获取分类及上级分类的信息
     * @param categoryId
     * @return
     */
    List<Category> getCategoryAndParent(Long categoryId);


    /**
     * 根据分类信息(分类、上级分类名称列表)
     *
     * @param categoryId 分类信息id
     * @return 分类信息
     */
    CategoryVO getInfo(Long categoryId);

    /**
     * 获取指定店铺的分类及语言列表
     * @param shopId
     * @return
     */
    List<Category> listCategoryAndLangList(Long shopId);

    /**
     * 获取Es数据形式的分类集合
     * @param categoryIds 分类id集合
     * @return Es数据形式的分类集合
     */
    List<CategorySearchVO> listEsCategoryByCategoryIds(List<Long> categoryIds);
}
