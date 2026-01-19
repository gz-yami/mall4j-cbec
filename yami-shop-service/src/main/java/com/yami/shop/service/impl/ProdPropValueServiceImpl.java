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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.yami.shop.bean.model.ProdPropValue;
import com.yami.shop.bean.model.ProdPropValueLang;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.dao.ProdPropValueLangMapper;
import com.yami.shop.dao.ProdPropValueMapper;
import com.yami.shop.manager.impl.LangManager;
import com.yami.shop.service.ProdPropValueService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 *
 * @author lgh
 * @date 2018/07/06
 */
@Service
@AllArgsConstructor
public class ProdPropValueServiceImpl extends ServiceImpl<ProdPropValueMapper, ProdPropValue> implements ProdPropValueService {

    private final ProdPropValueMapper prodPropValueMapper;
    private final ProdPropValueLangMapper prodPropValueLangMapper;
    private final LangManager langManager;


    @Override
    public List<ProdPropValue> propValueListByPropId(Long specId) {
        List<ProdPropValue> prodPropValues = prodPropValueMapper.propValueListByPropId(specId);
        langManager.getProdPropValueLang(prodPropValues);
        return prodPropValues;
    }

    @Override
    public void insertBatch(List<ProdPropValue> prodPropValues, Long propId) {

        for (ProdPropValue prodPropValue:prodPropValues){
            prodPropValue.setValueId(null);
            prodPropValue.setPropId(propId);
        }
        saveBatch(prodPropValues);

        saveLangBatch(prodPropValues);
    }

    private void saveLangBatch(List<ProdPropValue> prodPropValues) {
        List<ProdPropValueLang> prodPropValueLangList = Lists.newArrayList();
        for (ProdPropValue prodPropValue:prodPropValues){
            checkProdLang(prodPropValue.getProdPropValueLangList());

            for (ProdPropValueLang prodPropValueLang : prodPropValue.getProdPropValueLangList()) {
                prodPropValueLang.setValueId(prodPropValue.getValueId());
            }
            prodPropValueLangList.addAll(prodPropValue.getProdPropValueLangList());
        }
        prodPropValueLangMapper.insetBatch(prodPropValueLangList);
    }

    private void checkProdLang(List<ProdPropValueLang> prodPropValueLangList) {
        boolean containsMaster = false;
        Integer lang = langManager.getDefaultLang();
        for (ProdPropValueLang prodPropValueLang : prodPropValueLangList) {
            if (Objects.equals(prodPropValueLang.getLang(), lang)) {
                containsMaster = true;
            }
            prodPropValueLang.setPropValue(prodPropValueLang.getPropValue().trim());
        }
        if (!containsMaster) {
            // 语言数据已更新，请重新录入商品信息
            throw new YamiShopBindException("yami.prod.prod.value.exception.langUpdate");
        }
    }

    @Override
    public void updatePropValues(Long propId, List<ProdPropValue> prodPropValues) {
        // 先删除原有的属性值，再添加新的属性值
        List<ProdPropValue> propValueList = prodPropValueMapper.propValueListByPropId(propId);
        if (CollUtil.isNotEmpty(propValueList)) {
            List<Long> valueIds = propValueList.stream().map(ProdPropValue::getValueId).collect(Collectors.toList());
            prodPropValueMapper.deleteByPropId(propId);
            prodPropValueLangMapper.delete(new LambdaQueryWrapper<ProdPropValueLang>().in(ProdPropValueLang::getValueId, valueIds));
        }
        insertBatch(prodPropValues,propId);
    }

    @Override
    public void deleteByPropId(Long propId) {
        //先删除语言表的数据
        prodPropValueLangMapper.deleteByPropId(propId);
        prodPropValueMapper.deleteByPropId(propId);
    }
}
