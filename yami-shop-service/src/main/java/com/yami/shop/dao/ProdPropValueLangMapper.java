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
import com.yami.shop.bean.model.ProdPropValueLang;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yami
 * @date 2021-02-24 17:02:07
 */
public interface ProdPropValueLangMapper extends BaseMapper<ProdPropValueLang> {

    /**
     * 批量插入属性属性值和语言数据
     * @param prodPropValueLangList 属性属性值和语言数据
     */
    void insetBatch(@Param("prodPropValueLangList") List<ProdPropValueLang> prodPropValueLangList);

    /**
     * 根据属性id删除属性属性值信息
     * @param propId 属性id
     */
    void deleteByPropId(@Param("propId") Long propId);
}
