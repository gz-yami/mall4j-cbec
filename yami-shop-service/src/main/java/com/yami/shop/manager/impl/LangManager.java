/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.manager.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yami.shop.bean.app.dto.ProductDto;
import com.yami.shop.bean.dto.DerivativeCategoryDto;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.vo.*;
import com.yami.shop.common.bean.LangConfig;
import com.yami.shop.common.bean.LangItemConfig;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.service.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 统一语言工具
 *
 * @author LGH
 */
@Service
@AllArgsConstructor
public class LangManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ProdLangService prodLangService;
    private SkuLangService skuLangService;
    private BrandLangService brandLangService;
    private CategoryLangService categoryLangService;
    private ProdPropLangService prodPropLangService;
    private ProdPropValueLangService prodPropValueLangService;
    private ProdParameterLangService prodParameterLangService;
    private final LangService langService;

    /**
     * ======================================= 商品 =======================================
     */

    public void getProdLang(List<Product> prodList) {
        if (CollUtil.isEmpty(prodList)) {
            return;
        }
        getProdAndLang(prodList);
        for (Product product : prodList) {
            product.setProdLangList(null);
            if (CollUtil.isEmpty(product.getSkuList())) {
                continue;
            }
            for (Sku sku : product.getSkuList()) {
                sku.setSkuLangList(null);
            }
        }
    }

    public void getProdAndLang(Product product) {
        getProdAndLang(Collections.singletonList(product));
    }

    public void getProdAndLang(List<Product> prodList) {
        if (CollUtil.isEmpty(prodList)) {
            return;
        }
        List<Sku> skuList = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        for (Product product : prodList) {
            ids.add(product.getProdId());
            if (CollUtil.isEmpty(product.getSkuList())) {
                continue;
            }
            skuList.addAll(product.getSkuList());
        }
        LangConfig langConfig = this.getLangConfig();

        List<ProdLang> list = prodLangService.list(new LambdaQueryWrapper<ProdLang>().in(ProdLang::getProdId , ids));
        Map<Long, List<ProdLang>> langMap = list.stream().collect(Collectors.groupingBy(ProdLang::getProdId));
        for (Product product : prodList) {
            List<ProdLang> langList = langMap.get(product.getProdId());
            if (CollUtil.isEmpty(langList)) {
                continue;
            }
            Map<Integer, ProdLang> map = langList.stream().filter(item -> Objects.nonNull(item.getLang())).collect(Collectors.toMap(ProdLang::getLang, o -> o));
            ProdLang prodLang = map.get(getLang(map.keySet(), langConfig, I18nMessage.getLang()));
            product.setProdName(prodLang.getProdName());
            product.setBrief(prodLang.getBrief());
            product.setContent(prodLang.getContent());
            product.setProdLangList(langList);
        }

        if (CollUtil.isNotEmpty(skuList)) {
            this.getSkuAndLang(skuList);
        }
    }

    public void getProdDtoLang(List<ProductDto> prodList) {
        if (CollUtil.isEmpty(prodList)) {
            return;
        }
        List<Long> ids = new ArrayList<>();
        for (ProductDto product : prodList) {
            ids.add(product.getProdId());
        }

        Map<Long, ProdLang> prodLangMap = this.getProdLangMap(ids);
        for (ProductDto product : prodList) {
            ProdLang prodLang = prodLangMap.get(product.getProdId());
            product.setProdName(prodLang.getProdName());
            product.setBrief(prodLang.getBrief());
//            product.setContent(prodLang.getContent());
        }
    }

    private ProdLang getProdLang(LangConfig langConfig, List<ProdLang> prodLangList, Integer lang) {
        if (CollUtil.isEmpty(prodLangList)) {
            return null;
        }
        Map<Integer, ProdLang> map = prodLangList.stream().filter(item -> Objects.nonNull(item.getLang())).collect(Collectors.toMap(ProdLang::getLang, o -> o));
        ProdLang prodLang = map.get(getLang(map.keySet(), langConfig, lang));
        return prodLang;
    }

    public Map<Long, ProdLang> getProdLangMap(List<Long> prodIds) {
        Map<Long, ProdLang> prodMap = new HashMap<>(16);
        if (CollUtil.isEmpty(prodIds)) {
            return prodMap;
        }
        LangConfig langConfig = this.getLangConfig();

        List<ProdLang> list = prodLangService.list(new LambdaQueryWrapper<ProdLang>().in(ProdLang::getProdId , prodIds));
        Map<Long, List<ProdLang>> langMap = list.stream().collect(Collectors.groupingBy(ProdLang::getProdId));
        for (Long prodId : prodIds) {
            List<ProdLang> langList = langMap.get(prodId);
            if (CollUtil.isEmpty(langList)) {
                continue;
            }
            Map<Integer, ProdLang> map = langList.stream().filter(item -> Objects.nonNull(item.getLang())).collect(Collectors.toMap(ProdLang::getLang, o -> o));
            ProdLang prodLang = map.get(getLang(map.keySet(), langConfig, I18nMessage.getLang()));
            prodMap.put(prodId, prodLang);
        }
        return prodMap;
    }


    public void handleProdLang(Product product) {
        handleProdLang(Collections.singletonList(product));
    }

    public void handleProdLang(List<Product> prodList) {
        if (CollUtil.isEmpty(prodList)) {
            return;
        }
        Integer lang = I18nMessage.getLang();
        LangConfig langConfig = this.getLangConfig();
        for (Product product : prodList) {
            if (CollUtil.isEmpty(product.getProdLangList())) {
                continue;
            }
            ProdLang prodLang = getProdLang(langConfig, product.getProdLangList(), lang);
            product.setProdName(prodLang.getProdName());
            product.setBrief(prodLang.getBrief());
            product.setContent(prodLang.getContent());
            if (CollUtil.isEmpty(product.getSkuList())) {
                continue;
            }
            for (Sku sku : product.getSkuList()) {
                if (CollUtil.isEmpty(sku.getSkuLangList())) {
                    continue;
                }
                SkuLang skuLang = getSkuLang(langConfig, sku.getSkuLangList(), lang);
                sku.setSkuName(skuLang.getSkuName());
                sku.setProdName(skuLang.getProdName());
                sku.setProperties(skuLang.getProperties());
            }
        }

    }


    /**
     * ======================================= sku =======================================
     */

    public void getSkuLang(List<Sku> skuList) {
        if (CollUtil.isEmpty(skuList)) {
            return;
        }
        this.getSkuAndLang(skuList);
        for (Sku sku : skuList) {
            sku.setSkuLangList(null);
        }
    }

    public void getSkuAndLang(Sku sku) {
        this.getSkuAndLang(Collections.singletonList(sku));
    }

    public void getSkuAndLang(List<Sku> skuList) {
        if (CollUtil.isEmpty(skuList)) {
            return;
        }
        LangConfig langConfig = this.getLangConfig();

        List<Long> ids = skuList.stream().map(Sku::getSkuId).collect(Collectors.toList());
        List<SkuLang> list = skuLangService.list(new LambdaQueryWrapper<SkuLang>().in(SkuLang::getSkuId , ids));
        Map<Long, List<SkuLang>> langMap = list.stream().collect(Collectors.groupingBy(SkuLang::getSkuId));
        for (Sku sku : skuList) {
            List<SkuLang> langList = langMap.get(sku.getSkuId());
            if (CollUtil.isEmpty(langList)) {
                continue;
            }
            Map<Integer, SkuLang> map = langList.stream().filter(item -> Objects.nonNull(item.getLang())).collect(Collectors.toMap(SkuLang::getLang, o -> o));
            SkuLang skuLang = map.get(getLang(map.keySet(), langConfig, I18nMessage.getLang()));
            sku.setSkuName(skuLang.getSkuName());
            sku.setProperties(skuLang.getProperties());
            sku.setSkuLangList(langList);
        }
    }

    public void getProdNameToSku(Sku sku) {
        this.getProdNameToSku(Collections.singletonList(sku));
    }

    public void getProdNameToSku(List<Sku> skuList) {
        if (CollUtil.isEmpty(skuList)) {
            return;
        }
        LangConfig langConfig = this.getLangConfig();

        List<Long> ids = skuList.stream().map(Sku::getProdId).collect(Collectors.toList());
        List<ProdLang> list = prodLangService.list(new LambdaQueryWrapper<ProdLang>().in(ProdLang::getProdId , ids));
        Map<Long, List<ProdLang>> langMap = list.stream().collect(Collectors.groupingBy(ProdLang::getProdId));
        for (Sku sku : skuList) {
            List<ProdLang> langList = langMap.get(sku.getProdId());
            if (CollUtil.isEmpty(langList)) {
                continue;
            }
            Map<Integer, ProdLang> map = langList.stream().filter(item -> Objects.nonNull(item.getLang())).collect(Collectors.toMap(ProdLang::getLang, o -> o));
            ProdLang prodLang = map.get(getLang(map.keySet(), langConfig, I18nMessage.getLang()));
            sku.setProdName(prodLang.getProdName());
        }
    }


    public Map<Long, SkuLang> getSkuLangMap(List<Long> skuIds) {
        Map<Long, SkuLang> skuMap = new HashMap<>(16);
        if (CollUtil.isEmpty(skuIds)) {
            return skuMap;
        }
        LangConfig langConfig = this.getLangConfig();

        List<SkuLang> list = skuLangService.list(new LambdaQueryWrapper<SkuLang>().in(SkuLang::getSkuId , skuIds));
        Map<Long, List<SkuLang>> langMap = list.stream().collect(Collectors.groupingBy(SkuLang::getSkuId));
        for (Long skuId : skuIds) {
            List<SkuLang> langList = langMap.get(skuId);
            if (CollUtil.isEmpty(langList)) {
                continue;
            }
            Map<Integer, SkuLang> map = langList.stream().filter(item -> Objects.nonNull(item.getLang())).collect(Collectors.toMap(SkuLang::getLang, o -> o));
            SkuLang skuLang = map.get(getLang(map.keySet(), langConfig, I18nMessage.getLang()));
            skuMap.put(skuId, skuLang);
        }
        return skuMap;
    }

    private SkuLang getSkuLang(LangConfig langConfig, List<SkuLang> skuLangList, Integer lang) {
        if (CollUtil.isEmpty(skuLangList)) {
            return null;
        }
        Map<Integer, SkuLang> map = skuLangList.stream().filter(item -> Objects.nonNull(item.getLang())).collect(Collectors.toMap(SkuLang::getLang, o -> o));
        SkuLang skuLang = map.get(getLang(map.keySet(), langConfig, lang));
        return skuLang;
    }

    public void handleSkuLang(List<Sku> skuList) {
        if (CollUtil.isEmpty(skuList)) {
            return;
        }
        handleSkuAndLang(skuList);
        for (Sku sku : skuList) {
            sku.setSkuLangList(null);
        }
    }

    public void handleSkuAndLang(Sku sku) {
        this.handleSkuAndLang(Collections.singletonList(sku));
    }

    public void handleSkuAndLang(List<Sku> skuList) {
        if (CollUtil.isEmpty(skuList)) {
            return;
        }
        Integer lang = I18nMessage.getLang();
        LangConfig langConfig = this.getLangConfig();
        for (Sku sku : skuList) {
            if (CollUtil.isEmpty(sku.getSkuLangList())) {
                continue;
            }
            SkuLang skuLang = getSkuLang(langConfig, sku.getSkuLangList(), lang);
            sku.setSkuName(skuLang.getSkuName());
            sku.setProdName(skuLang.getProdName());
            sku.setProperties(skuLang.getProperties());
        }
    }

    /**
     * ======================================= 分类 =======================================
     */

    public void getCategoryLang(List<Category> categories) {
        if (CollUtil.isEmpty(categories)) {
            return;
        }
        getCategoryAndLang(categories);
        for (Category category : categories) {
            category.setCategoryLangList(null);
        }
    }

    public void getCategoryAndLang(Category category) {
        getCategoryAndLang(Collections.singletonList(category));
    }

    public void getCategoryAndLang(List<Category> categories) {
        if (CollUtil.isEmpty(categories)) {
            return;
        }
        Integer lang = I18nMessage.getLang();
        LangConfig langConfig = this.getLangConfig();

        List<Long> ids = categories.stream().map(Category::getCategoryId).collect(Collectors.toList());
        List<CategoryLang> list = categoryLangService.list(new LambdaQueryWrapper<CategoryLang>().in(CategoryLang::getCategoryId , ids));
        Map<Long, List<CategoryLang>> langMap = list.stream().collect(Collectors.groupingBy(CategoryLang::getCategoryId));
        for (Category category : categories) {
            List<CategoryLang> categoryLangList = langMap.get(category.getCategoryId());
            if (CollUtil.isEmpty(categoryLangList)) {
                continue;
            }
            CategoryLang categoryLang = getCategoryLang(langConfig, categoryLangList, category.getCategoryId(), lang);
            category.setCategoryName(categoryLang.getCategoryName());
            category.setCategoryLangList(categoryLangList);
        }
    }

    public void getCategoryShopVoAndLang(List<CategoryShopVO> categories) {
        if (CollUtil.isEmpty(categories)) {
            return;
        }
        LangConfig langConfig = this.getLangConfig();
        Integer lang = I18nMessage.getLang();
        List<Long> categoryIds = new ArrayList<>();
        for (CategoryShopVO category : categories) {
            categoryIds.add(category.getCategoryId());
            categoryIds.add(category.getParentId());
        }
        List<CategoryLang> list = categoryLangService.list(new LambdaQueryWrapper<CategoryLang>().in(CategoryLang::getCategoryId , categoryIds));
        Map<Long, List<CategoryLang>> langMap = list.stream().collect(Collectors.groupingBy(CategoryLang::getCategoryId));
        for (CategoryShopVO category : categories) {
            CategoryLang categoryLang = getCategoryLang(langConfig, langMap, category.getCategoryId(),lang);
            if (Objects.nonNull(categoryLang)) {
                category.setName(categoryLang.getCategoryName());
                category.setCategoryLangList(langMap.get(category.getCategoryId()));
            }

            CategoryLang parentCategoryLang = getCategoryLang(langConfig, langMap, category.getParentId(), lang);
            if (Objects.nonNull(parentCategoryLang)) {
                category.setParentName(parentCategoryLang.getCategoryName());
                category.setParentCategoryLangList(langMap.get(category.getParentId()));
            }
        }
    }
    public void handleCategoryShopVoLang(List<CategoryShopVO> categories) {
        if (CollUtil.isEmpty(categories)) {
            return;
        }
        Integer lang = I18nMessage.getLang();
        LangConfig langConfig = this.getLangConfig();
        for (CategoryShopVO category : categories) {
            if (CollUtil.isNotEmpty(category.getCategoryLangList())) {
                CategoryLang categoryLang = getCategoryLang(langConfig, category.getCategoryLangList(), lang);
                category.setName(categoryLang.getCategoryName());
                category.setCategoryLangList(null);
            }
            if (CollUtil.isNotEmpty(category.getParentCategoryLangList())) {
                CategoryLang categoryLang = getCategoryLang(langConfig, category.getParentCategoryLangList(), lang);
                category.setParentName(categoryLang.getCategoryName());
                category.setParentCategoryLangList(null);
            }
        }
    }

    private CategoryLang getCategoryLang(LangConfig langConfig, Map<Long, List<CategoryLang>> langMap, Long categoryId, Integer lang) {
        List<CategoryLang> langList = langMap.get(categoryId);
        if (CollUtil.isEmpty(langList)) {
            return null;
        }
        Map<Integer, CategoryLang> map = langList.stream().filter(item -> Objects.nonNull(item.getLang())).collect(Collectors.toMap(CategoryLang::getLang, o -> o));
        CategoryLang categoryLang = map.get(getLang(map.keySet(), langConfig, lang));
        return categoryLang;
    }

    public CategoryLang getCategoryLang(LangConfig langConfig, List<CategoryLang> categoryLangList, Long categoryId, Integer lang) {
        if (CollUtil.isEmpty(categoryLangList)) {
            return null;
        }
        Map<Integer, CategoryLang> map = categoryLangList.stream().filter(item -> Objects.nonNull(item.getLang())).collect(Collectors.toMap(CategoryLang::getLang, o -> o));
        CategoryLang categoryLang = map.get(getLang(map.keySet(), langConfig, lang));
        return categoryLang;
    }

    public Map<Long, CategoryLang> getCategoryLangMap(List<Long> categoryIds) {
        Map<Long, CategoryLang> categoryMap = new HashMap<>(16);
        if (CollUtil.isEmpty(categoryIds)) {
            return categoryMap;
        }
        LangConfig langConfig = this.getLangConfig();

        List<CategoryLang> list = categoryLangService.list(new LambdaQueryWrapper<CategoryLang>().in(CategoryLang::getCategoryId , categoryIds));
        Map<Long, List<CategoryLang>> langMap = list.stream().collect(Collectors.groupingBy(CategoryLang::getCategoryId));
        for (Long categoryId : categoryIds) {
            List<CategoryLang> langList = langMap.get(categoryId);
            if (CollUtil.isEmpty(langList)) {
                continue;
            }
            Map<Integer, CategoryLang> map = langList.stream().filter(item -> Objects.nonNull(item.getLang())).collect(Collectors.toMap(CategoryLang::getLang, o -> o));
            CategoryLang categoryLang = map.get(getLang(map.keySet(), langConfig, I18nMessage.getLang()));
            categoryMap.put(categoryId, categoryLang);
        }
        return categoryMap;
    }

    public void handleCategoryLang(List<Category> categories) {
        if (CollUtil.isEmpty(categories)) {
            return;
        }
        LangConfig langConfig = this.getLangConfig();
        Integer lang = I18nMessage.getLang();
        for (Category category : categories) {
            if (CollUtil.isNotEmpty(category.getCategoryLangList())) {
                CategoryLang categoryLang = getCategoryLang(langConfig, category.getCategoryLangList(), lang);
                category.setCategoryName(categoryLang.getCategoryName());
                category.setCategoryLangList(null);
            }
            if (CollUtil.isEmpty(category.getCategories())) {
                continue;
            }
            for (DerivativeCategoryDto derivativeCategory : category.getCategories()) {
                CategoryLang categoryLang = getCategoryLang(langConfig, derivativeCategory.getCategoryLangList(), lang);
                derivativeCategory.setCategoryName(categoryLang.getCategoryName());
                derivativeCategory.setCategoryLangList(null);
            }
        }
    }

    private CategoryLang getCategoryLang(LangConfig langConfig, List<CategoryLang> categoryLangList, Integer lang) {
        if (CollUtil.isEmpty(categoryLangList)) {
            return null;
        }
        Map<Integer, CategoryLang> map = categoryLangList.stream().filter(item -> Objects.nonNull(item.getLang())).collect(Collectors.toMap(CategoryLang::getLang, o -> o));
        CategoryLang categoryLang = map.get(getLang(map.keySet(), langConfig, lang));
        return categoryLang;
    }



    /**
     * ======================================= 品牌 =======================================
     */

    public void getBrandLang(List<Brand> brandList) {
        if (CollUtil.isEmpty(brandList)) {
            return;
        }
        getBrandAndLang(brandList);
        for (Brand brand : brandList) {
            brand.setBrandLangList(null);
        }
    }

    public void getBrandAndLang(Brand brand) {
        getBrandAndLang(Collections.singletonList(brand));
    }

    public void getBrandAndLang(List<Brand> brands) {
        if (CollUtil.isEmpty(brands)) {
            return;
        }
        LangConfig langConfig = this.getLangConfig();

        List<Long> ids = brands.stream().map(Brand::getBrandId).collect(Collectors.toList());
        List<BrandLang> list = brandLangService.list(new LambdaQueryWrapper<BrandLang>().in(BrandLang::getBrandId , ids));
        Map<Long, List<BrandLang>> langMap = list.stream().collect(Collectors.groupingBy(BrandLang::getBrandId));
        for (Brand brand : brands) {
            List<BrandLang> langList = langMap.get(brand.getBrandId());
            if (CollUtil.isEmpty(langList)) {
                continue;
            }
            Map<Integer, BrandLang> map = langList.stream().filter(item -> Objects.nonNull(item.getLang())).collect(Collectors.toMap(BrandLang::getLang, o -> o));
            BrandLang brandLang = map.get(getLang(map.keySet(), langConfig, I18nMessage.getLang()));
            brand.setName(brandLang.getName());
            brand.setBrandLangList(langList);
        }
    }


    public void getBrandShopVoAndLang(List<BrandShopVO> brands) {
        LangConfig langConfig = this.getLangConfig();
        if (CollUtil.isEmpty(brands)) {
            return;
        }
        List<Long> ids = brands.stream().map(BrandShopVO::getBrandId).collect(Collectors.toList());
        List<BrandLang> list = brandLangService.list(new LambdaQueryWrapper<BrandLang>().in(BrandLang::getBrandId , ids));
        Map<Long, List<BrandLang>> brandLangMap = list.stream().collect(Collectors.groupingBy(BrandLang::getBrandId));
        for (BrandShopVO brand : brands) {
            List<BrandLang> brandLangList = brandLangMap.get(brand.getBrandId());
            if (CollUtil.isEmpty(brandLangList)) {
                continue;
            }
            Map<Integer, BrandLang> map = brandLangList.stream().filter(item -> Objects.nonNull(item.getLang())).collect(Collectors.toMap(BrandLang::getLang, o -> o));
            BrandLang brandLang = map.get(getLang(map.keySet(), langConfig, I18nMessage.getLang()));
            brand.setName(brandLang.getName());
        }
    }



    /**
     * ======================================= 规格 =======================================
     */

    public void getProdPropAndLang(List<ProdProp> prodProps) {
        if (CollUtil.isEmpty(prodProps)) {
            return;
        }
        LangConfig langConfig = this.getLangConfig();

        List<Long> ids = prodProps.stream().map(ProdProp::getPropId).collect(Collectors.toList());
        List<ProdPropLang> list = prodPropLangService.list(new LambdaQueryWrapper<ProdPropLang>().in(ProdPropLang::getPropId , ids));
        Map<Long, List<ProdPropLang>> langMap = list.stream().collect(Collectors.groupingBy(ProdPropLang::getPropId));
        for (ProdProp prodProp : prodProps) {
            List<ProdPropLang> langList = langMap.get(prodProp.getPropId());
            if (CollUtil.isEmpty(langList)) {
                continue;
            }
            Map<Integer, ProdPropLang> map = langList.stream().filter(item -> Objects.nonNull(item.getLang())).collect(Collectors.toMap(ProdPropLang::getLang, o -> o));
            ProdPropLang prodPropLang = map.get(getLang(map.keySet(), langConfig, I18nMessage.getLang()));
            prodProp.setPropName(prodPropLang.getPropName());
            prodProp.setProdPropLangList(langList);
        }
    }

    public void getProdPropAndValueLang(List<ProdProp> prodPropList) {
        if (CollUtil.isEmpty(prodPropList)) {
            return;
        }
        List<ProdPropValue> prodPropValueList = new ArrayList<>();
        for (ProdProp prodProp : prodPropList) {
            if (CollUtil.isEmpty(prodProp.getProdPropValues())) {
                continue;
            }
            prodPropValueList.addAll(prodProp.getProdPropValues());
        }
        this.getProdPropAndLang(prodPropList);
        this.getProdPropValueAndLang(prodPropValueList);
    }

    /**
     * ======================================= 规格值 =======================================
     */
    public void getProdPropValueLang(List<ProdPropValue> prodPropValues) {
        this.getProdPropValueAndLang(prodPropValues);
        for (ProdPropValue prodPropValue : prodPropValues) {
            prodPropValue.setProdPropValueLangList(null);
        }
    }

    private void getProdPropValueAndLang(List<ProdPropValue> prodPropValues) {
        if (CollUtil.isEmpty(prodPropValues)) {
            return;
        }
        LangConfig langConfig = this.getLangConfig();

        List<Long> ids = prodPropValues.stream().map(ProdPropValue::getValueId).collect(Collectors.toList());
        List<ProdPropValueLang> list = prodPropValueLangService.list(new LambdaQueryWrapper<ProdPropValueLang>().in(ProdPropValueLang::getValueId , ids));
        Map<Long, List<ProdPropValueLang>> langMap = list.stream().collect(Collectors.groupingBy(ProdPropValueLang::getValueId));
        for (ProdPropValue prodPropValue : prodPropValues) {
            List<ProdPropValueLang> langList = langMap.get(prodPropValue.getValueId());
            if (CollUtil.isEmpty(langList)) {
                continue;
            }
            Map<Integer, ProdPropValueLang> map = langList.stream().filter(item -> Objects.nonNull(item.getLang())).collect(Collectors.toMap(ProdPropValueLang::getLang, o -> o));
            ProdPropValueLang prodPropValueLang = map.get(getLang(map.keySet(), langConfig, I18nMessage.getLang()));
            prodPropValue.setPropValue(prodPropValueLang.getPropValue());
            prodPropValue.setProdPropValueLangList(langList);
        }
    }


    /**
     * ======================================= 商品参数 =======================================
     */

    public void getProdParameterAndLang(List<ProdParameter> prodParameters) {
        if (CollUtil.isEmpty(prodParameters)) {
            return;
        }
        LangConfig langConfig = this.getLangConfig();

        List<Long> ids = prodParameters.stream().map(ProdParameter::getProdParameterId).collect(Collectors.toList());
        List<ProdParameterLang> list = prodParameterLangService.list(new LambdaQueryWrapper<ProdParameterLang>().in(ProdParameterLang::getProdParameterId , ids));
        Map<Long, List<ProdParameterLang>> langMap = list.stream().collect(Collectors.groupingBy(ProdParameterLang::getProdParameterId));
        for (ProdParameter prodParameter : prodParameters) {
            List<ProdParameterLang> langList = langMap.get(prodParameter.getProdParameterId());
            if (CollUtil.isEmpty(langList)) {
                continue;
            }
            Map<Integer, ProdParameterLang> map = langList.stream().filter(item -> Objects.nonNull(item.getLang())).collect(Collectors.toMap(ProdParameterLang::getLang, o -> o));
            ProdParameterLang prodParameterLang = map.get(getLang(map.keySet(), langConfig, I18nMessage.getLang()));
            prodParameter.setParameterKey(prodParameterLang.getParameterKey());
            prodParameter.setParameterValue(prodParameterLang.getParameterValue());
            prodParameter.setProdParameterLangList(langList);
        }
    }

    public void handleProdParameterLang(List<ProdParameter> prodParameters) {
        if (CollUtil.isEmpty(prodParameters)) {
            return;
        }
        Integer lang = I18nMessage.getLang();
        LangConfig langConfig = this.getLangConfig();
        for (ProdParameter prodParameter : prodParameters) {
            if (CollUtil.isEmpty(prodParameter.getProdParameterLangList())) {
                continue;
            }
            Map<Integer, ProdParameterLang> parameterLangMap = prodParameter.getProdParameterLangList().stream().filter(prodParameterLang -> Objects.nonNull(prodParameterLang.getLang())).collect(Collectors.toMap(ProdParameterLang::getLang, p -> p));
            ProdParameterLang prodParameterLang = parameterLangMap.get(getLang(parameterLangMap.keySet(), langConfig, lang));
            prodParameter.setParameterKey(prodParameterLang.getParameterKey());
            prodParameter.setParameterValue(prodParameterLang.getParameterValue());
        }
    }


    /**
     * 获取默认语言
     * @return
     */
    public Integer getDefaultLang() {
        LangConfig lang = this.getLangConfig();
        return lang.getLang();
    }

    public Integer getLang(Set<Integer> langSet) {
        Integer lang = I18nMessage.getLang();
        LangConfig langConfig = getLangConfig();
        return getLang(langSet, langConfig, lang);
    }

    public Integer getLang(Set<Integer> langSet, LangConfig langConfig, Integer lang) {
        boolean defaultLang = Objects.equals(lang, langConfig.getLang());
        if (langSet.contains(lang)) {
            return lang;
        }
        // 如果不包含当前语言，就尝试获取其他的语言信息
        else {
            // 优先默认语言
            if (!defaultLang && langSet.contains(langConfig.getLang())) {
                return langConfig.getLang();
            }
            // 如果没有默认语言，再按尝试从语言列表中或者语言
            else {
                for (LangItemConfig langItemConfig : langConfig.getLangItemList()) {
                    if (langSet.contains(langItemConfig.getLang())) {
                        return langItemConfig.getLang();
                    }
                }
            }
        }

        // 排序，防止每次获取的语言都不一致
        List<Integer> langList = new ArrayList<>(langSet);
        Collections.sort(langList);
        // 如果上面的步骤都无法获取到对应语言，就是该商品拥有的语言已经被删除，这里强制赋值一种
        return langList.get(0);
    }
    public LangConfig getLangConfig() {
        DefaultLangVO defaultLang = langService.getDefaultLang();
        LangConfig langConfig = new LangConfig();
        BeanUtils.copyProperties(defaultLang, langConfig);
        if (!CollectionUtils.isEmpty(defaultLang.getLangItemList())) {
            List<LangItemConfig> langItemConfigs = defaultLang.getLangItemList().stream().map(x -> {
                LangItemConfig langItemConfig = new LangItemConfig();
                BeanUtils.copyProperties(x, langItemConfig);
                return langItemConfig;
            }).collect(Collectors.toList());
            langConfig.setLangItemList(langItemConfigs);
        }
        return langConfig;
    }
}
