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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.enums.ProdPropRule;
import com.yami.shop.bean.model.ProdProp;
import com.yami.shop.bean.model.ProdPropLang;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.PageAdapter;
import com.yami.shop.dao.CategoryPropMapper;
import com.yami.shop.dao.ProdPropLangMapper;
import com.yami.shop.dao.ProdPropMapper;
import com.yami.shop.manager.impl.LangManager;
import com.yami.shop.service.ProdPropService;
import com.yami.shop.service.ProdPropValueService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author lgh
 * @date 2018/07/06
 */
@Service
@AllArgsConstructor
public class ProdPropServiceImpl extends ServiceImpl<ProdPropMapper, ProdProp> implements ProdPropService {

    private final ProdPropMapper prodPropMapper;
    private final ProdPropValueService prodPropValueService;
    private final CategoryPropMapper categoryPropMapper;
    private final ProdPropLangMapper prodPropLangMapper;
    private final LangManager langManager;


    @Override
    public IPage<ProdProp> pagePropAndValue(ProdProp prodProp, Page<ProdProp> page) {
        List<ProdProp> prodPropList = prodPropMapper.listPropAndValue(new PageAdapter(page), prodProp);
        langManager.getProdPropAndValueLang(prodPropList);
        page.setRecords(prodPropList);
        page.setTotal(prodPropMapper.countPropAndValue(prodProp));
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProdPropAndValues(ProdProp prodProp) {
        List<ProdProp> dbProdProp = prodPropMapper.getProdPropByPropNameAndShopId(prodProp.getPropName(),prodProp.getShopId(), prodProp.getRule());
        if (CollUtil.isNotEmpty(dbProdProp)) {
            // 已有相同名称规格
            throw new YamiShopBindException("yami.same.specifications");
        }
        prodPropMapper.insert(prodProp);
        // 规格语言表
        saveProdPropLangList(prodProp);
        if (CollUtil.isEmpty(prodProp.getProdPropValues())) {
            return;
        }
//        prodPropValueMapper.insertPropValues(prodProp.getPropId(), prodProp.getProdPropValues())
        prodPropValueService.insertBatch(prodProp.getProdPropValues(),prodProp.getPropId());
    }

    private void saveProdPropLangList(ProdProp prodProp) {
        checkProdLang(prodProp.getProdPropLangList());
        for (ProdPropLang prodPropLang : prodProp.getProdPropLangList()) {
            prodPropLang.setPropId(prodProp.getPropId());
        }
        prodPropLangMapper.insertBatch(prodProp.getProdPropLangList());
    }

    private void checkProdLang(List<ProdPropLang> prodPropLangList) {
        Integer lang = langManager.getDefaultLang();
        boolean containsMaster = false;
        for (ProdPropLang prodPropLang : prodPropLangList) {
            if (Objects.equals(prodPropLang.getLang(), lang)) {
                containsMaster = true;
            }
            prodPropLang.setPropName(prodPropLang.getPropName().trim());
        }
//        if (!containsMaster) {
//            // 语言数据已更新，请重新录入商品信息
//            throw new YamiShopBindException("yami.prod.prod.exception.langUpdate");
//        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProdPropAndValues(ProdProp prodProp) {
        List<ProdProp> dbProdProp = prodPropMapper.getProdPropByPropNameAndShopId(prodProp.getPropName(),prodProp.getShopId(), prodProp.getRule());
        for (ProdProp prop : dbProdProp) {
            if (dbProdProp != null && !Objects.equals(prodProp.getPropId(), prop.getPropId())) {
                // 已有相同名称规格
                throw new YamiShopBindException("yami.same.specifications");
            }
        }
        prodPropMapper.updateById(prodProp);
        // 更新规格语言表
        updateProdPropLang(prodProp);
        if (CollUtil.isEmpty(prodProp.getProdPropValues())) {
            return;
        }
        //更新规格值
        prodPropValueService.updatePropValues(prodProp.getPropId(), prodProp.getProdPropValues());
//        prodPropValueMapper.insertPropValues(prodProp.getPropId(), prodProp.getProdPropValues());
    }

    private void updateProdPropLang(ProdProp prodProp) {
        prodPropLangMapper.delete(new LambdaQueryWrapper<ProdPropLang>().eq(ProdPropLang::getPropId, prodProp.getPropId()));
        saveProdPropLangList(prodProp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProdPropAndValues(Long propId, Integer propRule, Long shopId) {
        int deleteRows = prodPropMapper.deleteByPropId(propId, propRule, shopId);
        // 商品规格语言表
        prodPropLangMapper.deleteByPropId(propId);
        if (deleteRows == 0) {
            return;
        }
        // 删除原有的属性值
        prodPropValueService.deleteByPropId(propId);
        // 如果是参数，删除参数与分类关联信息
        if (ProdPropRule.ATTRIBUTE.value().equals(propRule)) {
            categoryPropMapper.deleteByPropId(propId);
        }
    }

    @Override
    public List<ProdProp> listByCategoryId(Long categoryId) {
        return prodPropMapper.listByCategoryId(categoryId);
    }

    @Override
    public List<ProdProp> listByLang(ProdProp prodProp) {
        List<ProdProp> prodProps = prodPropMapper.listByLang(prodProp);
        langManager.getProdPropAndValueLang(prodProps);
        return prodProps;
    }
}
