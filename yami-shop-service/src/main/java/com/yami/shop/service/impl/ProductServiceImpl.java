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
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.app.dto.SkuDto;
import com.yami.shop.bean.app.vo.ProductVO;
import com.yami.shop.bean.bo.ProductBO;
import com.yami.shop.bean.dto.*;
import com.yami.shop.bean.enums.*;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.param.*;
import com.yami.shop.bean.vo.SkuStockVO;
import com.yami.shop.bean.vo.search.ProductSearchLangVO;
import com.yami.shop.bean.vo.search.ProductSearchVO;
import com.yami.shop.common.bean.LangConfig;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.constants.CacheNames;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.util.*;
import com.yami.shop.dao.*;
import com.yami.shop.manager.impl.LangManager;
import com.yami.shop.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.compile;

/**
 * @author yami
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductMapper productMapper;
    private final ProdLangService prodLangService;
    private final ApplicationContext applicationContext;
    private final BasketService basketService;
    private final SkuService skuService;
    private final BasketMapper basketMapper;
    private final SkuMapper skuMapper;
    private final SkuLangMapper skuLangMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final SysConfigService sysConfigService;
    private final ShopDetailService shopDetailService;
    private final LangManager langManager;
    private final ProdParameterService prodParameterService;
    private final ProdExtensionService prodExtensionService;
    private final UserCollectionService userCollectionService;

    @Override
    public EsPageVO<ProductSearchVO> pageProduct(PageParam pageParam, EsProductParam productParam) {
        pageParam.setCurrent(pageParam.getCurrent());
        pageParam.setSize(pageParam.getSize());
        Page<ProductSearchVO> page = productMapper.pageProduct(pageParam, productParam);
        this.fillEsProductInfo(productParam, page.getRecords());
        // 封装成esPage
        EsPageVO<ProductSearchVO> esPage = new EsPageVO<>();
        esPage.setPages((int) pageParam.getPages());
        esPage.setTotal(pageParam.getTotal());
        esPage.setRecords(new ArrayList<>(page.getRecords()));
        return esPage;
    }

    /**
     * 补充商品信息(从数据库中查出来的)
     *
     * @param products 商品集合
     */
    private void fillEsProductInfo(EsProductParam productParam, List<ProductSearchVO> products) {
        if (CollectionUtils.isEmpty(products)) {
            return;
        }
        List<Long> prodIds = new ArrayList<>(Constant.INITIAL_CAPACITY);
        for (ProductSearchVO product : products) {
            prodIds.add(product.getProdId());
        }
        // 数据准备 - 多语言
        Map<Long, List<ProductSearchLangVO>> prodLangMap = new HashMap<>(Constant.INITIAL_CAPACITY);

        List<ProdLang> prodLangs = prodLangService.list(new LambdaQueryWrapper<ProdLang>().in(ProdLang::getProdId, prodIds));
        if (!CollectionUtils.isEmpty(prodLangs)) {
            prodLangMap = prodLangs.stream().map(prodLang -> {
                ProductSearchLangVO productSearchLangVO = new ProductSearchLangVO();
                BeanUtils.copyProperties(prodLang, productSearchLangVO);
                return productSearchLangVO;
            }).collect(Collectors.groupingBy(ProductSearchLangVO::getProdId));
        }

        // 数据补充
        for (ProductSearchVO product : products) {
            // 多语言
            product.setProdLangList(prodLangMap.get(product.getProdId()));
            // 销量
//
//            product.setSoldNum(product.getActualSoldNum() + product.getWaterSoldNum());

        }
        getProdLang(products);
    }

    private void getProdLang(List<ProductSearchVO> prodList) {
        LangConfig langConfig = langManager.getLangConfig();
        Integer lang = I18nMessage.getLang();
        for (ProductSearchVO product : prodList) {
            if (CollUtil.isEmpty(product.getProdLangList())) {
                continue;
            }
            Map<Integer, ProductSearchLangVO> langMap = product.getProdLangList().stream().filter(prod -> Objects.nonNull(prod.getLang()) && Objects.nonNull(prod.getProdName())).collect(Collectors.toMap(ProductSearchLangVO::getLang, p -> p));
            if (langMap.size() == 0) {
                product.setProdName(null);
                product.setBrief(null);
                continue;
            }
            ProductSearchLangVO productSearchLangVO = langMap.get(langManager.getLang(langMap.keySet(), langConfig, lang));

            product.setProdName(productSearchLangVO.getProdName());
            product.setBrief(productSearchLangVO.getBrief());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProduct(ProductParam productParam) {
        if (Objects.isNull(productParam.getStatus())) {
            productParam.setStatus(ProdStatusEnums.NORMAL.getValue());
        }
        checkProdLang(productParam.getProdLangList());
        Product product = BeanUtil.map(productParam, Product.class);
        // 实物商品且不是活动商品，必须要配置物流模板
        boolean deliveryTemplateIsEmpty = Objects.equals(product.getMold(), 0) &&
                !Objects.equals(product.getProdType(), ProdType.PROD_TYPE_ACTIVE.value()) &&
                Objects.isNull(product.getDeliveryTemplateId());
        if (deliveryTemplateIsEmpty) {
            throw new YamiShopBindException("yami.prod.delivery.template.not.null");
        }
        product.setDeliveryMode(Json.toJsonString(productParam.getDeliveryModeVo()));
        product.setUpdateTime(new Date());
        product.setPutawayTime(new Date());
        product.setCreateTime(new Date());
        product.setVersion(0);
        if (Objects.isNull(productParam.getMold()) || !Objects.equals(productParam.getMold(), ProdMoldEnum.VIRTUAL.value())) {
            product.setIsRefund(null);
            product.setWriteOffTime(null);
            product.setWriteOffStart(null);
            product.setWriteOffEnd(null);
            product.setWriteOffNum(null);
            product.setWriteOffMultipleCount(null);
            product.setVirtualRemark(null);
        }
        product.setActivityId(0L);
        productMapper.insert(product);
        // 保存语言表
        productParam.setProdId(product.getProdId());
        Integer prodStock = 0;
        // 保存商品参数
        prodParameterService.saveAndUpdateParameter(productParam.getProdParameterList(), productParam.getProdId());
        if (CollectionUtil.isNotEmpty(product.getSkuList())) {
            List<Sku> skuList = product.getSkuList();
            setSkuName(productParam.getProdLangList(), skuList);
            Long prodId = productParam.getProdId();
            for (Sku sku : skuList) {
                handleSku(sku);
                if (Objects.equals(sku.getStatus(), 1)) {
                    prodStock += sku.getStocks();
                }
                sku.setProdId(prodId);
            }

            List<SkuStockVO> skuStocks = skuService.insertBatchAndLang(skuList, productParam.getShopId(), productParam.getMold(), product.getStatus(), productParam.getEmployeeMobile(), productParam.getEmployeeId());

        }
        saveLang(productParam);
    }

    private void handleSku(Sku sku) {
        double minPrice = 0.01;
        sku.setOriPrice(Objects.isNull(sku.getOriPrice()) ? minPrice : sku.getOriPrice());
        sku.setPrice(Objects.isNull(sku.getPrice()) ? minPrice : sku.getPrice());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(ProductParam productParam, Product dbProduct) {
        // 没有品牌的商品，品牌id设为0
        if (Objects.isNull(productParam.getBrandId())) {
            productParam.setBrandId(0L);
        }
        checkProdLang(productParam.getProdLangList());
        Product product = BeanUtil.map(productParam, Product.class);
        product.setDeliveryMode(Json.toJsonString(productParam.getDeliveryModeVo()));
        product.setUpdateTime(new Date());

        // 实物商品且不是活动和积分商品，必须要配置物流模板
        boolean deliveryTemplateIsEmpty = Objects.equals(product.getMold(), 0) &&
                !Objects.equals(product.getProdType(), ProdType.PROD_TYPE_ACTIVE.value()) &&
                !Objects.equals(product.getProdType(), ProdType.PROD_TYPE_SCORE.value()) &&
                Objects.isNull(product.getDeliveryTemplateId());
        if (deliveryTemplateIsEmpty) {
            throw new YamiShopBindException("yami.prod.delivery.template.not.null");
        }
        productParam.setStatus(product.getStatus());
        List<Long> dbSkuIds = dbProduct.getSkuList().stream().map(Sku::getSkuId).collect(Collectors.toList());
        Map<Long, Sku> dbSkuMap = dbProduct.getSkuList().stream().collect(Collectors.toMap(Sku::getSkuId, dbSku -> dbSku));
        // 将所有该商品的sku标记为已删除状态
        skuMapper.deleteByProdId(product.getProdId());
        // 删除商品对应语言信息列表
        prodLangService.remove(new LambdaQueryWrapper<ProdLang>().eq(ProdLang::getProdId, product.getProdId()));
        // 保存语言表信息
        saveLang(productParam);
        // 保存商品参数
        prodParameterService.saveAndUpdateParameter(productParam.getProdParameterList(), productParam.getProdId());

        // 接口传入sku列表
        List<Sku> skuList = product.getSkuList();

        if (CollectionUtil.isEmpty(skuList)) {
            return;
        }

        // 是否有sku的状态发生变化
        boolean skuStatusChangeFlag = false;

        List<Sku> insertSkuList = new ArrayList<>();
        // 原有sku增加库存的sku列表(不包含新增的sku)
        List<Sku> addStockSkuList = new ArrayList<>();
        // 原有sku减少库存的sku列表(不包含新增的sku)
        List<Sku> reduceStockSkuList = new ArrayList<>();
        setSkuName(productParam.getProdLangList(), skuList);
        skuStatusChangeFlag = updateProdSku(dbProduct, product, dbSkuIds, dbSkuMap, skuList, skuStatusChangeFlag, insertSkuList, addStockSkuList, reduceStockSkuList);

        // 处理sku
        handleSku(skuList, dbSkuIds, insertSkuList, skuStatusChangeFlag, product);
        // 清除缓存
        removeProdCacheByProdId(dbProduct.getProdId());
    }


    private void handleSku(List<Sku> skuList, List<Long> dbSkuIds, List<Sku> insertSkuList, boolean skuStatusChangeFlag, Product product) {
        List<Long> deleteSkuIds = new ArrayList<>();
        List<Long> existSkuId = skuList.stream().map(Sku::getSkuId).toList();
        for (Long dbSkuId : dbSkuIds) {
            if (!existSkuId.contains(dbSkuId)) {
                deleteSkuIds.add(dbSkuId);
            }
        }
        // 需要删除的skuIds对应的购物车
        deleteShopCart(dbSkuIds, skuList);
        // 更新商品信息
        saveOrUpdate(product);
    }


    /**
     * 删除sku时，删除掉关联购物车
     *
     * @param dbSkuIds 数据库sku信息
     * @param skuList  此时修改后的信息
     */
    private void deleteShopCart(List<Long> dbSkuIds, List<Sku> skuList) {
        List<Long> deleteSkuIds = new ArrayList<>();
        List<Long> existSkuId = skuList.stream().map(Sku::getSkuId).toList();
        for (Long dbSkuId : dbSkuIds) {
            if (!existSkuId.contains(dbSkuId)) {
                deleteSkuIds.add(dbSkuId);
            }
        }
        // 如果不为空，则进行删除关联的购物车
//        if (CollectionUtils.isNotEmpty(deleteSkuIds)) {
//            for (Long deleteSkuId : deleteSkuIds) {
//                basketMapper.delete(new LambdaQueryWrapper<Basket>().eq(Basket::getSkuId, deleteSkuId));
//            }
//        }
    }

    /**
     * 这里的缓存 1800秒消失，详情RedisCacheConfig这里的配置
     * 当秒杀的时候，并不更新这里的缓存，所以库存会滞后，但是半个小时后缓存消失，又会重新获取
     *
     * @param prodId
     * @return
     */
    @Override
    @Cacheable(cacheNames = CacheNames.PRODUCT_KEY, key = "#prodId")
    public Product getProductByProdId(Long prodId) {
        Product product = productMapper.selectByIdAndLang(prodId);
        if (Objects.isNull(product)) {
            return null;
        }
        langManager.getProdAndLang(product);
        return product;
    }

    @Override
    public Product getProductAndLang(Long prodId) {
        ProductServiceImpl productService = (ProductServiceImpl) AopContext.currentProxy();
        Product product = productService.getProductByProdId(prodId);
        if (Objects.isNull(product)) {
            return null;
        }
        langManager.handleProdLang(product);
        return product;
    }

    @Override
    public Product getProductInfo(Long prodId) {
        Product product = this.getProductByProdId(prodId);
        if (Objects.isNull(product)) {
            return null;
        }
//        ProdExtension prodExtension = prodExtensionService.getByProdId(prodId);
//        product.setTotalStocks(prodExtension.getStock());
//        product.setWaterSoldNum(prodExtension.getWaterSoldNum());
//        product.setSoldNum(prodExtension.getSoldNum());
        return product;
    }

    @Override
    public Product getProductById(Long prodId) {
        return getProductInfo(prodId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeProductByProdId(Long prodId, List<Long> deleteSkuIds) {
        ProductService productService = (ProductService) AopContext.currentProxy();
        Product dbProduct = productService.getProductByProdId(prodId);
        // 将商品的状态变成-1（逻辑删除）
        Product product = new Product();
        product.setProdId(prodId);
        product.setIsTop(0);
        product.setStatus(-1);
        saveOrUpdate(product);
        skuMapper.deleteByProdId(prodId);
        //删除用户收藏中的该商品
        userCollectionService.remove(new LambdaQueryWrapper<UserCollection>()
                .eq(UserCollection::getProdId, prodId));
        // 删除语言表信息
        skuLangMapper.deleteByProdId(prodId);
        // 删除商品参数
        prodParameterService.removeByProdId(prodId);

        // 清除缓存
        removeProdCacheByProdId(prodId);
    }


    @Override
    public void removeProdCacheByProdId(Long prodId) {
        if (Objects.isNull(prodId)) {
            return;
        }
        removeProductCacheByProdIds(Collections.singletonList(prodId));
    }

    @Override
    public void removeProductCacheByProdIds(List<Long> prodIds) {
        if (CollUtil.isEmpty(prodIds)) {
            return;
        }
        List<String> keys = new ArrayList<>();
        for (Long prodId : prodIds) {
            // cacheNames = "product"
            keys.add(CacheNames.PRODUCT_KEY + CacheNames.UNION + prodId);
            // cacheNames = "skuList"
            keys.add(CacheNames.SKU_LIST_KEY + CacheNames.UNION + prodId);
            // cacheNames = "prodExtensionInfo"
            keys.add(CacheNames.PROD_EXTENSION_INFO_KEY + CacheNames.UNION + prodId);
        }
        RedisUtil.del(keys);

    }


    @Override
    public List<ProdAndSkuListsDto> getProdAndSkuLists(List<Long> prodIds) {
        List<ProdAndSkuListsDto> productList = productMapper.getProdAndSkuLists(prodIds);
        Set<Long> skuIdSet = new HashSet<>();
        for (ProdAndSkuListsDto prodAndSkuListsDto : productList) {
            skuIdSet.addAll(prodAndSkuListsDto.getSkuDtoList().stream().map(SkuDto::getSkuId).toList());
        }
        return productList;
    }

    @Override
    public int updateProductToGroup(Long groupActivityId, Long prodId) {
        return productMapper.updateProductToGroup(groupActivityId, prodId);
    }

    @Override
    public IPage<Product> pageProducts(PageParam<Product> page, ProductParam product) {
        return productMapper.pageProducts(page, product);
    }

    @Override
    public IPage<Product> pageByLang(PageParam<Product> page, ProductParam product) {
        IPage<Product> productPage = productMapper.pageByLang(page, product);
        langManager.getProdLang(productPage.getRecords());
        return productPage;
    }

    @Override
    public List<Product> recoveryPreSaleProd() {
        List<Product> products = list(new LambdaUpdateWrapper<Product>().eq(Product::getPreSellStatus, 1).lt(Product::getPreSellTime, new Date()));
        if (CollectionUtils.isEmpty(products)) {
            return null;
        }
        productMapper.updatePreSale(products);
        return products;
    }

//    @Override
//    public IPage<ProductTagParam> pageByLangAndTag(PageParam<Product> page, ProductTagParam product, Integer isContain) {
//        return productMapper.pageByLangAndTag(page,product,isContain);
//    }

    @Override
    public List<Product> listProdByIdsAndType(ProductParam product) {
        List<Product> productList = productMapper.listProdByIdsAndType(product);
        langManager.getProdLang(productList);
        return productList;
    }

    @Override
    public List<Product> getProductListBySpuIds(Set<Long> prodIds) {
        if (CollectionUtil.isEmpty(prodIds)) {
            return new ArrayList<>();
        }
        List<Product> prodList = productMapper.getProductListBySpuIds(prodIds);
        langManager.getProdLang(prodList);
        return prodList;
    }

    @Override
    public IPage<Product> canDistributionProdPage(PageParam<Product> page, ProductParam product) {
        IPage<Product> productPage = productMapper.canDistributionProdPage(page, product);
        langManager.getProdLang(productPage.getRecords());
        return productPage;
    }

    @Override
    public void updateProductType(Long prodId, Integer prodType) {
        if (Objects.isNull(prodId)) {
            return;
        }
        bathUpdateProductType(Collections.singletonList(prodId), prodType);
    }

    @Override
    public void bathUpdateProductType(List<Long> prodIds, Integer prodType) {
        if (CollUtil.isEmpty(prodIds)) {
            return;
        }
        productMapper.bathUpdateProductType(prodIds, prodType);
        removeProductCacheByProdIds(prodIds);
    }

    @Override
    public Map<Long, Product> getProdDeliveryTemplateIdByProdIds(List<Long> prodIds) {
        List<Product> products = productMapper.getProdDeliveryTemplateIdByProdIds(prodIds);
        return products.stream().collect(Collectors.toMap(Product::getProdId, p -> p));
    }

    @Override
    public int getProductNum(Long shopId, Integer status) {
        return productMapper.getProductNum(shopId, status);
    }

    @Override
    public List<ProductBO> listProdStockAndSold(List<Long> prodIds) {
        if (CollUtil.isEmpty(prodIds)) {
            return new ArrayList<>();
        }
        return productMapper.listEsProdSoldAndStock(prodIds);
    }


    @Override
    public void changeStatusByProdIds(List<Long> pIds, Integer status) {
        productMapper.changeStatusByProdIds(pIds, status);
    }

    @Override
    public IPage<Product> prodSkuPage(PageParam<Product> page, ProductParam productParam) {
        List<Product> products = productMapper.prodSkuPage(new PageAdapter(page), productParam);
        if (!CollectionUtils.isEmpty(products)) {
            langManager.getProdLang(products);
            // 查询库存
            List<SkuStockVO> skuStockVOList = new ArrayList<>(Constant.INITIAL_CAPACITY);
            for (Product product : products) {
                for (Sku sku : product.getSkuList()) {
                    skuStockVOList.add(new SkuStockVO(sku.getSkuId()));
                }
            }
        }
        page.setRecords(products);
        page.setTotal(productMapper.countProdSkuList(new PageAdapter(page), productParam));
        return page;
    }

    @Override
    public List<Product> prodAndSkuInfo(List<Long> prodIds, List<Long> skuIds) {
        List<Product> products = productMapper.prodAndSkuInfo(prodIds, skuIds);
        langManager.getProdLang(products);
        return products;
    }


    @Override
    public List<Product> listProdAndSku(List<Long> prodIds) {
        List<Product> products = productMapper.listProdAndSku(prodIds);
        langManager.getProdLang(products);
        List<Sku> skus = new ArrayList<>();
        for (Product product : products) {
            skus.addAll(product.getSkuList());
        }
        return products;
    }

    @Override
    public List<Product> listProdByAutidStatus() {
        List<Product> products = productMapper.selectList(new LambdaQueryWrapper<Product>().eq(Product::getStatus, ProdStatusEnums.AUDIT.getValue()));
        return products;
    }

    @Override
    public List<ProductVO> listProdWithLang(ProductDto productDto) {
        return productMapper.listProdWithLang(productDto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProdStatus(List<Long> prodIds, Integer prodStatus) {
        for (Long prodId : prodIds) {
            ProductServiceImpl productService = (ProductServiceImpl) AopContext.currentProxy();
            Product product = new Product();
            product.setProdId(prodId);
            product.setIsTop(0);

            productService.updateById(product);

            productService.removeProdCacheByProdId(prodId);
            List<String> userIds = basketService.listUserIdByProdId(prodId);
            //清除购物车缓存
            basketService.removeCacheByUserIds(userIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Product> handleExpireVirtualProd() {
        List<Product> products = list(new LambdaUpdateWrapper<Product>()
                .eq(Product::getMold, 1)
                .eq(Product::getStatus, ProdStatusEnums.NORMAL.getValue())
                .eq(Product::getWriteOffTime, 0)
                .lt(Product::getWriteOffEnd, new Date()));
        if (CollectionUtils.isEmpty(products)) {
            return null;
        }
        for (Product product : products) {
            product.setStatus(ProdStatusEnums.SHOP_OFFLINE.getValue());
            updateById(product);
        }
        return products;
    }

    private void setSkuName(List<ProdLangParam> prodLangList, List<Sku> skuList) {
        Map<Integer, String> prodNameMap = prodLangList.stream().collect(Collectors.toMap(ProdLangParam::getLang, ProdLangParam::getProdName));
        for (Sku sku : skuList) {
            for (SkuLang skuLang : sku.getSkuLangList()) {
                String prodName = prodNameMap.get(skuLang.getLang());
                if (StrUtil.isBlank(prodName)) {
                    continue;
                }
                if (StrUtil.isBlank(skuLang.getSkuName())) {
                    skuLang.setProdName(prodName);
                } else {
                    skuLang.setProdName(prodName + " " + skuLang.getSkuName());
                }
            }
        }
    }

    private void checkProdLang(List<ProdLangParam> prodLangList) {
        Integer lang = langManager.getDefaultLang();
        if (CollUtil.isEmpty(prodLangList)) {
            throw new YamiShopBindException("yami.product.exception.prodLangNull");
        }
        boolean containsMaster = false;
        for (ProdLangParam prodLangParam : prodLangList) {
            if (Objects.equals(prodLangParam.getLang(), lang)) {
                containsMaster = true;
            }
            prodLangParam.setProdName(prodLangParam.getProdName().trim());
            if (StrUtil.isNotBlank(prodLangParam.getBrief())) {
                prodLangParam.setBrief(prodLangParam.getBrief().trim());
            }
        }
        if (!containsMaster) {
            throw new YamiShopBindException("yami.product.exception.prodLangUpdate");
        }
    }


    private void saveLang(ProductParam productParam) {
        List<ProdLang> prodLangList = BeanUtil.mapAsList(productParam.getProdLangList(), ProdLang.class);
        for (ProdLang prodLang : prodLangList) {
            prodLang.setProdId(productParam.getProdId());
        }
        prodLangService.saveBatch(prodLangList);
    }

    private boolean updateProdSku(Product dbProduct, Product product, List<Long> dbSkuIds, Map<Long, Sku> dbSkuMap, List<Sku> skuList, boolean skuStatusChangeFlag, List<Sku> insertSkuList, List<Sku> addStockSkuList, List<Sku> reduceStockSkuList) {
        Long shopId = product.getShopId();
        // 更新的sku数量
        for (Sku sku : skuList) {
            sku.setIsDelete(0);
            // 如果数据库中原有sku就更新，否者就插入
            if (dbSkuIds.contains(sku.getSkuId())) {
                Sku dbSku = dbSkuMap.get(sku.getSkuId());
                if (!skuStatusChangeFlag) {
                    skuStatusChangeFlag = !Objects.equals(dbSku.getStatus(), sku.getStatus());
                }
                skuService.updateSku(sku, product.getMold());
            } else {
                insertSkuList.add(sku);
            }
        }
        // 批量插入sku
        if (CollectionUtil.isNotEmpty(insertSkuList)) {
            for (Sku sku : skuList) {
                sku.setProdId(product.getProdId());
            }
            skuService.insertBatchAndLang(insertSkuList, shopId, product.getMold(), product.getStatus(), product.getEmployeeMobile(), product.getEmployeeId());
        }


        return skuStatusChangeFlag;
    }


}
