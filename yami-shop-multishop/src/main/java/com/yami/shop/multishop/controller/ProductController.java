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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yami.shop.bean.app.dto.ProductDto;
import com.yami.shop.bean.dto.SkuAdminDTO;
import com.yami.shop.bean.enums.*;
import com.yami.shop.bean.event.*;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.param.ProductParam;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.enums.StatusEnum;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.delivery.common.model.Transport;
import com.yami.shop.delivery.common.service.TransportService;
import com.yami.shop.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 商品列表、商品发布controller
 *
 * @author lgh
 */
@RestController
@RequestMapping("/prod/prod")
@Tag(name = "商品接口")
@AllArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final SkuService skuService;
    private final BasketService basketService;
    private final BrandService brandService;

    private final ShopDetailService shopDetailService;
    private final CategoryService categoryService;
    private final ApplicationEventPublisher eventPublisher;
    private final ProdParameterService prodParameterService;
    private final TransportService transportService;
    private final Snowflake snowflake;

    @GetMapping("/page")
    @Operation(summary = "商品信息列表", description = "商品信息列表")
    public ServerResponseEntity<IPage<Product>> page(ProductParam product, PageParam<Product> page) {
        product.setProdName(StrUtil.isNotBlank(product.getProdName()) ? product.getProdName().trim() : product.getProdName());
        IPage<Product> products = productService.page(page,
                new LambdaQueryWrapper<Product>()
                        .like(StrUtil.isNotBlank(product.getProdName()), Product::getProdName, product.getProdName())
                        .eq(Product::getShopId, Constant.PLATFORM_SHOP_ID)
                        .ne(Product::getStatus, -1)
                        .eq(product.getStatus() != null, Product::getStatus, product.getStatus())
                        .orderByDesc(Product::getSeq));
        List<Long> prodIds = products.getRecords().stream().map(Product::getProdId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(prodIds)) {
            Map<Long, Integer> stockMap = skuService.listSkuStock(prodIds);
            products.getRecords().forEach(prod -> prod.setTotalStocks(stockMap.getOrDefault(prod.getProdId(), 0)));
        }
        return ServerResponseEntity.success(products);
    }

    @GetMapping("/listProdByIdsAndType")
    @Operation(summary = "批量获取商品信息" , description = "批量获取商品信息")
    public ServerResponseEntity<List<ProductDto>> listProdByIdsAndType(ProductParam product) {
        product.setLang(I18nMessage.getDbLang());
        product.setShopId(Constant.PLATFORM_SHOP_ID);
        List<Product> products = productService.listProdByIdsAndType(product);
        List<ProductDto> productDtos = BeanUtil.mapAsList(products, ProductDto.class);
        return ServerResponseEntity.success(productDtos);
    }

    @GetMapping("/pageOnLineProd")
    @Operation(summary = "分页获取上架的商品信息" , description = "分页获取上架的商品信息")
    @PreAuthorize("@pms.hasPermission('prod:prod:page')")
    public ServerResponseEntity<IPage<Product>> pageOnLineProd(ProductParam product, PageParam<Product> page) {
        product.setShopId(Constant.PLATFORM_SHOP_ID);
        IPage<Product> products = productService.pageProducts(page, product);
        return ServerResponseEntity.success(products);
    }

    @GetMapping("/info/{prodId}")
    @Operation(summary = "根据商品id获取商品信息" , description = "根据商品id获取商品信息")
    @PreAuthorize("@pms.hasPermission('prod:prod:info')")
    public ServerResponseEntity<Product> info(@PathVariable("prodId") Long prodId) {
        Product prod = productService.getProductById(prodId);
        if (Objects.isNull(prod) || Objects.equals(prod.getStatus(), ProdStatusEnums.DELETE.getValue())) {
            //商品状态不正确，请退出重试
            throw new YamiShopBindException("yami.prod.status.error.Please.exit.and.try.again");
        }
        Brand brand = brandService.getByBrandId(prod.getBrandId());
        if (Objects.nonNull(brand)) {
            prod.setBrand(brand);
        }
        if (!Objects.equals(prod.getShopId(), Constant.PLATFORM_SHOP_ID)) {
            throw new YamiShopBindException("yami.prod.shop.not.exist");
        }
        List<ProdParameter> prodParameterList = prodParameterService.listParameterAndLang(prodId);
        prod.setProdParameterList(prodParameterList);
        List<Sku> skuList = skuService.listSkuByProdId(prodId);
        Integer totalStock = skuList.stream().mapToInt(Sku::getStocks).sum();
        prod.setSkuList(skuList);
        prod.setTotalStocks(totalStock);
        // 店铺分类信息
        prod.setCategoryVO(categoryService.getInfo(prod.getCategoryId()));
        if (Objects.nonNull(prod.getShopCategoryId()) && !Objects.equals(prod.getShopCategoryId(), Constant.PLATFORM_SHOP_ID)) {
            prod.setShopCategoryVO(categoryService.getInfo(prod.getShopCategoryId()));
        }
        return ServerResponseEntity.success(prod);
    }

    @PostMapping
    @PreAuthorize("@pms.hasPermission('prod:prod:save')")
    @Operation(summary = "新增商品" , description = "新增商品")
    public ServerResponseEntity<Long> save(@Valid @RequestBody ProductParam productParam) {
        checkParam(productParam);
        Long shopId = Constant.PLATFORM_SHOP_ID;
        ShopDetail shopDetail = shopDetailService.getShopDetailByShopId(shopId);

        this.checkCategoryAndBrand(shopId, productParam.getCategoryId(), productParam.getBrandId());
        productParam.setShopId(shopId);
        if (Objects.nonNull(productParam.getProdType()) && Objects.equals(productParam.getProdType(), ProdType.PROD_TYPE_ACTIVE.value())) {
            productParam.setProdType(ProdType.PROD_TYPE_ACTIVE.value());
        } else {
            productParam.setProdType(ProdType.PROD_TYPE_NORMAL.value());
        }
        productService.saveProduct(productParam);
        eventPublisher.publishEvent(new EsProductUpdateEvent(productParam.getProdId(), null, EsOperationType.SAVE));
        // 移除缓存
        prodParameterService.removeCacheByProdId(productParam.getProdId());
        return ServerResponseEntity.success(productParam.getProdId());
    }

    @PutMapping
    @PreAuthorize("@pms.hasPermission('prod:prod:update')")
    @Operation(summary = "修改商品" , description = "修改商品")
    public ServerResponseEntity<Long> update(@Valid @RequestBody ProductParam productParam) {
        checkParam(productParam);
        Product dbProduct = productService.getProductById(productParam.getProdId());
        if (Objects.equals(dbProduct.getStatus(), ProdStatusEnums.DELETE.getValue())) {
            // 产品已被删除
            throw new YamiShopBindException("yami.product.service.delete");
        }
        if (Objects.equals(dbProduct.getStatus(), ProdStatusEnums.PLATFORM_OFFLINE.getValue())) {
            productParam.setStatus( ProdStatusEnums.PLATFORM_OFFLINE.getValue());
        }
        if (!Objects.equals(dbProduct.getShopId(), Constant.PLATFORM_SHOP_ID)) {
            // 查找不到该商品信息
            throw new YamiShopBindException("yami.product.not.exist");
        }

        if (!Objects.equals(dbProduct.getProdType(), productParam.getProdType())) {
            // 商品类型改变，请刷新页面后重试
            throw new YamiShopBindException("yami.prod.type.check");
        }

        if (!Objects.equals(dbProduct.getMold(), productParam.getMold())) {
            // 商品创建后不能修改商品类型
            throw new YamiShopBindException("yami.order.prod.type.check");
        }
        Long shopId = Constant.PLATFORM_SHOP_ID;
        this.checkCategoryAndBrand(shopId, productParam.getCategoryId(), productParam.getBrandId());
        List<String> userIds = basketService.listUserIdByProdId(productParam.getProdId());


        List<Sku> dbSkus = skuService.listSkuByProdId(dbProduct.getProdId());

        dbProduct.setSkuList(dbSkus);
        productParam.setProdType(productParam.getProdType());
        productService.updateProduct(productParam, dbProduct);
        //清除购物车缓存
        basketService.removeCacheByUserIds(userIds);
        productService.removeProdCacheByProdId(productParam.getProdId());
        prodParameterService.removeCacheByProdId(productParam.getProdId());
        for (Sku sku : dbSkus) {
            skuService.removeSkuCacheBySkuId(sku.getSkuId(), sku.getProdId());
        }
        return ServerResponseEntity.success(Objects.equals(productParam.getStatus(), ProdStatusEnums.NORMAL.getValue()) ? productParam.getProdId() : null);
    }

    @DeleteMapping("/{prodId}")
    @PreAuthorize("@pms.hasPermission('prod:prod:delete')")
    @Operation(summary = "根据商品id删除商品" , description = "根据商品id删除商品")
    public ServerResponseEntity<Void> delete(@PathVariable("prodId") Long prodId) {
        Product dbProduct = productService.getProductByProdId(prodId);
        if (!Objects.equals(dbProduct.getShopId(), Constant.PLATFORM_SHOP_ID)) {
            // 查找不到该商品信息
            throw new YamiShopBindException("yami.product.not.exist");
        }
        // 已经删除的商品
        if (Objects.equals(dbProduct.getStatus(), StatusEnum.DELETE.value())) {
            eventPublisher.publishEvent(new EsProductUpdateEvent(prodId, null, EsOperationType.DELETE));
            return ServerResponseEntity.success();
        }
        List<Sku> dbSkus = skuService.listSkuByProdId(dbProduct.getProdId());
        List<Long> deleteSkuIds = dbSkus.stream().map(Sku::getSkuId).collect(Collectors.toList());
        // 删除商品
        productService.removeProductByProdId(prodId, deleteSkuIds);
        productService.removeProdCacheByProdId(prodId);
        prodParameterService.removeCacheByProdId(prodId);
        if (Objects.nonNull(dbSkus)) {
            for (Sku sku : dbSkus) {
                skuService.removeSkuCacheBySkuId(sku.getSkuId(), sku.getProdId());
            }
        }

        List<String> userIds = basketService.listUserIdByProdId(prodId);
        // 商品状态改变时的发送事件，让活动下线
        if (Objects.nonNull(dbSkus)) {
            for (Sku sku : dbSkus) {
                skuService.removeSkuCacheBySkuId(sku.getSkuId(), sku.getProdId());
            }
        }
        //清除购物车缓存
        basketService.removeCacheByUserIds(userIds);
        return ServerResponseEntity.success();
    }

    @PutMapping("/prodStatus")
    @PreAuthorize("@pms.hasPermission('prod:prod:status')")
    @Operation(summary = "更新商品状态" , description = "更新商品状态")
    public ServerResponseEntity<Void> prodStatus(@RequestBody ProductParam productParam) {
        Long shopId = Constant.PLATFORM_SHOP_ID;
        Long prodId = productParam.getProdId();
        Integer prodStatus = productParam.getStatus();

        Product dbProduct = productService.getProductByProdId(prodId);
        checkProdInfo(productParam, shopId, dbProduct);
        productService.updateProdStatus(Collections.singletonList(prodId),prodStatus);

        return ServerResponseEntity.success();
    }

    private void checkProdInfo(ProductParam productParam, Long shopId, Product dbProduct) {
        if (!Objects.equals(dbProduct.getShopId(), shopId)) {
            // 查找不到该商品信息
            throw new YamiShopBindException("yami.product.not.exist");
        }
        if (!(Objects.equals(dbProduct.getStatus(), ProdStatusEnums.NORMAL.getValue())
                || Objects.equals(dbProduct.getStatus(), ProdStatusEnums.SHOP_OFFLINE.getValue()))) {
            // 商品不在正常状态，修改失败
            throw new YamiShopBindException("yami.product.on.normal");
        }
    }

    @DeleteMapping
    @PreAuthorize("@pms.hasPermission('prod:prod:batchDelete')")
    @Operation(summary = "根据商品id列表批量删除商品" , description = "根据商品id列表批量删除商品")
    public ServerResponseEntity<Boolean> batchRemoveById(@RequestBody Long[] ids) {
        // TODO 待优化
        StringBuilder stringBuilder = new StringBuilder();
        for (Long prodId : ids) {
            try {
                delete(prodId);
            } catch (Exception e) {
                Product dbProduct = productService.getProductByProdId(prodId);
                stringBuilder.append("【").append(dbProduct.getProdName()).append("】  ");
            }
        }
        if (!stringBuilder.isEmpty()) {
            throw new YamiShopBindException(String.format(I18nMessage.getMessage("yami.product.exception.deleteError"), stringBuilder));
        }
        eventPublisher.publishEvent(new EsProductUpdateEvent(null, Arrays.asList(ids), EsOperationType.DELETE_BATCH));
        return ServerResponseEntity.success();
    }

    @PutMapping("/batchProdStatus/{status}")
    @PreAuthorize("@pms.hasPermission('prod:prod:batchStatus')")
    @Operation(summary = "批量更新商品状态" , description = "批量更新商品状态")
    public ServerResponseEntity<Void> batchProdStatus(@RequestBody Long[] ids, @PathVariable("status") Integer status) {
        // TODO 待优化
        ProductParam product = new ProductParam();
        product.setStatus(status);
        StringBuilder stringBuilder = new StringBuilder();
        for (Long prodId : ids) {
            product.setProdId(prodId);
            try {
                prodStatus(product);
            } catch (Exception e) {
                Product dbProduct = productService.getProductByProdId(prodId);
                //【商品名】:错误信息xxx (【数码相机】:本店分类处于下线中，商品不能上架)
                stringBuilder.append("【" + dbProduct.getProdName() + "】:" + e.getMessage() + "\n");
            }
        }
        eventPublisher.publishEvent(new EsProductUpdateEvent(null, Arrays.asList(ids), EsOperationType.UPDATE_BATCH));
        if (!stringBuilder.isEmpty()) {
            throw new YamiShopBindException(String.format(I18nMessage.getMessage("yami.product.exception.updateStatusError"), stringBuilder));
        }
        return ServerResponseEntity.success();
    }



    @GetMapping("/prodSkuPage")
    @Operation(summary = "分页获取商品及商品sku信息" , description = "分页获取商品及商品sku信息")
    public ServerResponseEntity<IPage<Product>> prodSkuPage(ProductParam product, PageParam<Product> page) {
        product.setLang(I18nMessage.getDbLang());
        product.setShopId(Constant.PLATFORM_SHOP_ID);
        IPage<Product> products = productService.prodSkuPage(page, product);
        return ServerResponseEntity.success(products);
    }


    private void checkParam(ProductParam productParam) {
        Long prodId = productParam.getProdId();
        Product.DeliveryModeVO deliveryMode = productParam.getDeliveryModeVo();
        boolean hasDeliverMode = deliveryMode != null
                && (deliveryMode.getHasShopDelivery() || deliveryMode.getHasUserPickUp() || deliveryMode.getHasCityDelivery());
        if (!hasDeliverMode) {
            // 请选择配送方式
            throw new YamiShopBindException("yami.product.dvy.type");
        }
        if (Objects.equals(productParam.getMold(), 1)) {
            // 虚拟商品的配送方式
            Product.DeliveryModeVO mode = new Product.DeliveryModeVO();
            mode.setHasShopDelivery(true);
            mode.setHasUserPickUp(false);
            mode.setHasCityDelivery(false);
            productParam.setDeliveryModeVo(mode);
        }
        //运费模板
        if(Objects.nonNull(productParam.getDeliveryTemplateId()) && !DeliveryTemplateType.isUnifiedTemplate(productParam.getDeliveryTemplateId())) {
            Transport transport = transportService.getTransportAndAllItems(productParam.getDeliveryTemplateId());
            if (Objects.isNull(transport)) {
                // 产品运费模板不存在
                throw new YamiShopBindException("yami.prod.transport.not.exist");
            }
        }

        // 统一运费-运费金额不能小于0
        if(Objects.equals(productParam.getDeliveryTemplateId(), DeliveryTemplateType.FREIGHT.getValue()) && productParam.getDeliveryAmount() < 0) {
            // 运费金额不能小于0
            throw new YamiShopBindException("yami.Shipping.amount.error");
        }
        Category category = categoryService.getById(productParam.getCategoryId());
        if (Objects.isNull(category)) {
            // 该平台分类已删除，请选择新的分类
            throw new YamiShopBindException("yami.prod.platform.category.deleted");
        }
        if (category.getGrade() < CategoryLevel.THIRD.value()) {
            // 请选择三级分类
            throw new YamiShopBindException("yami.prod.category.error");
        }
        List<SkuAdminDTO> skuList = productParam.getSkuList();
        Long shopId = Constant.PLATFORM_SHOP_ID;
        //商品编码
        List<Product> products = productService.list(new LambdaQueryWrapper<Product>().eq(Product::getShopId, shopId)
                .ne(Product::getStatus, -1));
        List<Long> prodIds = products.stream().map(Product::getProdId).collect(Collectors.toList());
        List<String> partyCodes = skuService.listSkuByProdIds(prodIds, prodId);
        // 校验sku
        checkSku(skuList, partyCodes,productParam.getMold());
        // 判断组合选品的预售状态
        if(Objects.equals(productParam.getProdType(), ProdType.PROD_TYPE_ACTIVE.value())) {
            productParam.setPreSellStatus(0);
        }
    }

    private void checkSku(List<SkuAdminDTO> skuList, List<String> partyCodes, Integer mold) {
        // 检查库存点是否被删除了
        boolean isAllUnUse = true;
        for (SkuAdminDTO sku : skuList) {
            //雪花算法生成商品编码
            if (StrUtil.isBlank(sku.getPartyCode())) {
                String partyCode = StringUtils.join("RM", String.valueOf(snowflake.nextId()));
                sku.setPartyCode(partyCode);
            }
            if (CollectionUtils.isNotEmpty(partyCodes) && partyCodes.contains(sku.getPartyCode())) {
                String message = I18nMessage.getMessage("yami.sku.party.code");
                String isExit = I18nMessage.getMessage("yami.is.exist");
                //商品编码已存在
                throw new YamiShopBindException(message + sku.getPartyCode() + isExit);
            }
            // sku价格最低只能为0.01
            if (Constant.MIN_PRODUCT_AMOUNT > sku.getPrice()) {
                sku.setPrice(Constant.MIN_PRODUCT_AMOUNT);
            }
            if (1 == sku.getStatus()) {
                isAllUnUse = false;
            }
            if (Objects.isNull(sku.getStockWarning())) {
                sku.setStockWarning(0);
            }
            if(sku.getStockWarning()>Constant.STOCK_WARNING_MAX){
                //超过库存预警最大值
                throw new YamiShopBindException("yami.sku.stockWarning.limit");
            }
        }
        if (isAllUnUse) {
            // 至少要启用一种商品规格
            throw new YamiShopBindException("yami.product.enable.sku");
        }
    }

    /**
     * 检查分类与品牌是否可用
     *
     * @param shopId         店铺id
     * @param categoryId     平台分类
     * @param brandId        品牌id
     */
    private void checkCategoryAndBrand(Long shopId, Long categoryId, Long brandId) {
        Category shopCategory = categoryService.getOne(Wrappers.lambdaQuery(Category.class)
                .eq(Category::getShopId, shopId)
                .eq(Category::getCategoryId, categoryId));
        if (Objects.isNull(shopCategory) || !Objects.equals(shopCategory.getStatus(), StatusEnum.ENABLE.value())) {
            // 当前店铺分类已不可用，请刷新分类列表重新选择
            throw new YamiShopBindException("yami.product.shop.category.not.under");
        }
        if (Objects.isNull(brandId) || Objects.equals(brandId, 0L)) {
            return;
        }
        Brand brand = brandService.getOne(Wrappers.lambdaQuery(Brand.class).eq(Brand::getBrandId, brandId));
        if (Objects.isNull(brand) || !Objects.equals(brand.getStatus(), StatusEnum.ENABLE.value())) {
            throw new YamiShopBindException("yami.product.brand.not.under");
        }
    }


}
