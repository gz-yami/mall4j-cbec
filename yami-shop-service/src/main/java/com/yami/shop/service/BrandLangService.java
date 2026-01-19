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
import com.yami.shop.bean.model.BrandLang;

import java.util.List;

/**
 * 品牌-国际化表
 *
 * @author YXF
 * @date 2021-08-09 09:51:55
 */
public interface BrandLangService extends IService<BrandLang> {
    /**
     * 保存品牌-国际化表
     * @param brandLangList 品牌-国际化表
     * @param brandId 品牌id
     */
    void saveBrandLang(List<BrandLang> brandLangList, Long brandId);

    /**
     * 更新品牌-国际化表
     * @param brandLangList 品牌-国际化表
     * @param brandId 品牌id
     */
    void updateBrandLang(List<BrandLang> brandLangList, Long brandId);
}
