/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.multishop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.app.dto.CategoryDto;
import com.yami.shop.bean.enums.EsOperationType;
import com.yami.shop.bean.event.EsProductUpdateEvent;
import com.yami.shop.bean.model.Category;
import com.yami.shop.bean.model.Product;
import com.yami.shop.bean.vo.CategoryVO;
import com.yami.shop.common.annotation.RedisLock;
import com.yami.shop.common.annotation.SysLog;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.multishop.config.PlatformConstant;
import com.yami.shop.service.CategoryService;
import com.yami.shop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * 分类管理
 * @author lgh
 *
 */
@RestController
@RequestMapping("/prod/category")
@Tag(name = "分类接口")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final ApplicationEventPublisher eventPublisher;



    @GetMapping("/info/{categoryId}")
    @Operation(summary = "根据分类id获取分类信息" , description = "根据分类id获取分类信息")
    @PreAuthorize("@pms.hasPermission('prod:category:info')")
    public ServerResponseEntity<Category> info(@PathVariable("categoryId") Long categoryId){
        // 获取数据库中的分类，如果分类为空则抛异常
        Category category = categoryService.getCategoryByCategoryIdAndShopId(categoryId, Constant.PLATFORM_SHOP_ID);
        if (!Objects.equals(Constant.PLATFORM_SHOP_ID, category.getShopId())) {
            throw new YamiShopBindException("yami.no.auth");
        }
        return ServerResponseEntity.success(category);
    }

    @SysLog("保存分类")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('prod:category:save')")
    @Operation(summary = "保存分类信息" , description = "保存分类信息")
    @RedisLock(lockName = "saveCategoryLock",key = "#Category")
    public ServerResponseEntity<Long> save(@RequestBody Category category){
        category.setShopId(Constant.PLATFORM_SHOP_ID);
        category.setRecTime(new Date());
        Integer count = categoryService.getCategoryName(category);
        if(count > 0){
            // 类目名称已存在！
            throw new YamiShopBindException("yami.category.name.exist");
        }
        category.setSeq(Objects.isNull(category.getSeq()) ? 0 : category.getSeq());
        categoryService.saveCategroy(category);
        removeCategoryCacheByParentId(category);
        return ServerResponseEntity.success(category.getCategoryId());
    }

    @SysLog("更新分类")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('prod:category:update')")
    @Operation(summary = "更新分类信息" , description = "更新分类信息")
    public ServerResponseEntity<String> update(@RequestBody Category category){
        // 获取数据库中的分类，如果分类为空则抛异常
        categoryService.getCategoryByCategoryIdAndShopId(category.getCategoryId(),Constant.PLATFORM_SHOP_ID);
        category.setShopId(Constant.PLATFORM_SHOP_ID);
        Category categoryDb = categoryService.getCategoryByCategoryId(category.getCategoryId());
        if (Objects.equals(categoryDb.getParentId(), Constant.CATEGORY_ID) && !Objects.equals(category.getParentId(), Constant.CATEGORY_ID)){
            // 一级分类不能改为二级分类
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.category.one.check"));
        }else if(Objects.equals(category.getParentId(), Constant.CATEGORY_ID) && !Objects.equals(categoryDb.getParentId(), Constant.CATEGORY_ID)){
            // 二级分类不能改为一级分类
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.category.two.check"));
        }
        if (Objects.equals(category.getParentId(),category.getCategoryId())) {
            // 分类的上级不能是自己本身
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.category.superior.check"));
        }
        // 如果改成正常，且存在上级分类，则需要判断上级的状态
        if (Objects.equals(category.getStatus(),1) && !Objects.equals(category.getParentId(),0L)){
            Category parentCategory = categoryService.getOne(new LambdaQueryWrapper<Category>()
                    .eq(Category::getCategoryId, category.getParentId()));
            if(Objects.isNull(parentCategory) || Objects.equals(parentCategory.getStatus(),0)){
                // 修改失败，上级分类不存在或者不为正常状态
                throw new YamiShopBindException("yami.category.status.check");
            }
        }
        Integer count = categoryService.getCategoryName(category);
        if(count > 0){
            // 类目名称已存在！
            throw new YamiShopBindException("yami.category.name.exist");
        }
        category.setOldCategoryName(categoryDb.getCategoryName());
        categoryService.updateCategroy(category);
        removeCategoryCacheByParentId(category);
        eventPublisher.publishEvent(new EsProductUpdateEvent(category.getCategoryId(), null, EsOperationType.UPDATE_BY_SHOP_CATEGORY_ID));
        return ServerResponseEntity.success();
    }

    @SysLog("删除分类")
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("@pms.hasPermission('prod:category:delete')")
    @Operation(summary = "根据分类id删除分类" , description = "根据分类id删除分类")
    public ServerResponseEntity<String> delete(@PathVariable("categoryId") Long categoryId){
        long categoryProdCount = productService.count(new LambdaQueryWrapper<Product>().eq(Product::getShopCategoryId, categoryId).ne(Product::getStatus, -1));
        if (categoryProdCount>0){
            // 该分类下还有商品，请先删除该分类下的商品
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.category.delete.check"));
        }

        Category category = categoryService.getCategoryByCategoryIdAndShopId(categoryId,Constant.PLATFORM_SHOP_ID);
        categoryService.deleteCategroy(category);
        removeCategoryCacheByParentId(category);
        return ServerResponseEntity.success();
    }

    @GetMapping("/listCategory")
    @PreAuthorize("@pms.hasPermission('prod:category:listCategory')")
    @Operation(summary = "获取分类集合" , description = "获取分类集合")
    public ServerResponseEntity<List<Category>> listCategory(Category category){
        category.setLang(I18nMessage.getLang());
        category.setShopId(Constant.PLATFORM_SHOP_ID);
        List<Category> list =  categoryService.categoryLangList(category);
        return ServerResponseEntity.success(list);
    }

    @GetMapping("/listCategoryByGrade")
    @Operation(summary = "根据等级与状态获取平台分类列表" , description = "根据等级与状态获取平台分类列表")
    @Parameters(value = {
            @Parameter(name = "grade", description = "0：一级分类，1：二级分类，2：三级分类" ),
            @Parameter(name = "status", description = "默认是1，表示正常状态,0为下线状态" )
    })
    public ServerResponseEntity<List<Category>> listCategoryByGrade(@RequestParam(value = "grade", defaultValue = "2") Integer grade, @RequestParam(value = "status", required = false) Integer status) {
        List<Category> categories = categoryService.listByGrade(grade, status, Constant.PLATFORM_SHOP_ID);
        return ServerResponseEntity.success(categories);
    }

    @GetMapping("/pageCategory")
    @PreAuthorize("@pms.hasPermission('prod:category:listCategory')")
    @Operation(summary = "分页获取一级分类" , description = "分页获取一级分类")
    public ServerResponseEntity<IPage<Category>> pageCategory(PageParam<Category> page, Category category){
        category.setLang(I18nMessage.getLang());
        category.setGrade(1);
        category.setShopId(Constant.PLATFORM_SHOP_ID);
        IPage<Category> categoryPage =  categoryService.pageCategoryLangList(page, category);
        return ServerResponseEntity.success(categoryPage);
    }

    @GetMapping("/platformCategory")
    @Operation(summary = "获取平台所有上架的分类" , description = "获取平台所有上架的分类")
    public ServerResponseEntity<List<Category>> listCategory(){
        List<Category> list =  categoryService.platformCategory();
        return ServerResponseEntity.success(list);
    }

    @GetMapping("/shopCategory")
    @Operation(summary = "获取店铺分类" , description = "获取店铺分类")
    public ServerResponseEntity<List<CategoryVO>> shopCategory(){
        Category category = new Category();
        category.setLang(I18nMessage.getLang());
        category.setGrade(1);
        category.setShopId(Constant.PLATFORM_SHOP_ID);
        List<Category> list =  categoryService.categoryLangList(category);
        List<CategoryVO> categoryVos = BeanUtil.mapAsList(list, CategoryVO.class);
        return ServerResponseEntity.success(categoryVos);
    }



    @GetMapping("/upAndCurrCategoryList/{categoryId}")
    @Operation(summary = "获取上架分类和当前选中分类的父类" , description = "获取上架分类和当前选中分类的父类")
    @Parameters(value = {
            @Parameter(name = "maxGrade", description = "0：一级分类，1：二级分类，2：三级分类" ),
            @Parameter(name = "categoryId", description = "分类id" )
    })
    public ServerResponseEntity<List<Category>> upAndCurrCategoryList(
            @RequestParam(value = "maxGrade", required = false, defaultValue = "2") Integer maxGrade,
            @PathVariable("categoryId") Long categoryId){
        Category category = new Category();
        category.setLang(I18nMessage.getDbLang());
        category.setStatus(1);
        category.setShopId(Constant.PLATFORM_SHOP_ID);
        category.setGrade(maxGrade);
        //获取上架的分类
        List<Category> upList = categoryService.categoryList(category);

        //如果是新增的，直接返回上架的分类即可
        if (categoryId==0){
            return ServerResponseEntity.success(upList);
        }
        Category currCategory = categoryService.getCategoryByCategoryId(categoryId);
        if (currCategory == null) {
            return ServerResponseEntity.success(upList);
        }
        while (currCategory.getParentId() != 0) {
            currCategory=categoryService.getCategoryByCategoryId(currCategory.getParentId());
            if (!Objects.equals(currCategory.getStatus(), 1)) {
                upList.add(currCategory);
            }
        }
        return ServerResponseEntity.success(upList);
    }

    @GetMapping("/categoryInfo")
    @Operation(summary = "分类信息列表" , description = "获取所有的产品分类信息，顶级分类的parentId为0,默认为顶级分类")
    @Parameters({
            @Parameter(name = "parentId", description = "分类ID" ),
            @Parameter(name = "shopId", description = "店铺id" )
    })
    public ServerResponseEntity<List<CategoryDto>> categoryInfo(@RequestParam(value = "parentId", defaultValue = "0") Long parentId,
                                                                @RequestParam(value = "shopId", defaultValue = "0") Long shopId) {
        List<Category> categories = categoryService.listCategoryAndChildCategory(parentId, shopId);
        List<CategoryDto> categoryDtos = BeanUtil.mapAsList(categories, CategoryDto.class);
        return ServerResponseEntity.success(categoryDtos);
    }

    @GetMapping("/getCategoryAndParent")
    @Operation(summary = "获取平台分类及所有上级分类" , description = "获取平台分类及所有上级分类")
    public ServerResponseEntity<List<Category>> getCategoryAndParent(@RequestParam(value = "categoryId") Long categoryId){
        List<Category> categories = categoryService.getCategoryAndParent(categoryId);
        return ServerResponseEntity.success(categories);
    }

    private void removeCategoryCacheByParentId(Category category) {
        categoryService.removeListRateCache();
        categoryService.removeCacheByParentIdAndLang(category.getParentId(), category.getShopId());
        categoryService.removeCacheByParentIdAndLang(category.getSuperiorId(), category.getShopId());
        // 如果是2/3级分类，第一分类也需要清空缓存数据
        if(category.getGrade() == 1 || category.getGrade() == PlatformConstant.MAX_CATEGORY_GRADE) {
            categoryService.removeCacheByParentIdAndLang(Constant.CATEGORY_ID, Constant.PLATFORM_SHOP_ID);
        }
    }

}
