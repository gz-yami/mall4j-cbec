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
import com.yami.shop.bean.model.ProdParameterLang;
import com.yami.shop.dao.ProdParameterLangMapper;
import com.yami.shop.service.ProdParameterLangService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 *
 * @author Citrus
 * @date 2021-11-02 11:26:08
 */
@Service
@AllArgsConstructor
public class ProdParameterLangServiceImpl extends ServiceImpl<ProdParameterLangMapper, ProdParameterLang> implements ProdParameterLangService {

    private final ProdParameterLangMapper prodParameterLangMapper;

    @Override
    public void insertBatch(List<ProdParameterLang> prodParameterLangList) {
        if (CollUtil.isEmpty(prodParameterLangList)) {
            return;
        }
        prodParameterLangMapper.insertBatch(prodParameterLangList);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        prodParameterLangMapper.deleteBatch(ids);
    }
}
