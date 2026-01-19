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
import com.yami.shop.bean.model.SkuLang;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 *
 * @author lhd
 * @date 2020-08-31 13:54:58
 */
public interface SkuLangMapper extends BaseMapper<SkuLang> {

    /**
     * 根据商品id删除sku语言表
     * @param prodId 商品id
     */
    void deleteByProdId(@Param("prodId") Long prodId);

    /**
     * 批量更新
     * @param skuLangList
     */
    void batchUpdateBySkuIdAndLang(@Param("skuLangList") List<SkuLang> skuLangList);
}
