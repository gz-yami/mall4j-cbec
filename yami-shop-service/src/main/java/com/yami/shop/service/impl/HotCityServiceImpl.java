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

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.HotCity;
import com.yami.shop.dao.HotCityMapper;
import com.yami.shop.service.HotCityService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chiley
 * @date 2022-12-01 14:02:57
 */
@Service
@AllArgsConstructor
public class HotCityServiceImpl extends ServiceImpl<HotCityMapper, HotCity> implements HotCityService {

    private final HotCityMapper hotCityMapper;


    @Override
    @Cacheable(cacheNames = "HotCityList")
    public List<HotCity> listHotCity() {
        return hotCityMapper.listHotCity();
    }

    @Override
    @CacheEvict(cacheNames = "HotCityList")
    public void removeHotCityList() {
    }
}
