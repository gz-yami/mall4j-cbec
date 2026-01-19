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

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.IndexImg;
import com.yami.shop.dao.IndexImgMapper;
import com.yami.shop.service.IndexImgService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author lgh on 2018/11/26.
 */
@Service
@AllArgsConstructor
public class IndexImgServiceImpl extends ServiceImpl<IndexImgMapper, IndexImg> implements IndexImgService {

    private final IndexImgMapper indexImgMapper;

    @Override
    public void deleteIndexImgsByIds(Long[] ids, Long shopId) {
        indexImgMapper.deleteIndexImgsByIds(ids,shopId);
    }

    @Override
    @Cacheable(cacheNames = "indexImg", key = "#shopId +':'+ #imgType")
    public List<IndexImg> listIndexImgsByShopId(Long shopId, Integer imgType) {
        return indexImgMapper.selectList(new LambdaQueryWrapper<IndexImg>()
                .eq(IndexImg::getShopId,shopId)
                .eq(IndexImg::getStatus,1)
                .eq(IndexImg::getImgType,imgType)
                .orderByDesc(IndexImg::getSeq));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "indexImg", key = "#shopId + ':1'"),
            @CacheEvict(cacheNames = "indexImg", key = "#shopId + ':0'")

    })
    public void removeIndexImgCacheByShopId(Long shopId) {

    }

    @Override
    public void updateImgProd(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        indexImgMapper.updateImgProd(ids);
    }

}
