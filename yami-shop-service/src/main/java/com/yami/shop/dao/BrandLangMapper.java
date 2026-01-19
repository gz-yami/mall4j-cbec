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
import com.yami.shop.bean.model.BrandLang;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 品牌-国际化表
 *
 * @author YXF
 * @date 2021-08-09 09:51:55
 */
public interface BrandLangMapper extends BaseMapper<BrandLang> {


    /**
     * 批量更新
     *
     * @param brandLangList
     */
    void batchUpdate(@Param("brandLangList") List<BrandLang> brandLangList);

    /**
     * 保存
     *
     * @param brandLang
     */
    void save(@Param("brandLang") BrandLang brandLang);
}
