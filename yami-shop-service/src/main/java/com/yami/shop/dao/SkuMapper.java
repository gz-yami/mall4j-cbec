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
import com.yami.shop.bean.app.dto.SkuDto;
import com.yami.shop.bean.app.vo.SkuVO;
import com.yami.shop.bean.model.Sku;
import com.yami.shop.bean.param.ProductParam;
import com.yami.shop.common.util.PageParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author yami
 */
public interface SkuMapper extends BaseMapper<Sku> {

    /**
     * 获取商品的sku列表 (不包含库存信息)
     *
     * @param prodId 商品id
     * @return sku列表
     */
    List<Sku> listByProdId(@Param("prodId") Long prodId);

    /**
     * 删除商品
     *
     * @param prodId 商品id
     */
    void deleteByProdId(@Param("prodId") Long prodId);

    /**
     * 批量新增sku并且返回自增主键
     *
     * @param skuList sku列表
     */
    void insertBatchReturnId(@Param("skuList") List<Sku> skuList);

    /**
     * 获取sku 不用语言条件下
     *
     * @param skuId skuId
     * @return
     */
    Sku getSkuBySkuId(@Param("skuId") Long skuId);

    /**
     * 根据skuId列表查找库存信息，赠品使用无缓存
     *
     * @param skuIds
     * @return
     */
    List<Sku> getSkuBySkuIds(@Param("skuIds") List<Long> skuIds);

    /**
     * 根据商品id列表获取有商品编号的
     *
     * @param prodIds   获取商品编号的商品id列表
     * @param disProdId 过滤掉的商品
     * @return
     */
    List<String> listSkuPartyCodeByProdIds(@Param("prodIds") List<Long> prodIds, @Param("disProdId") Long disProdId);

    /**
     * 获取指定商品的sku列表
     *
     * @param prodIds   获取商品编号的商品id列表
     * @return
     */
    List<Sku> listSkuByProdIds(@Param("prodIds") List<Long> prodIds);


    /**
     * 分页获取sku
     *
     * @param page
     * @param product
     * @return
     */
    IPage<Sku> pageSku(@Param("page") PageParam<Sku> page, @Param("product") ProductParam product);

    /**
     * 根据编码获取sku信息
     *
     * @param shopId 店铺id
     * @param partyCode 商品编码
     * @param notProdType 不包含商品类型
     * @return
     */
    Sku getSkuByPartyCode(@Param("shopId") Long shopId,
                          @Param("partyCode") String partyCode,
                          @Param("notProdType") Integer notProdType);

    /**
     * 更新sku,该方法不更新库存
     *
     * @param sku
     */
    void updateSkuById(@Param("sku") Sku sku);


    /**
     * 获取导出sku列表
     *
     * @param product
     * @return
     */
    List<Sku> listExportSku(@Param("product") ProductParam product);

    /**
     * 获取指定商品的sku列表（包括sku库存信息等）（仅获取启用状态）
     *
     * @param prodId
     * @return
     */
    List<Sku> getProdDetailSkuInfo(@Param("prodId") Long prodId);

    /**
     * 或者指定的sku列表
     *
     * @param skuIds
     * @return
     */
    List<Sku> listSkuBySkuIds(@Param("skuIds") List<Long> skuIds);

    /**
     * 获取库存预警数量
     * @param shopId 店铺id
     * @return
     */
    List<Sku> stockWarningCount(@Param("shopId") Long shopId);

    /**
     * 获取sku和语言信息
     *
     * @param prodIds
     * @return
     */
    List<Sku> listSkuAndLangByProdIds(@Param("prodIds") List<Long> prodIds);

    /**
     * 获取sku及其多语言集合
     * @param skuDto skuDTO
     * @return skuVO集合
     */
    List<SkuVO> listSkuWithLang(@Param("skuDto") SkuDto skuDto);

    /**
     * 批量更新库存预警状态
     *
     * @param skuList
     */
    void batchUpdateSkuWarning(@Param("skuList") List<Sku> skuList);

    /**
     * 扣减sku库存
     *
     * @param skuId skuId
     * @param count 扣减数量
     * @return 影响行数
     */
    int deductStock(@Param("skuId") Long skuId, @Param("count") Integer count);

    /**
     * sku及单品列表
     *
     * @param prodIds
     * @return
     */
    List<Sku> listSkuAndComboByProdIds(@Param("prodIds") List<Long> prodIds);

    /**
     * 分页获取sku
     * @param page
     * @return
     */
    IPage<Sku> page(@Param("page") PageParam<Sku> page);

    /**
     * 更新sku库存
     * @param sku
     * @return
     */
    int updateStocks(@Param("sku") Sku sku);
}
