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
import com.yami.shop.bean.model.ProdParameter;

import java.util.List;

/**
 * 商品参数service
 *
 * @author Citrus
 * @date 2021-11-01 16:50:52
 */
public interface ProdParameterService extends IService<ProdParameter> {

    /**
     * 根据商品id查询参数列表，商家端  【缓存】
     *
     * @param prodId
     * @return
     */
    List<ProdParameter> listParameterAndLang(Long prodId);

    /**
     * 用户端查参数列表
     * @param prodId
     * @return
     */
    List<ProdParameter> listParameterByProdId(Long prodId);

    /**
     * 根据商品id删除缓存
     *
     * @param prodId
     */
    void removeCacheByProdId(Long prodId);

    /**
     * 商品参数批量操作
     *
     * @param prodParameterList
     * @param prodId
     */
    void saveAndUpdateParameter(List<ProdParameter> prodParameterList, Long prodId);

    /**
     * 根据商品id删除商品参数
     *
     * @param prodId
     */
    void removeByProdId(Long prodId);
}
