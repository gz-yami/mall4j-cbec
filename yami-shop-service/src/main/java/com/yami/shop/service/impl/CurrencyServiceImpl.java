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

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.dto.CurrencyDTO;
import com.yami.shop.bean.model.Currency;
import com.yami.shop.bean.model.CurrencyLang;
import com.yami.shop.bean.vo.*;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.i18n.bean.I18nLangInfo;
import com.yami.shop.common.util.Arith;
import com.yami.shop.common.util.HttpContextUtils;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.dao.CurrencyMapper;
import com.yami.shop.service.CurrencyLangService;
import com.yami.shop.service.CurrencyService;
import com.yami.shop.service.LangService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author lhd
 */
@Service
@AllArgsConstructor
public class CurrencyServiceImpl extends ServiceImpl<CurrencyMapper, Currency> implements CurrencyService {

    private final CurrencyMapper currencyMapper;
    private final CurrencyLangService currencyLangService;

    @Override
//    @Cacheable(cacheNames = "enableCurrencies", key = "'enableCurrencies:' + '-' + #lang")
    public List<CurrencyVO> listEnableCurrency() {
        I18nLangInfo i18nLang = I18nMessage.getI18nLang();
        List<CurrencyVO> currencies = currencyMapper.listEnableCurrency();
        // 处理一下语言
        for (CurrencyVO currency : currencies) {
            // 默认语言
            String name = "";
            Map<Integer, String> map = currency.getCurrencyLangs().stream().collect(Collectors.toMap(CurrencyLangVO::getLangId, CurrencyLangVO::getCurrencyName));
            if(map.containsKey(i18nLang.getCurrentLang().getLangId())){
                currency.setCurrencyName(map.get(i18nLang.getCurrentLang().getLangId()));
            }else{
                currency.setCurrencyName(map.get(i18nLang.getDefaultLang().getLangId()));
            }
        }
        return currencies;
    }

    /**
     * 汇率计算
     */
    @Override
    public Double getSelectCurrencyAmount(Double price) {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        String currencyCode = request.getHeader("currency");
        CurrencyServiceImpl currencyService = (CurrencyServiceImpl) AopContext.currentProxy();
        Double exchangeRate = 1.0;
        if(StrUtil.isBlank(currencyCode)){
            return price;
        }
        List<CurrencyVO> currencies = currencyService.listEnableCurrency();
        for (CurrencyVO currency : currencies) {
            if(!StrUtil.equals(currency.getCurrencyCode(), currencyCode)){
                continue;
            }
            exchangeRate = currency.getExchangeRate();
            return Arith.mul(price,exchangeRate);
        }
        return 0.0;
    }

    @Override
    public CurrencyVO getCurrencyById(Long id) {
        return currencyMapper.getCurrencyById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCurrency(Currency currency) {
        updateById(currency);
        if (CollectionUtils.isEmpty(currency.getCurrencyLangs())) {
            return;
        }
        // 先删后增
        currencyLangService.remove(new LambdaQueryWrapper<CurrencyLang>()
                .eq(CurrencyLang::getId,currency.getId()));
        List<CurrencyLang> currencyLangs = currency.getCurrencyLangs();
        currencyLangs.forEach(currencyLang -> currencyLang.setId(currency.getId()));
        currencyLangService.saveBatch(currencyLangs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeCurrency(Long id) {
        removeById(id);
        // 先删后增
        currencyLangService.remove(new LambdaQueryWrapper<CurrencyLang>()
                .eq(CurrencyLang::getId,id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCurrency(Currency currency) {
        save(currency);
        if (CollectionUtils.isEmpty(currency.getCurrencyLangs())) {
            return;
        }
        List<CurrencyLang> currencyLangs = currency.getCurrencyLangs();
        currencyLangs.forEach(currencyLang -> currencyLang.setId(currency.getId()));
        currencyLangService.saveBatch(currencyLangs);
    }

    @Override
    public IPage<CurrencyVO> pageCurrency(PageParam<CurrencyVO> pageParam, CurrencyDTO currencyDTO) {
        I18nLangInfo i18nLang = I18nMessage.getI18nLang();
        currencyDTO.setDefaultLangId(i18nLang.getCurrentLang().getLangId());
        return currencyMapper.pageCurrency(pageParam, currencyDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDefaultCurrency(Currency currency) {
//        update(new LambdaUpdateWrapper<Currency>().set(Currency::getDefaultCurrency,0).eq(Currency::getDefaultCurrency,1));
//        update(new LambdaUpdateWrapper<Currency>().set(Currency::getDefaultCurrency,1).eq(Currency::getId,currency.getId()));
        // 修改所有货币的汇率信息
        List<Currency> currencies = list();
        Currency defaultCurrency = getOne(new LambdaQueryWrapper<Currency>().ge(Currency::getDefaultCurrency,1));
        CurrencyVO nowCurrency = getCurrencyById(currency.getId());
        // 先计算和之前的默认货币汇率比例
        // 用 1 / 现在的默认货币的汇率 = 比例
        //然后所有货币用计算出的比例 * 原有比例 = 最终比例
        double rate = Arith.div(1, nowCurrency.getExchangeRate());
        for (Currency currencyDb : currencies) {
            if(Objects.equals(currencyDb.getId(),currency.getId())){
                currencyDb.setExchangeRate(1.0);
                currencyDb.setDefaultCurrency(1);
                continue;
            }
            currencyDb.setExchangeRate(Arith.mul(currencyDb.getExchangeRate(),rate));
            currencyDb.setDefaultCurrency(0);
        }
        updateBatchById(currencies);
    }

    @Override
    @CacheEvict(cacheNames = "enableCurrencies", key = "'enableCurrencies:' + '-' + #lang")
    public void removeEnableCurrencyCache(Integer lang) {

    }

}
