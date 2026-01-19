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
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.yami.shop.bean.dto.DerivativeCategoryDto;
import com.yami.shop.bean.enums.CategoryLevel;
import com.yami.shop.bean.enums.EsOperationType;
import com.yami.shop.bean.event.EsProductUpdateEvent;
import com.yami.shop.bean.model.Category;
import com.yami.shop.bean.model.CategoryBrand;
import com.yami.shop.bean.model.CategoryLang;
import com.yami.shop.bean.model.Product;
import com.yami.shop.bean.param.CategoryScoreConfigParam;
import com.yami.shop.bean.param.ScoreConfigParam;
import com.yami.shop.bean.vo.CategoryVO;
import com.yami.shop.bean.vo.ParentCategoryVO;
import com.yami.shop.bean.vo.search.CategorySearchVO;
import com.yami.shop.common.bean.LangConfig;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.constants.ProductCacheNames;
import com.yami.shop.common.enums.StatusEnum;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.dao.CategoryBrandMapper;
import com.yami.shop.dao.CategoryMapper;
import com.yami.shop.dao.CategoryPropMapper;
import com.yami.shop.dao.ProductMapper;
import com.yami.shop.manager.impl.LangManager;
import com.yami.shop.service.CategoryLangService;
import com.yami.shop.service.CategoryService;
import com.yami.shop.service.SysConfigService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author LGH
 */
@Service
@AllArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{

    private final CategoryMapper categoryMapper;
    private final CategoryBrandMapper categoryBrandMapper;
    private final CategoryPropMapper categoryPropMapper;
    private final ProductMapper productMapper;
    private SysConfigService sysConfigService;
    private final CategoryLangService categoryLangService;
    private final ApplicationEventPublisher eventPublisher;
    private final LangManager langManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(cacheNames = "CategoryListByParentIdAndShopId", key = "#category.superiorId + ':' + #category.shopId"),
            @CacheEvict(cacheNames = "CategoryListByParentIdAndShopId", key = "#category.parentId + ':' + #category.shopId"),
            @CacheEvict(cacheNames = "CategoryListByShopId", key = "#category.shopId + ':category'")
    })
    public void saveCategroy(Category category) {
        category.setRecTime(new Date());
        // 保存分类信息
        categoryMapper.insert(category);
        saveCategoryLang(category);
        insertBrandsAndAttributes(category);

        if (Objects.equals(category.getParentId(),Constant.CATEGORY_ID) && Objects.equals(category.getShopId(),Constant.PLATFORM_SHOP_ID)){
            this.changeScoreConfig();
        }
    }

    private void saveCategoryLang(Category category) {
        checkCategoryLang(category.getCategoryLangList());

        for (CategoryLang categoryLang : category.getCategoryLangList()) {
            categoryLang.setCategoryId(category.getCategoryId());
        }
        categoryLangService.saveBatch(category.getCategoryLangList());
    }


    private void checkCategoryLang(List<CategoryLang> categoryLangList) {
        LangConfig configLang = langManager.getLangConfig();
        boolean containsMaster = false;
        for (CategoryLang categoryLang : categoryLangList) {
            if (Objects.equals(categoryLang.getLang(), configLang.getLang())) {
                containsMaster = true;
            }
            categoryLang.setCategoryName(categoryLang.getCategoryName().trim());
        }
        if (!containsMaster) {
            // 语言数据已更新，请重新录入商品信息
            throw new YamiShopBindException("yami.category.exception.langUpdate");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(cacheNames = "CategoryListByParentIdAndShopId", key = "#category.superiorId + ':' + #category.shopId"),
            @CacheEvict(cacheNames = "CategoryListByParentIdAndShopId", key = "#category.parentId + ':' + #category.shopId"),
            @CacheEvict(cacheNames = "CategoryListByShopId", key = "#category.shopId + ':category'")
    })
    public void updateCategroy(Category category) {
        Category categoryDb = categoryMapper.selectById(category.getCategoryId());
        category.setUpdateTime(new Date());
        Long shopId = categoryDb.getShopId();
        // 保存分类信息
        categoryMapper.updateById(category);
        // 更新分类语言表
        updateCategoryLang(category);
        if (Objects.equals(categoryDb.getStatus(),1) && Objects.equals(category.getStatus(),0)){
            offlineCategory(category, categoryDb, shopId);
        }
        //修改积分配置中的分类数据
        if (!Objects.equals(category.getCategoryName(),category.getOldCategoryName())){
            this.changeScoreConfig();
        }
        // 先删除后增加
        deleteBrandsAndAttributes(category.getCategoryId());
        insertBrandsAndAttributes(category);
    }

    private void offlineCategory(Category category, Category categoryDb, Long shopId) {
        List<Long> ids = new ArrayList<>();
        ids.add(category.getCategoryId());
        List<Category> categoryList = categoryMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getParentId, category.getCategoryId()));
        // 如果是平台且不是第三级分类的情况下，还需进行查找子集操作
        if (CollectionUtils.isNotEmpty(categoryList) && categoryDb.getGrade() < CategoryLevel.THIRD.value() && Objects.equals(shopId,Constant.PLATFORM_SHOP_ID)){
            categoryList.forEach(item -> item.setStatus(0));
            // 获取所有的下级分类,将他们的子集全部下架
            List<Long> offlineIds = new ArrayList<>();
            for (Category categoryByParentId: categoryList){
                categoryByParentId.setStatus(0);
                offlineIds.add(categoryByParentId.getCategoryId());
                ids.add(categoryByParentId.getCategoryId());
            }
            // 如果下架的是一级分类，还需进行查找子集操作
            if(Objects.equals(category.getParentId(), 0L)){
                List<Category> childCategories = list(new LambdaQueryWrapper<Category>().in(Category::getParentId, offlineIds));
                if(CollectionUtils.isNotEmpty(childCategories) && categoryDb.getGrade() < 1) {
                    for (Category childCategory : childCategories) {
                        ids.add(childCategory.getCategoryId());
                        childCategory.setStatus(0);
                    }
                    categoryList.addAll(childCategories);
                }
            }else{
                // 如果下架的是二级分类，直接放入下级分类id进行下架即可
                ids.addAll(offlineIds);
            }
            updateBatchById(categoryList);
        }
        // 从正常改为下线,批量修改已经下架的分类下的商品的状态
        if(CollectionUtils.isNotEmpty(ids)) {
            //从正常改为下线
            productMapper.offlineProdByCategoryId(ids, shopId);
            List<Product> products = new ArrayList<>();
            if(shopId == Constant.PLATFORM_SHOP_ID){
                products = productMapper.selectList(new LambdaQueryWrapper<Product>().in(Product::getCategoryId, ids));
                //删除所有店铺的分类缓存
                List<Long> shopIdList = products.stream().map(Product::getShopId).collect(Collectors.toList());
            }else{
                products =  productMapper.selectList(new LambdaQueryWrapper<Product>().in(Product::getShopCategoryId, ids));
            }
            List<Long> prodIds = products.stream().map(Product::getProdId).collect(Collectors.toList());
            // 更新es中的商品
            eventPublisher.publishEvent(new EsProductUpdateEvent(null, prodIds, EsOperationType.UPDATE_BATCH));
        }
    }

    private void updateCategoryLang(Category category) {
        categoryLangService.remove(new LambdaQueryWrapper<CategoryLang>().eq(CategoryLang::getCategoryId, category.getCategoryId()));
        saveCategoryLang(category);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(cacheNames = "CategoryListByParentIdAndShopId", key = "#category.superiorId + ':' + #category.shopId"),
            @CacheEvict(cacheNames = "CategoryListByParentIdAndShopId", key = "#category.parentId + ':' + #category.shopId"),
            @CacheEvict(cacheNames = "CategoryListByShopId", key = "#category.shopId + ':category'")
    })
    public void deleteCategroy(Category category) {
        categoryMapper.deleteById(category.getCategoryId());
        // 删除分类语言表
        removeCategoryLang(category);
        //修改积分配置中的分类数据
        if (Objects.equals(category.getParentId(),Constant.CATEGORY_ID)){
            this.changeScoreConfig();
        }
        deleteBrandsAndAttributes(category.getCategoryId());
    }

    private void removeCategoryLang(Category category) {
        Long categoryId = category.getCategoryId();
        categoryLangService.remove(new LambdaUpdateWrapper<CategoryLang>().eq(CategoryLang::getCategoryId,categoryId));
    }

    @Override
    public Category getCategoryByCategoryIdAndShopId(Long categoryId, Long shopId) {
        Category category = categoryMapper.getCategory(categoryId, shopId);
        if (category == null) {
            // 无法获取分类信息
            throw new YamiShopBindException("yami.unable.get.classification");
        }
        langManager.getCategoryAndLang(category);
        // 补充brandIds
        List<CategoryBrand> categoryBrands = categoryBrandMapper.selectList(new LambdaQueryWrapper<CategoryBrand>()
                .eq(CategoryBrand::getCategoryId, categoryId));
        if (!CollectionUtil.isEmpty(categoryBrands)) {
            List<Long> brandIds = categoryBrands.stream().map(CategoryBrand::getBrandId).toList();
            category.setBrandIds(brandIds);
        }
        return category;
    }

    @Override
    @Cacheable(cacheNames = "CategoryListByParentIdAndShopId", key = "#parentId + ':' + #shopId")
    public List<Category> listCategoryAndChildCategory(Long parentId, Long shopId) {
        // 如果是平台分类，则特殊处理
        List<Category> categories;
        if(shopId == Constant.PLATFORM_SHOP_ID) {
            categories = categoryMapper.listByParentId(parentId);
            List<Category> childCategories = categoryMapper.listThreeByParentId(parentId);
            langManager.getCategoryAndLang(childCategories);
            //根据父类id分组，并放入对应得父类中
            if (CollectionUtils.isNotEmpty(childCategories) && CollectionUtils.isNotEmpty(categories)) {
                List<DerivativeCategoryDto> derivativeCategories = BeanUtil.mapAsList(childCategories, DerivativeCategoryDto.class);
                Map<Long, List<DerivativeCategoryDto>> categoryMap = derivativeCategories.stream().collect(groupingBy(DerivativeCategoryDto::getParentId));
                // 无三级分类的一级分类id集合
                Set<Long> categoryIds = new HashSet<>();
                for (Category category : categories) {
                    if (categoryMap.containsKey(category.getCategoryId())) {
                        List<DerivativeCategoryDto> categoryList = categoryMap.get(category.getCategoryId());
                        category.setCategories(categoryList);
                    } else {
                        categoryIds.add(category.getCategoryId());
                    }
                }
                categories = categories.stream().filter(item -> !categoryIds.contains(item.getCategoryId())).collect(Collectors.toList());
            }
        }else {
            categories = categoryMapper.listSelect(shopId, parentId);
        }
        langManager.getCategoryAndLang(categories);
        return categories;
    }


    @Override
    @Cacheable(cacheNames = "CategoryListByShopId", key = "#shopId + ':category'")
    public List<Category> listByShopId(Long shopId) {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getShopId,shopId));
    }

    @Override
    public Long getParentCategoryByParentId(Long parentId) {
        return categoryMapper.getParentCategoryByParentId(parentId);
    }

    @Override
    @CacheEvict(cacheNames = "CategoryListByParentIdAndShopId", key = "#parentId + ':' + #shopId")
    public void removeCacheByParentIdAndLang( Long parentId,Long shopId) {
    }

    @Override
    public List<Category> categoryList(Category categoryParam) {
        categoryParam.setLang(I18nMessage.getLang());
        List<Category> categories = categoryMapper.categoryList(categoryParam);
        this.setCategoryLang(categories);
        return categories;
    }

    @Override
    public List<Category> categoryLangList(Category categoryParam) {
        List<Category> categories = categoryMapper.categoryLangList(categoryParam);
        langManager.getCategoryLang(categories);
        return categories;
    }

    @Override
    public IPage<Category> pageCategoryLangList(PageParam<Category> page, Category category) {
        IPage<Category> categoryPage = categoryMapper.categoryLangList(page, category);
        langManager.getCategoryLang(categoryPage.getRecords());
        return categoryPage;
    }

    @Override
    public List<Category> listByLang(Integer lang, Integer maxGrade, Long parentId, Integer status, Long shopId) {
        Integer defaultLang = null;
        if (!Objects.isNull(lang)) {
            defaultLang = I18nMessage.getI18nLang().getDefaultLang().getLang();
        }
        List<Category> categories = categoryMapper.listByLang(lang, defaultLang, maxGrade, parentId, status, shopId);
        if (!Objects.isNull(lang)) {
            this.setCategoryLang(categories);
        }
        return categories;
    }

    @Override
    public Integer getCategoryName(Category category) {
        return categoryMapper.getCategoryName(category);
    }

    @Override
    public Category getCategoryByCategoryId(Long categoryId) {
        return getCategoryByCategoryIdAndShopId(categoryId,null);
    }

    @Override
    public List<Category> listByGrade(Integer grade, Integer status, Long shopId) {
        List<Category> categories = categoryMapper.listByGrade(grade, status, shopId, I18nMessage.getLang());
        this.setCategoryLang(categories);
        return categories;
    }

    @Override
    public List<Long> getParentIdsByCategoryId(List<Long> categoryIds) {
        if (CollUtil.isEmpty(categoryIds)) {
            return null;
        }
        List<Category> categoryList = categoryMapper.getParentCategoryByCategoryId(categoryIds);
        langManager.getCategoryLang(categoryList);
        if (CollUtil.isEmpty(categoryList)) {
            return new ArrayList<>();
        }
        return categoryList.stream().map(Category::getCategoryId).collect(Collectors.toList());
    }

    @Override
    public List<Category> listByCategoryIds(Set<Long> cids) {
        if (CollectionUtils.isEmpty(cids)) {
            return Collections.emptyList();
        }
        List<Category> categories = categoryMapper.getListByCategoryIds(cids);
        langManager.getCategoryLang(categories);
        return categories;
    }

    @Override
    public List<Category> listByShopIdAndParentIdAndGrade(Long shopId, Long parentId, Integer grade) {
        List<Category> categories = categoryMapper.getListByShopIdAndParentId(shopId, parentId, grade);
        langManager.getCategoryAndLang(categories);
        return categories;
    }

    @Override
    public List<Category> platformCategory() {
        List<Category> categories = listByLang(I18nMessage.getDbLang(), 2, null, StatusEnum.ENABLE.value(), Constant.PLATFORM_SHOP_ID);
        Map<Long, List<Category>> categoryMap = categories.stream().collect(groupingBy(Category::getParentId));
        List<Category> primaryCategories = categoryMap.get(Constant.CATEGORY_ID);
        if (CollUtil.isEmpty(primaryCategories)) {
            return new ArrayList<>();
        }
        categories.clear();
        for (Category category : primaryCategories) {
            List<Category> secondaryCategories = categoryMap.get(category.getCategoryId());
            // 没有二级分类
            if (CollUtil.isEmpty(secondaryCategories)) {
                continue;
            }
            boolean hasSecondaryCategory = false;
            // 二级分类
            for (Category secondaryCategory : secondaryCategories) {
                List<Category> tertiaryCategories = categoryMap.get(secondaryCategory.getCategoryId());
                // 没有三级分类
                if (CollUtil.isEmpty(tertiaryCategories)) {
                    continue;
                }
                if (!hasSecondaryCategory) {
                    hasSecondaryCategory = true;
                }
                // 添加二级和三级分类
                categories.addAll(tertiaryCategories);
                categories.add(secondaryCategory);
            }
            // 最少包含一个启用的二级分类，才显示三级分类
            if (hasSecondaryCategory) {
                categories.add(category);
            }
        }
        return categories;
    }

    @Override
    public List<Category> getCategoryAndParent(Long categoryId) {
        if (Objects.isNull(categoryId) || Objects.equals(categoryId, Constant.PLATFORM_SHOP_ID)) {
            return new ArrayList<>();
        }
        List<Category> categoryList = categoryMapper.getCategoryAndParent(categoryId);
        langManager.getCategoryLang(categoryList);
        categoryList.sort(Comparator.comparing(Category::getGrade));
        return categoryList;
    }


    @Override
    @Cacheable(cacheNames = ProductCacheNames.CATEGORY_RATE, key = "'0'")
    public List<Category> listRate() {
        return categoryMapper.listRate();
    }

    @Override
    @CacheEvict(cacheNames = ProductCacheNames.CATEGORY_RATE, key = "'0'")
    public void removeListRateCache() {

    }

    private void deleteBrandsAndAttributes(Long categoryId) {
        // 删除所有分类所关联的品牌
        categoryBrandMapper.deleteByCategoryId(categoryId);
        // 删除所有分类所关联的参数
        categoryPropMapper.deleteByCategoryId(categoryId);
    }

    private void insertBrandsAndAttributes(Category category) {
        //保存分类与品牌信息
        if(CollUtil.isNotEmpty(category.getBrandIds())){
            categoryBrandMapper.insertCategoryBrand(category.getCategoryId(), category.getBrandIds());
        }

        //保存分类与参数信息
        if(CollUtil.isNotEmpty(category.getAttributeIds())){
            categoryPropMapper.insertCategoryProp(category.getCategoryId(), category.getAttributeIds());
        }
    }

    @Override
    public CategoryVO getInfo(Long categoryId) {
        Category categoryDb = categoryMapper.getCategory(categoryId, null);
        langManager.getCategoryAndLang(categoryDb);

        CategoryVO category = BeanUtil.map(categoryDb,CategoryVO.class);
        if (Objects.isNull(category)) {
            return category;
        }
        getPathNames(Collections.singletonList(category));
        if (CollUtil.isEmpty(category.getCategories())) {
            category.setCategories(new ArrayList<>());
        }
        return category;
    }

    @Override
    public List<Category> listCategoryAndLangList(Long shopId) {
        List<Category> categories = categoryMapper.listCategoryAndLangList(shopId);
        langManager.getCategoryAndLang(categories);
        return categories;
    }

    @Override
    public List<CategorySearchVO> listEsCategoryByCategoryIds(List<Long> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return new ArrayList<>();
        }
        Integer lang = I18nMessage.getDbLang();
        return categoryMapper.listEsCategoryByCategoryIds(lang, categoryIds);
    }

    @Override
    public void getPathNames(List<CategoryVO> categories) {
        if (CollUtil.isEmpty(categories)) {
            return;
        }

        List<Long> categoryIds = new ArrayList<>();
        for (CategoryVO category : categories) {
            if (Objects.isNull(category)) {
                continue;
            }
            categoryIds.add(category.getParentId());
        }
        // 获取分类的所有上级分类id集合
        List<Category> parentCategoryList = categoryMapper.getParentCategoryByCategoryId(categoryIds);
        langManager.getCategoryLang(parentCategoryList);
        if (CollUtil.isEmpty(parentCategoryList)) {
            return;
        }
        // 获取所有上级分类id列表
        List<ParentCategoryVO> categoryDtos = BeanUtil.mapAsList(parentCategoryList, ParentCategoryVO.class);
        Map<Long, ParentCategoryVO> categoryMap = categoryDtos.stream().collect(Collectors.toMap(ParentCategoryVO::getCategoryId, c -> c));
        // 获取每个分类的上级分类名称集合
        for (CategoryVO category : categories) {
            category.setCategories(new ArrayList<>());
            ParentCategoryVO secondaryCategory = categoryMap.get(category.getParentId());
            if (Objects.isNull(secondaryCategory)) {
                continue;
            }
            category.getCategories().add(secondaryCategory);
            if (categoryMap.containsKey(secondaryCategory.getParentId())) {
                category.getCategories().add(categoryMap.get(secondaryCategory.getParentId()));
            }
        }
    }

    /**
     * 刷新积分配置中的分类数据
     */
    private void changeScoreConfig(){
        ScoreConfigParam scoreParamDb = sysConfigService.getSysConfigObject(Constant.SCORE_CONFIG,ScoreConfigParam.class);
        if (Objects.isNull(scoreParamDb)){
            // 请先前往 【会员管理】-> 【积分成长值配置】-> 【积分获取配置】 进行配置并保存
            throw new YamiShopBindException("yami.flush.category.tips");
        }
        //判断分类数据是否正确
        List<Category> categoryList = categoryMapper.getListByShopIdAndParentId(Constant.PLATFORM_SHOP_ID, Constant.CATEGORY_ID, 1);
        langManager.getCategoryAndLang(categoryList);
        Map<Long,Category> categoryMap = categoryList.stream().collect(Collectors.toMap(Category::getCategoryId,category -> category));
        // 还没有分类数据时，scoreParamDb.getCategoryConfigs()为null,所以为它new一个对象
        if (Objects.isNull(scoreParamDb.getCategoryConfigs())) {
            scoreParamDb.setCategoryConfigs(Lists.newArrayList());
        }
        Iterator<CategoryScoreConfigParam> iterator = scoreParamDb.getCategoryConfigs().iterator();
        //更新数据并去除已删除的数据
        while (iterator.hasNext()){
            CategoryScoreConfigParam categoryScoreConfigParam = iterator.next();
            Category category = categoryMap.get(categoryScoreConfigParam.getCategoryId());
            if (Objects.isNull(category)){
                iterator.remove();
            } else {
                //刷新分类名称
                //删除map中已进行操作过的分类
                categoryMap.remove(categoryScoreConfigParam.getCategoryId());
            }
        }
        // 获取map中剩下的分类的id（剩下的分类为积分配置json中所没有的分类，需要新增）
        Set<Long> longs = categoryMap.keySet();
        // 新增积分配置json中的分类数据
        for (Long catrgoryId:longs){
            Category category = categoryMap.get(catrgoryId);
            CategoryScoreConfigParam categoryScoreConfigParam = new CategoryScoreConfigParam();
            categoryScoreConfigParam.setCategoryId(category.getCategoryId());
            categoryScoreConfigParam.setGetScoreLimit(1.00);
            categoryScoreConfigParam.setUseScoreLimit(1.00);
            scoreParamDb.getCategoryConfigs().add(categoryScoreConfigParam);
        }
        //保存数据
        sysConfigService.saveOrUpdateSysConfigService(scoreParamDb,Constant.SCORE_CONFIG);
    }

    private void setCategoryLang(List<Category> categories) {
        if (CollUtil.isEmpty(categories)) {
            return;
        }
        Integer lang = I18nMessage.getLang();
        LangConfig langConfig = langManager.getLangConfig();
        List<Long> ids = categories.stream().map(Category::getCategoryId).toList();
        for (Category category : categories) {
            List<CategoryLang> categoryLangList = category.getCategoryLangList();
            if (CollUtil.isEmpty(categoryLangList)) {
                continue;
            }
            CategoryLang categoryLang = langManager.getCategoryLang(langConfig, categoryLangList, category.getCategoryId(), lang);
            category.setCategoryName(categoryLang.getCategoryName());
            category.setCategoryLangList(categoryLangList);
        }
    }
}
