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
import com.yami.shop.bean.model.Category;
import com.yami.shop.bean.model.CategoryBrand;
import com.yami.shop.bean.model.CategoryLang;
import com.yami.shop.common.bean.LangConfig;
import com.yami.shop.common.bean.LangItemConfig;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.dao.CategoryBrandMapper;
import com.yami.shop.manager.impl.LangManager;
import com.yami.shop.service.CategoryBrandService;
import com.yami.shop.service.CategoryLangService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 *
 * @author lgh
 * @date 2018/07/13
 */
@Service
@AllArgsConstructor
public class CategoryBrandServiceImpl extends ServiceImpl<CategoryBrandMapper, CategoryBrand> implements CategoryBrandService {

    private final CategoryBrandMapper categoryBrandMapper;
    private final CategoryLangService categoryLangService;
    private final LangManager langManager;

    @Override
    public List<Long> getCategoryIdBrandId(Long brandId) {
        return categoryBrandMapper.getCategoryIdsByBrandId(brandId);
    }

    @Override
    public List<Category> getCategoryByBrandId(Long brandId) {
        List<Category> categoryVO = categoryBrandMapper.getCategoryByBrandId(brandId);
        loadCategoryList(categoryVO);
        return categoryVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveByCategoryIds(Long brandId, List<Long> categoryIds) {
        if (CollUtil.isEmpty(categoryIds)) {
            return;
        }
        List<CategoryBrand> categoryBrandList = new ArrayList<>();
        categoryIds.forEach(categoryId -> {
            CategoryBrand categoryBrand = new CategoryBrand();
            categoryBrand.setBrandId(brandId);
            categoryBrand.setCategoryId(categoryId);
            categoryBrandList.add(categoryBrand);
        });
        saveBatch(categoryBrandList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByCategoryIds(Long brandId, List<Long> categoryIds) {
        // 先删后增
        this.remove(new LambdaQueryWrapper<CategoryBrand>()
                .eq(CategoryBrand::getBrandId, brandId));
        if (!CollectionUtils.isEmpty(categoryIds)) {
            List<CategoryBrand> categoryBrands = new ArrayList<>(categoryIds.size());
            for (Long categoryId : categoryIds) {
                categoryBrands.add(new CategoryBrand(null, categoryId, brandId));
            }
            this.saveBatch(categoryBrands);
        }
    }

    @Override
    public void deleteByBrandId(Long brandId) {
        categoryBrandMapper.delete(new LambdaQueryWrapper<CategoryBrand>().eq(CategoryBrand::getBrandId, brandId));
    }

    private void loadCategoryList(List<Category> categories) {
        Integer lang = I18nMessage.getDbLang();
        LangConfig langConfig = langManager.getLangConfig();
        boolean defaultLang = Objects.equals(lang, langConfig.getLang());
        if (CollUtil.isEmpty(categories)) {
            return;
        }
        List<Long> categoryIds = categories.stream().map(Category::getCategoryId).collect(Collectors.toList());
        List<CategoryLang> list = categoryLangService.list(new LambdaQueryWrapper<CategoryLang>().in(CategoryLang::getCategoryId , categoryIds));
        Map<Long, List<CategoryLang>> categoryLangMap = list.stream().collect(Collectors.groupingBy(CategoryLang::getCategoryId));
        for (Category category : categories) {
            List<CategoryLang> categoryLangList = categoryLangMap.get(category.getCategoryId());
            if (CollUtil.isEmpty(categoryLangList)) {
                continue;
            }
            Map<Integer, CategoryLang> langMap = categoryLangList.stream().filter(categoryLang -> Objects.nonNull(categoryLang.getLang())).collect(Collectors.toMap(CategoryLang::getLang, c -> c));
            CategoryLang categoryLang = null;
            if (langMap.containsKey(lang)) {
                categoryLang = langMap.get(lang);
            }
            // 如果商品不包含当前语言，就尝试获取其他的语言信息
            else {
                // 优先默认语言
                if (!defaultLang && langMap.containsKey(langConfig.getLang())) {
                    categoryLang = langMap.get(langConfig.getLang());
                }
                // 如果没有默认语言，再按尝试从语言列表中的语言中显示商品语言
                else {
                    for (LangItemConfig langItemConfig : langConfig.getLangItemList()) {
                        if (langMap.containsKey(langItemConfig.getLang())) {
                            categoryLang = langMap.get(langItemConfig.getLang());
                            break;
                        }
                    }
                }
            }

            // 如果上面的步骤都无法获取到对应语言，就是该商品拥有的语言已经被删除，这里强制赋值一种
            if (Objects.isNull(categoryLang)) {
                categoryLang = categoryLangList.get(0);
            }
            category.setCategoryName(categoryLang.getCategoryName());
            category.setCategoryLangList(categoryLangList);
        }
    }
}
