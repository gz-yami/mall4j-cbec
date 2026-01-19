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
import com.yami.shop.bean.dto.AreaDto;
import com.yami.shop.bean.model.Area;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yami
 */
public interface AreaMapper extends BaseMapper<Area> {
    /**
     * 获取所有区域的组合数据列表
     * @return 区域信息列表
     */
    List<AreaDto> getAreaListInfo();

    /**
     * 如果level = 1 ，过滤掉没有三级地址的一级地址
     * @param pid 父级id
     * @param level 地址级别
     * @return 地址列表
     */
    List<Area> listByPid(@Param("pid") Long pid, @Param("level") Integer level);

    /**
     * 获取可用的省市区列表
     * @param lang 语言编号
     * @return
     */
    List<AreaDto> listAreaOfEnable(@Param("lang") Integer lang);

    /**
     * 获取四层可用列表
     * @return
     */
    List<AreaDto> getAllAreaList();
}
