/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.ProdExtension;
import com.yami.shop.bean.vo.SkuStockVO;
import com.yami.shop.common.constants.CacheNames;
import com.yami.shop.common.exception.YamiShopBindException;

import com.yami.shop.dao.ProdExtensionMapper;
import com.yami.shop.service.ProdExtensionService;
import com.yami.shop.service.SkuService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 *
 * @author LGH
 * @date 2022-05-05 10:47:48
 */
@Service
@AllArgsConstructor
public class ProdExtensionServiceImpl extends ServiceImpl<ProdExtensionMapper, ProdExtension> implements ProdExtensionService {

    private final ProdExtensionMapper prodExtensionMapper;

    private final SkuService skuService;

    @Override
    @Cacheable(cacheNames = CacheNames.PROD_EXTENSION_INFO_KEY, key = "#prodId")
    public ProdExtension getByProdId(Long prodId) {
        ProdExtension prodExtension = getOne(new LambdaQueryWrapper<ProdExtension>().eq(ProdExtension::getProdId, prodId));
        if (Objects.isNull(prodExtension)) {
            throw new YamiShopBindException("yami.product.no.exist");
        }
        return prodExtension;
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.PROD_EXTENSION_INFO_KEY, key = "#prodId")
    public void removeProdExtensionCache(Long prodId) {
    }


    @Override
    public void updateEsProdInfoToDb(List<Long> prodIds) {
        if (CollectionUtils.isEmpty(prodIds)) {
            return;
        }
        List<ProdExtension> prodExtensions = this.list(new LambdaQueryWrapper<ProdExtension>().in(ProdExtension::getProdId, prodIds));
       // 批量更新
        this.updateBatchById(prodExtensions);
    }
}
