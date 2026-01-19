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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.dto.CurrencyDTO;
import com.yami.shop.bean.model.Currency;
import com.yami.shop.bean.vo.CurrencyVO;
import com.yami.shop.common.util.PageParam;

import java.util.List;

/**
 *
 * @author lhd
 */
public interface CurrencyService extends IService<Currency> {

    /**
     * 获取启用的货币信息
     * @return
     */
    List<CurrencyVO> listEnableCurrency();

    /**
     * 移除缓存
     * @param lang
     */
    void removeEnableCurrencyCache(Integer lang);

    /**
     * 获取选择的货币和默认货币汇率计算后的金额
     * @param price
     * @return
     */
    Double getSelectCurrencyAmount(Double price);

    /**
     * 获取货币详细信息
     * @param id
     * @return
     */
    CurrencyVO getCurrencyById(Long id);

    /**
     * 修改货币信息
     * @param currency
     */
    void updateCurrency(Currency currency);

    /**
     * 删除
     * @param id
     */
    void removeCurrency(Long id);

    /**
     * 保存
     * @param currency
     */
    void saveCurrency(Currency currency);

    /**
     * 分页获取信息
     * @param page
     * @param currencyDTO
     * @return
     */
    IPage<CurrencyVO> pageCurrency(PageParam<CurrencyVO> page, CurrencyDTO currencyDTO);

    /**
     * 更换默认货币
     * @param currency
     */
    void updateDefaultCurrency(Currency currency);
}
