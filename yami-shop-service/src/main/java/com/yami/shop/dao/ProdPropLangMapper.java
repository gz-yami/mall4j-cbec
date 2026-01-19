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
import com.yami.shop.bean.model.ProdPropLang;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yami
 * @date 2021-02-24 17:02:07
 */
public interface ProdPropLangMapper extends BaseMapper<ProdPropLang> {

    /**
     * 批量插入规格语言信息
     * @param prodPropLangList 规格语言信息
     */
    void insertBatch(@Param("prodPropLangList") List<ProdPropLang> prodPropLangList);

    /**
     * 删除商品规格语言表
     * @param propId 规格id
     */
    void deleteByPropId(@Param("propId") Long propId);
}
