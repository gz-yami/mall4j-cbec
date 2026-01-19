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
import com.yami.shop.bean.model.ProdParameterLang;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品参数语言mapper
 * @author Citrus
 * @date 2021-11-02 11:26:08
 */
public interface ProdParameterLangMapper extends BaseMapper<ProdParameterLang> {
    /**
     * 批量保存商品参数信息
     * @param prodParameterLangList
     */
    void insertBatch(@Param("prodParameterLangList") List<ProdParameterLang> prodParameterLangList);

    /**
     * 批量删除
     * @param ids
     */
    void deleteBatch(@Param("ids") List<Long> ids);
}
