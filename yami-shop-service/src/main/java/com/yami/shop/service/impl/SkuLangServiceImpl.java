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
import com.yami.shop.bean.model.SkuLang;
import com.yami.shop.dao.SkuLangMapper;
import com.yami.shop.service.SkuLangService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @author lhd
 * @date 2020-08-31 13:54:58
 */
@Service
@AllArgsConstructor
public class SkuLangServiceImpl extends ServiceImpl<SkuLangMapper, SkuLang> implements SkuLangService {

    private final SkuLangMapper skuLangMapper;

}
