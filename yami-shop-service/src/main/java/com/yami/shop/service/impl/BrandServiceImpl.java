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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.dto.BrandShopDTO;
import com.yami.shop.bean.enums.EsOperationType;
import com.yami.shop.bean.event.EsProductUpdateEvent;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.vo.CategoryVO;
import com.yami.shop.bean.vo.search.BrandSearchVO;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.enums.StatusEnum;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.common.util.PageAdapter;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.dao.BrandMapper;
import com.yami.shop.dao.ProductMapper;
import com.yami.shop.manager.impl.LangManager;
import com.yami.shop.service.BrandLangService;
import com.yami.shop.service.BrandService;
import com.yami.shop.service.CategoryBrandService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lgh
 * @date 2018/07/05
 */
@Service
@AllArgsConstructor
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    private final BrandMapper brandMapper;
    private final BrandLangService brandLangService;
    private final ProductMapper productMapper;
    private final CategoryBrandService categoryBrandService;
    private final ApplicationEventPublisher eventPublisher;
    private final LangManager langManager;


    @Override
    public IPage<Brand> page(PageParam<Brand> page, Brand brand) {
        List<Brand> brandList = brandMapper.list(new PageAdapter(page), brand);
        langManager.getBrandLang(brandList);
        //设置对应的分类名称
        List<Long> categoryIds = brandList.stream().flatMap(o -> o.getCategoryIds().stream()).toList();
        Map<Long, CategoryLang> categoryLangMap = langManager.getCategoryLangMap(categoryIds);
        for (Brand bran : brandList) {
            for (CategoryVO category : bran.getCategories()) {
                category.setCategoryName(categoryLangMap.get(category.getCategoryId()).getCategoryName());
            }
        }
        page.setRecords(brandList);
        page.setTotal(brandMapper.listTotal(brand));
        return page;
    }

    @Override
    public List<Brand> listByParams(Brand brand) {
        List<Brand> brands = brandMapper.listByParams(brand);
        langManager.getBrandLang(brands);
        return brands;
    }

    @Override
    public IPage<Brand> pageAvailableBrandByCategoryIdAndBrandNameAndShopId(PageParam<Brand> page, Long categoryId, String brandName, Long shopId) {
        IPage<Brand> brandPage = brandMapper.pageSigningByShopIdAndBrandNameAndCategoryId(page, categoryId, brandName, shopId);
        langManager.getBrandLang(brandPage.getRecords());
        return brandPage;
    }

    @Override
    public void updateShopIdAndStatusByShopId(Long oldShopId, Integer status, Long newShopId) {
        brandMapper.updateShopIdAndStatusByShopId(oldShopId, status, newShopId);
    }

    @Override
    public Brand getByBrandId(Long brandId) {
        if (Objects.isNull(brandId) || Objects.equals(brandId, Constant.ZERO_LONG)) {
            return null;
        }
        Brand brand = brandMapper.getByBrandId(brandId);
        langManager.getBrandAndLang(brand);
        return brand;
    }

    @Override
    public Brand getInfo(Long brandId) {
        Brand brand = brandMapper.getByBrandId(brandId);
        if (Objects.isNull(brand)) {
            return new Brand();
        }
        langManager.getBrandAndLang(brand);
        List<Category> category = categoryBrandService.getCategoryByBrandId(brandId);
        if (CollUtil.isNotEmpty(category)) {
            brand.setCategories(BeanUtil.mapAsList(category, CategoryVO.class));
        }
        return brand;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBrand(Brand brand) {
        brand.setFirstLetter(brand.getFirstLetter().toUpperCase());
        brand.setStatus(StatusEnum.ENABLE.value());

        List<BrandLang> brandLangList = brand.getBrandLangList();
        checkBrand(brand.getBrandLangList());
        checkBrandName(brandLangList);
        save(brand);
        brandLangService.saveBrandLang(brand.getBrandLangList(), brand.getBrandId());
        if (CollUtil.isNotEmpty(brand.getCategoryIds())) {
            categoryBrandService.saveByCategoryIds(brand.getBrandId(), brand.getCategoryIds());
        }
    }

    /**
     * 检查品牌名称
     * @param brandLangList
     */
    private void checkBrandName(List<BrandLang> brandLangList) {
        // 查询国际化表品牌名是否重复
        List<String> names = brandLangList.stream().map(BrandLang::getName).collect(Collectors.toList());
        List<BrandLang> list = brandLangService.list(new LambdaQueryWrapper<BrandLang>().in(BrandLang::getName, names).ne(BrandLang::getBrandId, brandLangList.get(0).getBrandId()));
        //判断品牌名称是否重复
        if (!list.isEmpty()) {
            throw new YamiShopBindException("yami.brand.exception.brandNameRepeat");
        }
    }

    private void checkBrand(List<BrandLang> brandLangList) {
        Integer lang = langManager.getDefaultLang();
        boolean containsMaster = false;
        for (BrandLang brandLang : brandLangList) {
            if (Objects.equals(brandLang.getLang(), lang)) {
                containsMaster = true;
            }
            brandLang.setName(brandLang.getName().trim());
        }
        if (!containsMaster) {
            // 语言数据已更新，请重新录入商品信息
            throw new YamiShopBindException("yami.brand.exception.brandLangUpdate");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> updateBrand(Brand brand) {
        checkBrand(brand.getBrandLangList());
        checkBrandName(brand.getBrandLangList());
        brandMapper.updateById(brand);
        brandLangService.updateBrandLang(brand.getBrandLangList(), brand.getBrandId());
        categoryBrandService.updateByCategoryIds(brand.getBrandId(), brand.getCategoryIds());
        List<Long> prodIds = productMapper.listIdByBrandId(brand.getBrandId());
        eventPublisher.publishEvent(new EsProductUpdateEvent(null, prodIds, EsOperationType.UPDATE_BATCH));
        return prodIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> deleteById(Long brandId) {
        if (brandMapper.getUseNum(brandId) > 0) {
            throw new YamiShopBindException("yami.product.brand.not.delete");
        }
        brandMapper.deleteById(brandId);
        List<Long> prodIds = productMapper.listIdByBrandId(brandId);
        eventPublisher.publishEvent(new EsProductUpdateEvent(null, prodIds, EsOperationType.UPDATE_BATCH));
        brandLangService.remove(new LambdaQueryWrapper<BrandLang>().eq(BrandLang::getBrandId, brandId));
        categoryBrandService.deleteByBrandId(brandId);
        return prodIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> updateBrandStatus(Brand brand) {
        Brand dbBrand = brandMapper.getByBrandId(brand.getBrandId());
        if (Objects.isNull(dbBrand) || dbBrand.getStatus().equals(brand.getStatus())) {
            return null;
        }
        brandMapper.updateBrandStatus(brand);
        List<Long> prodIds = productMapper.listIdByBrandId(brand.getBrandId());
        // 查询出秒杀or团购的商品进行下线
        eventPublisher.publishEvent(new EsProductUpdateEvent(null, prodIds, EsOperationType.UPDATE_BATCH));
        return prodIds;
    }

//    @Override
//    @Cacheable(cacheNames = "brandList", key = "#categoryId + ':' + #lang")
//    public List<Brand> listByCategory(Long categoryId, Integer lang) {
//        List<Brand> brands = brandMapper.listByCategoryIdAndName(categoryId, null);
//        langManager.getBrandLang(brands);
//        return brands;
//    }
//
//    @Override
//    public void removeCache(List<Long> categoryIds) {
//        List<String> keys = new ArrayList<>();
//        if (CollUtil.isNotEmpty(categoryIds)) {
//            Set<Long> categoryIdSet = new HashSet<>(categoryIds);
//            for (Long categoryId : categoryIdSet) {
//                keys.add("brandList" + Constant.UNION + categoryId + Constant.COLON + LanguageEnum.LANGUAGE_ZH_CN.getLang());
//                keys.add("brandList" + Constant.UNION + categoryId + Constant.COLON + LanguageEnum.LANGUAGE_EN.getLang());
//            }
//        }
//        RedisUtil.del(keys);
//    }

    @Override
    public List<Brand> listByCategoryIdAndName(Long categoryId, String brandName) {
        List<Brand> brands = brandMapper.listByCategoryIdAndName(categoryId, brandName);
        langManager.getBrandLang(brands);
        return brands;
    }

    @Override
    public List<Brand> listByBrandIds(Set<Long> brandIds) {
        if (CollUtil.isEmpty(brandIds)) {
            return null;
        }
        List<Brand> brandList = brandMapper.listByBrandIds(brandIds);
        langManager.getBrandLang(brandList);
        return brandList;
    }

    @Override
    public List<BrandSearchVO> listEsBrandByBrandIds(List<Long> brandIds) {
        if (CollectionUtils.isEmpty(brandIds)) {
            return new ArrayList<>();
        }
        Integer lang = I18nMessage.getLang();
        return brandMapper.listEsBrandByBrandIds(lang, brandIds);
    }


    @Override
    public void insertBatchByBrandShopList(List<BrandShopDTO> brandShopList) {
        brandMapper.insertBatchByBrandShopList(brandShopList);
    }

}
