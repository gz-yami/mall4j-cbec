/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.app.vo.ProductVO;
import com.yami.shop.bean.bo.ProductBO;
import com.yami.shop.bean.dto.ProdAndSkuListsDto;
import com.yami.shop.bean.dto.ProductDto;
import com.yami.shop.bean.model.Product;
import com.yami.shop.bean.param.*;
import com.yami.shop.bean.vo.search.EsProductSearchVO;
import com.yami.shop.bean.vo.search.ProductSearchVO;
import com.yami.shop.common.util.EsPageVO;
import com.yami.shop.common.util.PageParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商品
 * @author yami
 */
public interface ProductService extends IService<Product> {

    EsPageVO<ProductSearchVO> pageProduct(PageParam pageParam, EsProductParam productParam);

    /**
     * 保存商品
     *
     * @param productParam 商品信息
     */
    void saveProduct(ProductParam productParam);

    /**
     * 更新商品
     *
     * @param product 商品信息
     * @param dbProduct 原有商品信息
     */
    void updateProduct(ProductParam product, Product dbProduct);


    /**
     * 根据商品id获取商品信息(商品及商品名称国际化信息)
     *
     * @param prodId 商品id
     * @return 商品信息
     */
    Product getProductByProdId(Long prodId);

    /**
     * 根据商品id获取商品信息(商品及商品名称国际化信息)
     *
     * @param prodId 商品id
     * @return 商品信息
     */
    Product getProductAndLang(Long prodId);


    /**
     * 获取商品信息（商品和商品扩展）(缓存)
     * @param prodId
     * @return
     */
    Product getProductInfo(Long prodId);


    /**
     *获取商品信息（商品和商品扩展）
     * @param prodId
     * @return
     */
    Product getProductById(Long prodId);


    /**
     * 移除商品通过商品id
     *
     * @param prodId       商品id
     * @param deleteSkuIds
     */
    void removeProductByProdId(Long prodId, List<Long> deleteSkuIds);

    /**
     * 通过商品id和语言移除商品缓存
     * @param prodId 商品id
     */
    void removeProdCacheByProdId(Long prodId);

    /**
     * 批量移除商品缓存通过商品id列表
     * @param prodIds 商品id列表
     */
    void removeProductCacheByProdIds(List<Long> prodIds);


    /**
     * 通过商品列表获取商品信息及所有规格属性
     * @param prodIds 商品ids
     * @return 商品列表
     */
    List<ProdAndSkuListsDto> getProdAndSkuLists(List<Long> prodIds);

    /**
     * 更新商品为活动商品
     * @param groupActivityId 拼团活动id
     * @param prodId 商品id
     * @return
     */
    int updateProductToGroup(Long groupActivityId, Long prodId);


    /**
     * 分页获取上架的商品信息
     * @param page 分页信息
     * @param product 商品筛选信息
     * @return
     */
    IPage<Product> pageProducts(PageParam<Product> page, ProductParam product);

    /**
     * 筛选获取商品分页列表
     * @param page 分页数据
     * @param product 商品筛选信息
     * @return 商品分页列表
     */
    IPage<Product> pageByLang(PageParam<Product> page, ProductParam product);

    /**
     * 过了预售时间的商品，恢复成普通商品状态
     * @return 商品列表
     */
    List<Product> recoveryPreSaleProd();

//    IPage<ProductTagParam> pageByLangAndTag(PageParam<Product> page, ProductTagParam product, Integer isContain);
    /**
     * 获取商品信息列表
     * @param product 商品筛选信息
     * @return 分页获取商品信息
     */
    List<Product> listProdByIdsAndType(ProductParam product);

    /**
     * 通过商品ids获取商品列表
     * @param prodIds 商品ids
     * @return 商品列表
     */
    List<Product> getProductListBySpuIds(Set<Long> prodIds);

    /**
     * 批量修改商品状态
     * @param pIds
     * @param status
     */
    void changeStatusByProdIds(List<Long> pIds, Integer status);

    /**
     * 分页获取商品及商品sku信息
     * @param page
     * @param product
     * @return
     */
    IPage<Product> prodSkuPage(PageParam<Product> page, ProductParam product);

    /**
     * 获取核销时间过期的虚拟商品并进行下架操作
     * @return 过期的虚拟商品
     */
    List<Product> handleExpireVirtualProd();

    /**
     * 根据商品、sku Id， 获取商品信息
     * @param prodIds
     * @param skuIds
     * @return
     */
    List<Product> prodAndSkuInfo(List<Long> prodIds, List<Long> skuIds);

    /**
     * 获取商品及sku列表
     * @param prodIds
     * @return
     */
    List<Product> listProdAndSku(List<Long> prodIds);

    /**
     * 查询可以添加到分销商品的数据
     * @param page
     * @param product
     * @return
     */
    IPage<Product> canDistributionProdPage(PageParam<Product> page, ProductParam product);

    /**
     * 更新商品类型
     * @param prodId
     * @param prodType
     */
    void updateProductType(Long prodId, Integer prodType);

    /**
     * 批量更新商品类型
     * @param prodIds
     * @param prodType
     */
    void bathUpdateProductType(List<Long> prodIds, Integer prodType);

    /**
     * 根据商品id列表获取商品的配送模板id
     * @param prodIds
     * @return
     */
    Map<Long, Product> getProdDeliveryTemplateIdByProdIds(List<Long> prodIds);

    /**
     * 根据店铺Id 商品状态 获取商品总数量
     * @param shopId
     * @param status
     * @return
     */
    int getProductNum(Long shopId, Integer status);

    /**
     * 获取商品库存和销量列表
     * @param prodIds
     * @return
     */
    List<ProductBO> listProdStockAndSold(List<Long> prodIds);

    /**
     * 获取所有待审核的商品
     * @return
     */
    List<Product> listProdByAutidStatus();

    /**
     * 获取商品及其多语言集合
     * @param productDto 商品DTO
     * @return 商品VO
     */
    List<ProductVO> listProdWithLang(ProductDto productDto);

    /**
     * 修改商品状态
     * @param prodIds
     * @param prodStatus
     */
    void updateProdStatus(List<Long> prodIds, Integer prodStatus);

}
