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

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.CurrencyLang;
import com.yami.shop.dao.CurrencyLangMapper;
import com.yami.shop.service.CurrencyLangService;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 货币语言表
 * @author LGH
 */
@Service
@AllArgsConstructor
public class CurrencyLangServiceImpl extends ServiceImpl<CurrencyLangMapper, CurrencyLang> implements CurrencyLangService {

    private final CurrencyLangMapper currencyLangMapper;
}
