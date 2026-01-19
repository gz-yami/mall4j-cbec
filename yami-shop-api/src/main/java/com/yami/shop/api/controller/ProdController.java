/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.api.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.app.dto.ProductDto;
import com.yami.shop.bean.app.dto.SkuDto;
import com.yami.shop.bean.app.vo.ProductVO;
import com.yami.shop.bean.dto.ApiProdDto;
import com.yami.shop.bean.enums.DvyType;
import com.yami.shop.bean.enums.ProdStatusEnums;
import com.yami.shop.bean.enums.ProdType;
import com.yami.shop.bean.event.CalculateTransfeeEvent;
import com.yami.shop.bean.event.CheckAddrEvent;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.param.EsProductParam;
import com.yami.shop.bean.param.ProductParam;
import com.yami.shop.bean.vo.search.EsProductSearchVO;
import com.yami.shop.bean.vo.search.ProductSearchVO;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.common.util.EsPageVO;
import com.yami.shop.common.util.Json;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author LGH
 */
@RestController
@RequestMapping("/prod")
@Tag(name = "商品接口")
@AllArgsConstructor
public class ProdController {

    private final ProductService prodService;
    private final CurrencyService currencyService;
    private final CategoryService categoryService;
    private final ApplicationContext applicationContext;
    private final SkuService skuService;
    private final ShopDetailService shopDetailService;
    private final ProdParameterService prodParameterService;
    private final ApplicationEventPublisher eventPublisher;


    @GetMapping("/prodInfo")
    @Operation(summary = "商品详情信息" , description = "根据商品ID（prodId）获取商品信息")
    @Parameter(name = "prodId", description = "商品ID" , required = true)
    public ServerResponseEntity<ProductVO> prodInfo(Long prodId, String userId, @RequestParam(name = "addrId", defaultValue = "0")Long addrId, @RequestParam(name = "dvyType", defaultValue = "1")Integer dvyType) {
        Product product = prodService.getProductInfo(prodId);
        if (product == null || product.getStatus() != 1) {
            // 商品已下线
            throw new YamiShopBindException("yami.product.off.shelves");
        }
        product.setProdLangList(null);
        // 启用的sku列表
        List<Sku> skuList = skuService.listPutOnSkuAndSkuStock(prodId,product.getMold());
        int sale = 0;
        int stock = 0;
        for (Sku sku : skuList) {
            // sale += sku.getSaleNum();
            stock += sku.getStocks();
        }
        // product.setSoldNum(sale);
        product.setTotalStocks(stock);
        product.setSkuList(skuList);
        ProductVO productVO = BeanUtil.map(product, ProductVO.class);
        //获取运费信息
        applicationContext.publishEvent(new CalculateTransfeeEvent(productVO, addrId, userId));
        if(Objects.equals(productVO.getProdType(), ProdType.PROD_TYPE_ACTIVE.value()) || productVO.getMold() == 1 || dvyType == DvyType.STATION.value()){
            productVO.setIsDelivery(true);
        }else{
            // 判断用户默认地址是否在配送区域内以及该区域仓库是否充足
            applicationContext.publishEvent(new CheckAddrEvent(addrId, userId, productVO, null, dvyType));
        }
        // 普通商品有多种物流，需要加载物流模板信息
//        if (Objects.equals(productVO.getProdType(), ProdType.PROD_TYPE_NORMAL.value())) {
        loadDeliveryMode(product.getDeliveryMode(), productVO);
//        }
        // 商品参数列表
        List<ProdParameter> prodParameters = prodParameterService.listParameterAndLang(prodId);
        productVO.setProdParameterList(prodParameters);
        return ServerResponseEntity.success(productVO);
    }

    @GetMapping("/skuList")
    @Operation(summary = "sku信息" , description = "根据商品ID（prodId）单独获取sku信息")
    @Parameter(name = "prodId", description = "商品ID" , required = true)
    public ServerResponseEntity<List<SkuDto>> skuList(@RequestParam("prodId") Long prodId) {
        Product product = prodService.getProductByProdId(prodId);
        return ServerResponseEntity.success(skuService.getProdDetailSkuInfo(product));
    }

    @GetMapping("/isStatus")
    @Operation(summary = "校验商品是否下架" , description = "根据商品ID（prodId）校验商品是否下架")
    @Parameter(name = "prodId", description = "商品ID" , required = true)
    public ServerResponseEntity<Boolean> isStatus(Long prodId) {
        Product product = prodService.getProductByProdId(prodId);
        if (product == null || product.getStatus() != 1) {
            return ServerResponseEntity.success(false);
        }
        return ServerResponseEntity.success(true);
    }

    @GetMapping("/listProdByIdsAndType")
    @Operation(summary = "获取商品信息" , description = "根据商品ids获取商品信息")
    public ServerResponseEntity<List<ProductDto>> listProdByIdsAndType(ApiProdDto apiProdDto) {
        ProductParam productParam = BeanUtil.map(apiProdDto, ProductParam.class);
        apiProdDto.setLang(I18nMessage.getDbLang());
        apiProdDto.setStatus(ProdStatusEnums.NORMAL.getValue());
        List<Product> products = prodService.listProdByIdsAndType(productParam);
        List<ProductDto> productDtos = BeanUtil.mapAsList(products, ProductDto.class);
        return ServerResponseEntity.success(productDtos);
    }


    /**
     *  用户未登录情况下的商品推荐
     * @param page
     * @param productParam
     * @return
     */
    @GetMapping("/recommendList")
    @Operation(summary = "推荐商品列表" , description = "根据商品ID（prodId）获取商品信息")
    public ServerResponseEntity<EsPageVO<ProductSearchVO>> recommendList(PageParam page, ProductParam productParam) {
        EsProductParam esProductParam = new EsProductParam();
        if (Objects.isNull(productParam.getProdType())) {
            esProductParam.setProdType(ProdType.PROD_TYPE_NORMAL.value());
        }
        Long primaryCategoryId = null;
        List<Category> categoryList = categoryService.getCategoryAndParent(productParam.getCategoryId());
        if (CollUtil.isNotEmpty(categoryList)) {
            primaryCategoryId = categoryList.get(0).getCategoryId();
        }
        esProductParam.setPrimaryCategoryId(primaryCategoryId);
        //如果有商品id则过滤掉
        if (Objects.nonNull(productParam.getProdId())) {
            List<Long> prodIds = new ArrayList<>();
            prodIds.add(productParam.getProdId());
            esProductParam.setSpuIdsExclude(prodIds);
        }
        PageParam esPageParam = new PageParam();
        esPageParam.setCurrent((int) page.getCurrent());
        esPageParam.setSize((int) page.getSize());
        EsPageVO<ProductSearchVO> productPage = prodService.pageProduct(esPageParam, esProductParam);
        List<ProductSearchVO> products = productPage.getRecords();
        long current = page.getCurrent();
        long size = page.getSize();
        int spuNum = products.size();
        // 推荐商品的数量不足时，查询额外的商品进行填充
        if (Objects.equals(current , 1L) && size > spuNum) {
            // 分页后的空数据集合非List下的类，无法进行addAll操作，需重新创建一个List集合
            if (Objects.equals(products.size(), 0)) {
                products = new ArrayList<>();
            }
            esPageParam.setSize(Math.toIntExact(size - spuNum));
            esPageParam.setCurrent(1);
            // 查询该分类以外的商品
            esProductParam.setPrimaryCategoryId(null);
            esProductParam.setNotPrimaryCategoryId(primaryCategoryId);
            EsPageVO<ProductSearchVO> subProductPage = prodService.pageProduct(esPageParam, esProductParam);
            products.addAll(subProductPage.getRecords());
            productPage.setTotal((long) products.size());
            productPage.setPages(productPage.getTotal() > 0 ? (int)page.getCurrent() : 0);
            productPage.setRecords(products);
        }
        EsProductSearchVO esProductSearchVO = new EsProductSearchVO();
        esProductSearchVO.setProducts(products);
        return ServerResponseEntity.success(productPage);
    }

    /**
     * 加载商品物流模板
     * @param deliveryMode
     * @param productVO
     */
    private void loadDeliveryMode(String deliveryMode, ProductVO productVO) {
        // 物流模板
        Product.DeliveryModeVO deliveryModeVO = Json.parseObject(deliveryMode, Product.DeliveryModeVO.class);

        productVO.setDeliveryModeVO(deliveryModeVO);
    }
}
