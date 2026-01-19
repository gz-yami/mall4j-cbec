/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.shop.bean.app.vo.ProductVO;
import com.yami.shop.bean.bo.ProductBO;
import com.yami.shop.bean.dto.ProdAndSkuListsDto;
import com.yami.shop.bean.dto.ProductDto;
import com.yami.shop.bean.model.Product;
import com.yami.shop.bean.param.*;
import com.yami.shop.bean.vo.search.ProductSearchVO;
import com.yami.shop.common.util.PageAdapter;
import com.yami.shop.common.util.PageParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author yami
 */
public interface ProductMapper extends BaseMapper<Product> {


    /**
     * 通过商品ids列表获取商品列表及商品所有属性
     *
     * @param prodIds 商品ids
     * @return 商品列表
     */
    List<ProdAndSkuListsDto> getProdAndSkuLists(@Param("prodIds") List<Long> prodIds);

    /**
     * 更新商品为活动商品
     *
     * @param groupActivityId 拼团活动id
     * @param prodId          商品id
     * @return 更新结果
     */
    int updateProductToGroup(@Param("groupActivityId") Long groupActivityId, @Param("prodId") Long prodId);

    /**
     * 更新商品为待审核状态
     *
     * @param prodId 商品id
     * @return 更新结果
     */
    int updateToApply(@Param("prodId") Long prodId);

    /**
     * 更新商品为商家下架状态
     *
     * @param prodId 商品id
     * @return 更新结果
     */
    int updateToShopOffline(@Param("prodId") Long prodId);

    /**
     * 更新商品为下架状态
     *
     * @param prodId 商品id
     * @return 更新结果
     */
    int updateToOffline(@Param("prodId") Long prodId);

    /**
     * 下架店铺所有的商品
     *
     * @param shopIds 店铺id列表
     * @return 结果
     */
    int offlineProdByShopIds(@Param("shopIds") List<Long> shopIds);


    /**
     * 获取用于导出的商品列表
     *
     * @param prodIds 商品id列表
     * @return 商品列表
     */
    List<ProductExportParam> listProdsByProdParam(@Param("prodIds") List<Long> prodIds);

    /**
     * 通过分类ids改变商品为下架状态
     *
     * @param categoryIds 分类ids
     * @param shopId      店铺id
     */
    void offlineProdByCategoryId(@Param("categoryIds") List<Long> categoryIds, @Param("shopId") Long shopId);

    /**
     * 分页获取上架的商品信息
     *
     * @param page    分页信息
     * @param product 商品筛选信息
     * @return
     */
    IPage<Product> pageProducts(PageParam<Product> page, @Param("product") ProductParam product);

    /**
     * 在架商品数据,根据参数分页获取商品数据
     *
     * @param page    分页数据
     * @param param   筛选参数
     * @param prodIds 商品id列表
     * @return 商品数据
     */
    IPage<ProdEffectRespParam> pageProdByParam(PageParam<Product> page, @Param("param") ProdEffectParam param, @Param("prodIds") Set<Long> prodIds);


    /**
     * 在架商品数据,根据参数获取商品列表数据
     *
     * @param param   筛选参数
     * @param prodIds 商品id列表
     * @return 商品数据
     */
    List<ProdEffectRespParam> listProdByParam(@Param("param") ProdEffectParam param, @Param("prodIds") Set<Long> prodIds);

    /**
     * 支付商品件数
     *
     * @param prodId 商品id
     * @param param  参数
     * @return 支付商品件数
     */
    Long countPayNum(@Param("prodId") Long prodId, @Param("param") ProdEffectParam param);

    /**
     * 筛选获取商品分页列表
     *
     * @param page    分页数据
     * @param product 商品筛选信息
     * @return 商品分页列表
     */
    IPage<Product> pageByLang(PageParam<Product> page, @Param("product") ProductParam product);

    /**
     * 通过商品id和语言获取商品信息
     *
     * @param prodId 商品id
     * @return 商品信息
     */
    Product selectByIdAndLang(@Param("prodId") Long prodId);

    /**
     * 恢复普通商品
     *
     * @param products 商品信息
     */
    void updatePreSale(@Param("products") List<Product> products);

//    IPage<ProductTagParam> pageByLangAndTag(PageParam<Product> page, @Param("productTagParam") ProductTagParam productTagParam, @Param("isContain") Integer isContain);

    /**
     * 分页获取商品信息
     *
     * @param product 商品筛选信息
     * @return 分页获取商品信息
     */
    List<Product> listProdByIdsAndType(@Param("product") ProductParam product);

    /**
     * 通过商品ids获取商品列表
     *
     * @param prodIds 商品ids
     * @return 商品列表
     */
    List<Product> getProductListBySpuIds(@Param("prodIds") Set<Long> prodIds);

    /**
     * 分页获取可以添加到分销商品的数据
     *
     * @param page    分页信息
     * @param product 商品筛选信息
     * @return
     */
    IPage<Product> canDistributionProdPage(PageParam<Product> page, @Param("product") ProductParam product);

    /**
     * 获取商品信息
     *
     * @param prodIds
     * @return
     */
    List<ProductBO> getProductBO(@Param("prodIds") List<Long> prodIds);

    /**
     * 获取商品id列表
     *
     * @param categoryId
     * @param shopCategoryId
     * @param shopId
     * @return 商品id列表
     */
    List<Long> listProdId(@Param("categoryId") Long categoryId, @Param("shopCategoryId") Long shopCategoryId, @Param("shopId") Long shopId);

    /**
     * 根据品牌id，获取商品id列表
     *
     * @param brandId 品牌id
     * @return 商品id列表
     */
    List<Long> listIdByBrandId(@Param("brandId") Long brandId);

    /**
     * 批量改变商品状态（上下架）
     *
     * @param cids
     * @param status
     * @param shopId
     */
    void offlineProdByCategoryIdAndShopId(@Param("cids") List<Long> cids, @Param("status") Integer status, @Param("shopId") Long shopId);

    /**
     * 根据店铺id列表获取商品id列表
     *
     * @param shopIds
     * @return
     */
    List<Long> listIdByShopIds(@Param("shopIds") List<Long> shopIds);

    /**
     * 根据商品id列表修改商品状态
     *
     * @param pIds
     * @param status
     */
    void changeStatusByProdIds(@Param("pIds") List<Long> pIds, @Param("status") Integer status);

    /**
     * 获取商品及商品sku信息列表
     *
     * @param pageAdapter
     * @param product
     * @return
     */
    List<Product> prodSkuPage(@Param("page") PageAdapter pageAdapter, @Param("product") ProductParam product);

    /**
     * 统计商品数量
     *
     * @param pageAdapter
     * @param product
     * @return
     */
    long countProdSkuList(@Param("page") PageAdapter pageAdapter, @Param("product") ProductParam product);

    /**
     * 根据商品、sku Id， 获取商品信息
     *
     * @param prodIds
     * @param skuIds
     * @return
     */
    List<Product> prodAndSkuInfo(@Param("prodIds") List<Long> prodIds, @Param("skuIds") List<Long> skuIds);

    /**
     * 获取商品及sku
     *
     * @param prodIds
     * @return
     */
    List<Product> listProdAndSku(@Param("prodIds") List<Long> prodIds);

    /**
     * 检验商品是否存在
     *
     * @param prodIds
     * @return 返回未删除的商品id
     */
    List<Long> verifySpuExist(@Param("prodIds") List<Long> prodIds);

    /**
     * 批量更新商品类型
     *
     * @param prodIds
     * @param prodType
     */
    void bathUpdateProductType(@Param("prodIds") List<Long> prodIds, @Param("prodType") Integer prodType);

    /**
     * 获取时间范围内的新增商品数量列表
     *
     * @param param
     * @return
     */
    List<ProdAnalysisDataParam> listNewProdByTime(@Param("param") ProdAnalysisSurveyParam param);


    /**
     * 在架商品数据
     *
     * @param param
     * @param prodIds
     * @return
     */
    List<ProdEffectRespParam> getProdEffectByParam(@Param("param") ProdEffectParam param, @Param("prodIds") Set<Long> prodIds);

    /**
     * 根据商品id列表获取商品的配送模板id
     *
     * @param prodIds
     * @return
     */
    List<Product> getProdDeliveryTemplateIdByProdIds(@Param("prodIds") List<Long> prodIds);

    /**
     * 根据店铺id 商品状态 获取商品数量
     *
     * @param shopId
     * @param status
     * @return
     */
    int getProductNum(@Param("shopId") Long shopId, @Param("status") Integer status);

    /**
     * 获取es商品销量和库存数据
     *
     * @param prodIds
     * @return
     */
    List<ProductBO> listEsProdSoldAndStock(@Param("prodIds") List<Long> prodIds);

    /**
     * 获取商品列表
     *
     * @param product
     * @return
     */
    List<ProductBO> listSimple(@Param("product") ProductParam product);

    /**
     * 获取商品及其多语言集合
     * @param productDto 商品DTO
     * @return 商品VO
     */
    List<ProductVO> listProdWithLang(@Param("productDto") ProductDto productDto);

    /**
     * 获取用户的收藏商品列表
     * @param page
     * @param userId
     * @return
     */
    IPage<com.yami.shop.bean.app.dto.ProductDto> collectionProds(@Param("page") PageParam page, @Param("userId") String userId);

    /**
     * 分页查询Es商品信息
     * @param pageParam 分页参数
     * @param productParam 商品查询参数
     * @return Es商品分页数据
     */
    Page<ProductSearchVO> pageProduct(PageParam<ProductSearchVO> pageParam,
                                        @Param("param") EsProductParam productParam);
}
