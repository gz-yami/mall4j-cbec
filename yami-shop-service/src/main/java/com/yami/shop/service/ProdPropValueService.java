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
import com.yami.shop.bean.model.ProdPropValue;

import java.util.List;

/**
 *
 *
 * @author lgh
 * @date 2018/07/06
 */
public interface ProdPropValueService extends IService<ProdPropValue> {

    /**
     * 根据属性id获取属性值
     * @param propId 属性id
     * @return 属性值列表
     */
    List<ProdPropValue> propValueListByPropId(Long propId);

    /**
     * 批量插入属性属性值数据
     * @param propId 属性id
     * @param prodPropValues 属性值
     */
    void insertBatch(List<ProdPropValue> prodPropValues, Long propId);

    /**
     * 更新属性属性值数据 先删除原有的属性值，再添加新的属性值
     * @param propId 属性id
     * @param prodPropValues 属性值
     */
    void updatePropValues(Long propId, List<ProdPropValue> prodPropValues);

    /**
     * 根据属性id删除属性属性值信息
     * @param propId 属性id
     */
    void deleteByPropId(Long propId);

}
