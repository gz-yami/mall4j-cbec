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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.model.ProdProp;

import java.util.List;

/**
 *
 *
 * @author lgh
 * @date 2018/07/06
 */
public interface ProdPropService extends IService<ProdProp> {


    /**
     * 分页获取商品属性与属性值列表
     * @param page 分页参数
     * @param prodProp 查询参数
     * @return 商品规格属性列表
     */
    IPage<ProdProp> pagePropAndValue(ProdProp prodProp, Page<ProdProp> page);

    /**
     * 保存属性、属性值
     * @param prodProp 属性与属性值
     */
    void saveProdPropAndValues(ProdProp prodProp);

    /**
     * 更新属性、属性值
     * @param prodProp 属性与属性值
     */
    void updateProdPropAndValues(ProdProp prodProp);

    /**
     * 删除属性、属性值
     * @param propId 属性id
     * @param propRule 如果propRule为2，同时删除分类与属性值之间的关联关系
     * @param shopId 店铺id
     */
    void deleteProdPropAndValues(Long propId,Integer propRule,Long shopId);

    /**
     * 根据分类获取分类下的规格
     * @param categoryId 分类id
     * @return 规格列表
     */
    List<ProdProp> listByCategoryId(Long categoryId);

    /**
     * 根据不同的语言获取不同的规格显示数据
     * @param prodProp 规格查询参数
     * @return 规格参数列表
     */
    List<ProdProp> listByLang(ProdProp prodProp);
}
