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
import com.yami.shop.bean.app.dto.SkuDto;
import com.yami.shop.bean.app.vo.SkuVO;
import com.yami.shop.bean.model.Product;
import com.yami.shop.bean.model.Sku;
import com.yami.shop.bean.param.ProductParam;
import com.yami.shop.bean.vo.SkuStockVO;
import com.yami.shop.common.util.PageParam;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 *
 * @author lgh on 2018/09/29.
 */
public interface SkuService extends IService<Sku> {

    /**
     * 根据商品id获取商品中的sku列表（将会被缓存起来）(如果不需要sku库存、skuName信息，就使用该接口)
     * 从缓存中获取的skuName并不是当前语言的，如果需要skuName，建议用 listByProdId
     * 注意：该接口只有sku和sku国际化信息，不包含sku库存信息
     *
     * @param prodId 商品id
     * @return sku列表
     */
    List<Sku> listSkuByProdId(Long prodId);

    /**
     * 根据商品id获取商品中的sku列表
     * 从缓存中获取的sku数据，并对skuName进行了处理,是当前语言的
     * 注意：该接口只有sku和sku国际化信息，不包含sku库存信息
     * @param prodId 商品id
     * @return sku列表
     */
    List<Sku> listSkuAndSkuNameByProdId(Long prodId);

    /**
     * 获取sku、sku国际化、sku库存信息列表
     *
     * @param prodId 商品id
     * @param mold
     * @return sku列表
     */
    List<Sku> listSkuAndSkuStock(Long prodId, Integer mold);


    /**
     * 获取sku信息、库存及语言列表
     *
     * @param prodId
     * @param mold
     * @return
     */
    List<Sku> listSkuAndSkuStockAndLang(Long prodId, Integer mold);

    /**
     * 获取启用的sku、sku国际化、sku库存信息列表 （只获取启动状态的sku，不包含禁用的sku）
     * 在listSkuAndSkuStock() 的基础上过滤掉禁用的sku
     *
     * @param prodId 商品id
     * @param mold
     * @return sku列表
     */
    List<Sku> listPutOnSkuAndSkuStock(Long prodId, Integer mold);

    /**
     * 根据skuId获取sku及库存信息（将会被缓存起来, 不需要skuName时使用）
     * skuName并不是当前语言的，如果需要当前语言的skuName，建议使用getSkuAndName
     *
     * @param skuId shuId
     * @return
     */
    Sku getSkuBySkuId(Long skuId);

    /**
     * 根据id获取sku语言列表
     * @param skuId
     * @return
     */
    Sku getSkuListBySkuId( Long skuId);

    /**
     * 删除sku的缓存（包含sku、sku列表、sku库存的缓存）
     *
     * @param skuId skuId
     * @param prodId 商品id
     */
    void removeSkuCacheBySkuId(Long skuId, Long prodId);

    /**
     * 删除sku库存的缓存（包含sku、sku库存的缓存）
     * @param prodId 商品id
     * @param skuId skuId
     */
    void removeSkuStockCache(Long skuId, Long prodId);

    /**
     * 删除sku库存的缓存（包含sku、sku库存的缓存）
     * @param skuMap 商品id
     */
    void batchRemoveSkuStockCache(Map<Long, Long > skuMap);

    /**
     * 更新sku
     *
     * @param sku  sku
     * @param mold
     */
    void updateSku(Sku sku, Integer mold);

    /**
     * 批量保存sku、sku语言和sku库存表
     *
     * @param skuList sku列表
     * @param mold
     * @param shopId
     * @param spuStatus
     * @param mobile
     * @param employeeId
     * @return
     */
    List<SkuStockVO> insertBatchAndLang(List<Sku> skuList, Long shopId, Integer mold, Integer spuStatus, String mobile, Long employeeId);

    /**
     * 分页获取sku
     * @param page
     * @param product
     * @return
     */
    IPage<Sku> pageSku(PageParam<Sku> page, ProductParam product);

    /**
     * 根据商品id列表获取有商品编号的
     * @param prodIds 获取商品编号的商品id列表
     * @param disProdId  过滤掉的商品
     * @return
     */
    List<String> listSkuByProdIds(List<Long> prodIds,Long disProdId);

    /**
     * 根据商品id列表获取有商品编号的
     * @param prodIds 获取商品编号的商品id列表
     * @return
     */
    List<Sku> listSkuAndLangByProdIds(List<Long> prodIds);

    /**
     * 根据编码获取sku信息(sku、商品名称、sku库存信息)
     * @param shopId 店铺id
     * @param partyCode 商品编码
     * @param notProdType 不包含商品类型
     * @return
     */
    Sku getSkuByShopIdAndPartyCode(Long shopId, String partyCode, Integer notProdType);

    /**
     * 获取指定商品的sku列表（包括sku库存信息等）（仅获取启用状态）
     * @param product
     * @return
     */
    List<SkuDto> getProdDetailSkuInfo(Product product);

    /**
     * 或者指定的sku列表
     * @param skuIds
     * @return
     */
    List<Sku> listSkuBySkuIds(List<Long> skuIds);

    /**
     * 获取库存预警数量
     * @param shopId 店铺id
     * @return
     */
    Long stockWarningCount(Long shopId);

    /**
     * 获取sku及其多语言集合
     * @param skuDto skuDTO
     * @return skuVO集合
     */
    List<SkuVO> listSkuWithLang(SkuDto skuDto);

    /**
     * 扣减sku库存
     * @param skuCountMap skuId与扣减数量
     */
    void deductSkuStock(Map<Long, Integer> skuCountMap);

    /**
     * 获取指定商品id的sku库存信息
     * @param prodIds
     * @return
     */
    Map<Long, Integer> listSkuStock(List<Long> prodIds);
}
