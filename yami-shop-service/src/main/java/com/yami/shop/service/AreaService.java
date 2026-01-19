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
import com.yami.shop.bean.dto.AreaDto;
import com.yami.shop.bean.model.Area;

import java.util.List;

/**
 * @author lgh on 2018/10/26.
 */
public interface AreaService extends IService<Area> {

    /**
     * 通过pid 查找地址接口
     *
     * @param pid 父id
     * @param level 等级
     * @return
     */
    List<Area> listByPid(Long pid, Integer level);

    /**
     * 通过pid 清除地址缓存
     *
     * @param pid
     * @param level 等级
     */
    void removeAreaCacheByParentId(Long pid, Integer level);

    /**
     * 删除区域列表缓存
     */
    void removeAreaListCache();

    /**
     * 获取所有区域组合数据列表
     * @return 区域列表
     */
    List<AreaDto> getAreaListInfo();

    /**
     * 获取省信息列表
     * @return 省列表
     */
    List<Area> getProvinceInfoList();

    /**
     * 获取可用的省市区列表
     * @return
     */
    List<AreaDto> listAreaOfEnable();

    /**
     * 获取四层可用列表
     * @return
     */
    List<AreaDto> getAllAreaList();

    /**
     * 根据id获取列表
     * @param areIdList
     * @return
     */
    List<AreaDto> listByAreaIdList(List<Long> areIdList);

    /**
     * 获取所有区域组合数据列表
     * @return 区域列表
     */
    List<AreaDto> getCountryAreaListInfo();
}
