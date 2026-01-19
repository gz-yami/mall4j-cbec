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
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.yami.shop.bean.dto.AreaDto;
import com.yami.shop.bean.model.Area;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.dao.AreaMapper;
import com.yami.shop.service.AreaService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lgh on 2018/10/26.
 */
@Service
@AllArgsConstructor
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {

    private final AreaMapper areaMapper;

    @Override
    @Cacheable(cacheNames = "area", key = "#pid + ':' + #level")
    public List<Area> listByPid(Long pid, Integer level) {
        List<Area> areas = areaMapper.listByPid(pid, level);
        return areas;
    }

    @Override
    @CacheEvict(cacheNames = "area", key = "#pid + ':' + #level")
    public void removeAreaCacheByParentId(Long pid, Integer level) {

    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "area", key = "'areaList'"),
            @CacheEvict(cacheNames = "area", key = "'areaProvinceList'"),
            @CacheEvict(cacheNames = "area", key = "'allAreaList'")
    })
    public void removeAreaListCache() {
    }

    @Override
    @Cacheable(cacheNames = "area", key = "'areaList'")
    public List<AreaDto> getAreaListInfo() {
        List<AreaDto> areaList = areaMapper.getAreaListInfo();
        for (AreaDto province : areaList) {
            List<Long> cityAll = Lists.newArrayList();
            for (AreaDto city : province.getAreas()) {
                cityAll.add(city.getAreaId());
                List<Long> areaAll = Lists.newArrayList();
                for (AreaDto area : city.getAreas()) {
                    areaAll.add(area.getAreaId());
                }
                city.setAreaIds(areaAll);
            }
            province.setAreaIds(cityAll);
        }
        return areaList;
    }

    @Override
    @Cacheable(cacheNames = "area", key = "'areaProvinceList'")
    public List<Area> getProvinceInfoList() {
        List<Area> areaList = areaMapper.selectList(new LambdaQueryWrapper<Area>().eq(Area::getLevel, 1));
        return areaList;
    }

    @Override
    public List<AreaDto> listAreaOfEnable() {
        Integer lang = I18nMessage.getI18nLang().getCurrentLang().getLang();
        return areaMapper.listAreaOfEnable(lang);
    }

    @Override
    @Cacheable(cacheNames = "area", key = "'allAreaList'")
    public List<AreaDto> getAllAreaList(){
        List<AreaDto> allAreaList = areaMapper.getAllAreaList();
        Map<Long, List<AreaDto>> areaMap = allAreaList.stream().collect(Collectors.groupingBy(AreaDto::getParentId));
        List<AreaDto> regionList = areaMap.get(0L);
        if (CollUtil.isEmpty(regionList)) {
            return new ArrayList<>();
        }
        Iterator<AreaDto> iterator = regionList.iterator();
        while (iterator.hasNext()) {
            AreaDto areaDto = iterator.next();
            List<AreaDto> areaList = areaMap.get(areaDto.getAreaId());
            if (CollUtil.isEmpty(areaList)) {
                continue;
            }
            extracted(areaList, areaMap);
            if (CollUtil.isEmpty(areaList)) {
                continue;
            }
            areaDto.setAreas(areaList);
            List<Long> areaIdsList = new ArrayList<>();
            areaList.forEach(s->areaIdsList.add(s.getAreaId()));
            areaDto.setAreaIds(areaIdsList);
        }
        return regionList;
    }

    @Override
    public List<AreaDto> listByAreaIdList(List<Long> areIdList) {
        List<AreaDto> list = new ArrayList<>(areIdList.size());
        if (CollectionUtil.isEmpty(areIdList)) {
            return list;
        }
        List<Area> areaList = areaMapper.selectList(new LambdaQueryWrapper<Area>().in(Area::getAreaId, areIdList));
        list = BeanUtil.mapAsList(areaList, AreaDto.class);
        return list;
    }

    @Override
    public List<AreaDto> getCountryAreaListInfo() {
        List<Area> areaList = list(new LambdaQueryWrapper<Area>().eq(Area::getLevel, 1));
        return BeanUtil.mapAsList(areaList, AreaDto.class);
    }

    private void extracted(List<AreaDto> list, Map<Long, List<AreaDto>> areaMap) {
        Iterator<AreaDto> iterator = list.iterator();
        while (iterator.hasNext()) {
            AreaDto areaDto = iterator.next();
            if (!areaMap.containsKey(areaDto.getAreaId())) {
                continue;
            }
            List<AreaDto> cityList = new ArrayList<>();
            List<Long>  areaids = new ArrayList<>();
            for (AreaDto city : areaMap.get(areaDto.getAreaId())) {
                cityList.add(city);
                areaids.add(city.getAreaId());
                if (!areaMap.containsKey(city.getAreaId())) {
                    continue;
                }
                city.setAreas(areaMap.get(city.getAreaId()));
                List<Long> areaIdsList = new ArrayList<>();
                city.getAreas().forEach(s->areaIdsList.add(s.getAreaId()));
                city.setAreaIds(areaIdsList);
            }
            if (CollUtil.isEmpty(cityList)) {
                continue;
            }
            areaDto.setAreas(cityList);
            areaDto.setAreaIds(areaids);
        }
    }
}
