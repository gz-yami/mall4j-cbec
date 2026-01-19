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
import com.yami.shop.bean.model.ProdPropValue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yami
 */
public interface ProdPropValueMapper extends BaseMapper<ProdPropValue> {

    /**
     * 根据属性id删除属性属性值信息
     * @param propId 属性id
     */
    void deleteByPropId(@Param("propId") Long propId);

    /**
     * 根据属性id获取属性值
     * @param propId 属性id
     * @return 属性值列表
     */
    List<ProdPropValue> propValueListByPropId(@Param("propId") Long propId);
}
