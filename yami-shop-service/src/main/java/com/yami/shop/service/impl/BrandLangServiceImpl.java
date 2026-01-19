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
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.BrandLang;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.i18n.bean.I18nLangInfo;
import com.yami.shop.dao.BrandLangMapper;
import com.yami.shop.service.BrandLangService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 品牌-国际化表
 *
 * @author YXF
 * @date 2021-08-09 09:51:55
 */
@Service
@AllArgsConstructor
public class BrandLangServiceImpl extends ServiceImpl<BrandLangMapper, BrandLang> implements BrandLangService {

    private final BrandLangMapper brandLangMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBrandLang(List<BrandLang> brandLangList, Long brandId) {
        if (CollUtil.isEmpty(brandLangList)) {
            return;
        }
        I18nLangInfo langInfo = I18nMessage.getI18nLang();
        Iterator<BrandLang> iterator = brandLangList.iterator();
        while (iterator.hasNext()) {
            BrandLang brandLang = iterator.next();
            if (StrUtil.isBlank(brandLang.getName())) {
                if (Objects.equals(brandLang.getLang(), langInfo.getDefaultLang().getLang())) {
                    throw new YamiShopBindException("yami.brand.exception.defaultNull");
                }
                iterator.remove();
                continue;
            }
            brandLang.setBrandId(brandId);
        }
        saveBatch(brandLangList);
    }

    @Override
    public void updateBrandLang(List<BrandLang> brandLangList, Long brandId) {
        if (CollUtil.isEmpty(brandLangList)) {
            return;
        }
        List<BrandLang> list = list(new LambdaQueryWrapper<BrandLang>().eq(BrandLang::getBrandId, brandId));
        Map<Integer, String> langMap = list.stream().collect(Collectors.toMap(BrandLang::getLang, BrandLang::getName));
        Iterator<BrandLang> iterator = brandLangList.iterator();
        I18nLangInfo langInfo = I18nMessage.getI18nLang();
        while (iterator.hasNext()) {
            BrandLang brandLang = iterator.next();
            brandLang.setBrandId(brandId);
            if (StrUtil.isBlank(brandLang.getName())) {
                if (Objects.equals(brandLang.getLang(), langInfo.getDefaultLang().getLang())) {
                    throw new YamiShopBindException("yami.brand.exception.defaultNull");
                }
                // 数据库中有，新的数据中为空，则删除
                if (langMap.containsKey(brandLang.getLang())) {
                    brandLangMapper.delete(new LambdaQueryWrapper<BrandLang>().eq(BrandLang::getBrandId, brandId).eq(BrandLang::getLang, brandLang.getLang()));
                }
                iterator.remove();
                continue;
            }
            // 数据库中没有包含该语言，则新增
            if (!langMap.containsKey(brandLang.getLang())) {
                brandLangMapper.save(brandLang);
            }
        }
        brandLangMapper.batchUpdate(brandLangList);
    }
}
