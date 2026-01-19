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
import com.yami.shop.bean.model.ProdParameter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品参数mapper
 * @author Citrus
 * @date 2021-11-01 16:50:52
 */
public interface ProdParameterMapper extends BaseMapper<ProdParameter> {
    /**
     * 根据商品id删除商品参数
     * @param prodId
     */
    void removeByProdId(@Param("prodId") Long prodId);

    /**
     * 根据商品id查询商品参数
     * @param prodId
     * @return
     */
    List<ProdParameter> listProdParameterByProdId(@Param("prodId") Long prodId);
}
