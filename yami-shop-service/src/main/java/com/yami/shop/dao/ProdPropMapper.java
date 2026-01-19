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
import com.yami.shop.bean.model.ProdProp;
import com.yami.shop.common.util.PageAdapter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yami
 */
public interface ProdPropMapper extends BaseMapper<ProdProp> {

    /**
     * 分页获取商品属性与属性值列表
     * @param adapter 分页参数
     * @param prodProp 查询参数
     * @return 商品属性与属性值列表
     */
    List<ProdProp> listPropAndValue(@Param("adapter") PageAdapter adapter, @Param("prodProp") ProdProp prodProp);

    /**
     * 获取商品属性与属性值列表数量
     * @param prodProp 查询参数
     * @return 商品属性与属性值列表数量
     */
    long countPropAndValue(@Param("prodProp") ProdProp prodProp);

    /**
     * 根据商品id删除规格
     * @param propId 规格id
     * @param rule 规则
     * @param shopId 店铺id
     * @return 是否删除成功
     */
    int deleteByPropId(@Param("propId") Long propId, @Param("rule") Integer rule, @Param("shopId") Long shopId);

    /**
     * 根据分类获取分类下的规格
     * @param categoryId 分类id
     * @return 规格列表
     */
    List<ProdProp> listByCategoryId(Long categoryId);

    /**
     * 获取规格参数
     * @param propName 规格名称
     * @param shopId 店铺id
     * @param rule 规则
     * @return 规格参数列表
     */
    List<ProdProp> getProdPropByPropNameAndShopId(@Param("propName") String propName,
                                            @Param("shopId") Long shopId,
                                            @Param("rule") Integer rule);

    /**
     * 根据不同的语言获取不同的规格显示数据
     * @param prodProp 规格查询参数
     * @return 规格参数列表
     */
    List<ProdProp> listByLang(@Param("prodProp") ProdProp prodProp);
}
