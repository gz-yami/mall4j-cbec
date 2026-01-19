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
import com.yami.shop.bean.model.ProdExtension;

import java.util.List;

/**
 *
 *
 * @author LGH
 * @date 2022-05-05 10:47:48
 */
public interface ProdExtensionService extends IService<ProdExtension> {

    /**
     * 获取指定的商品的商品扩展信息
     * @param prodId 商品id
     * @return
     */
    ProdExtension getByProdId(Long prodId);


    /**
     * 删除缓存
     * @param prodId
     */
    void removeProdExtensionCache(Long prodId);


    /**
     * 更新Es商品信息到数据库
     * @param prodIds 商品id集合
     */
    void updateEsProdInfoToDb(List<Long> prodIds);
}
