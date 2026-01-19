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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.ProdParameter;
import com.yami.shop.bean.model.ProdParameterLang;
import com.yami.shop.common.bean.LangConfig;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.dao.ProdParameterMapper;
import com.yami.shop.manager.impl.LangManager;
import com.yami.shop.service.ProdParameterLangService;
import com.yami.shop.service.ProdParameterService;
import lombok.AllArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Citrus
 * @date 2021-11-01 16:50:52
 */
@Service
@AllArgsConstructor
public class ProdParameterServiceImpl extends ServiceImpl<ProdParameterMapper, ProdParameter> implements ProdParameterService {

    private final ProdParameterMapper prodParameterMapper;
    private final ProdParameterLangService prodParameterLangService;
    private final LangManager langManager;

    @Override
    @Cacheable(cacheNames = "listParameterByProdId", key = "#prodId")
    public List<ProdParameter> listParameterAndLang(Long prodId) {
        List<ProdParameter> prodParameters = prodParameterMapper.listProdParameterByProdId(prodId);
        langManager.getProdParameterAndLang(prodParameters);
        return prodParameters;
    }

    @Override
    public List<ProdParameter> listParameterByProdId(Long prodId) {
        ProdParameterService prodParameterService = (ProdParameterService) AopContext.currentProxy();
        List<ProdParameter> prodParameters = prodParameterService.listParameterAndLang(prodId);
        langManager.handleProdParameterLang(prodParameters);
        return prodParameters;
    }

    @Override
    @CacheEvict(cacheNames = "listParameterByProdId", key = "#prodId")
    public void removeCacheByProdId(Long prodId) {
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAndUpdateParameter(List<ProdParameter> prodParameterList, Long prodId) {
        List<ProdParameter> dbProdParameterList = listParameterAndLang(prodId);
        if (CollUtil.isEmpty(dbProdParameterList) && CollUtil.isEmpty(prodParameterList)) {
            return;
        }
        // 先删除，再新增
        if (CollUtil.isNotEmpty(dbProdParameterList)) {
            prodParameterMapper.removeByProdId(prodId);
        }

        // 保存参数
        for (ProdParameter prodParameter : prodParameterList) {
            prodParameter.setProdParameterId(null);
            prodParameter.setProdId(prodId);
        }
        saveBatch(prodParameterList);
        removeCacheByProdId(prodId);

        // 保存参数语言信息
        List<ProdParameterLang> prodParameterLangList = new ArrayList<>();
        for (ProdParameter prodParameter : prodParameterList) {
            checkProdParameterLang(prodParameter.getProdParameterLangList());
            for (ProdParameterLang prodParameterLang : prodParameter.getProdParameterLangList()) {
                prodParameterLang.setProdParameterId(prodParameter.getProdParameterId());
            }
            prodParameterLangList.addAll(prodParameter.getProdParameterLangList());
        }
        // 先删除再保存
        prodParameterLangService.insertBatch(prodParameterLangList);
    }

    private void checkProdParameterLang(List<ProdParameterLang> prodParameterLangList) {
        LangConfig configLang = langManager.getLangConfig();
        boolean containsMaster = false;
        for (ProdParameterLang prodParameterLang : prodParameterLangList) {
            if (Objects.equals(prodParameterLang.getLang(), configLang.getLang())) {
                containsMaster = true;
            }
        }
        if (!containsMaster) {
            // 语言数据已更新，请重新录入商品信息
            throw new YamiShopBindException("yami.prod.parameter.exception.langUpdate");
        }
    }

    @Override
    public void removeByProdId(Long prodId) {
        prodParameterMapper.removeByProdId(prodId);
    }
}
