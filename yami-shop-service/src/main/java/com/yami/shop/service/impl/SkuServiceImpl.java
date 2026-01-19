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
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.app.dto.SkuDto;
import com.yami.shop.bean.app.vo.SkuVO;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.param.ProductParam;
import com.yami.shop.bean.vo.*;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.constants.CacheNames;
import com.yami.shop.common.enums.StatusEnum;
import com.yami.shop.common.enums.SysTypeEnum;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ResponseEnum;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.*;
import com.yami.shop.dao.SkuLangMapper;
import com.yami.shop.dao.SkuMapper;
import com.yami.shop.manager.impl.LangManager;

import com.yami.shop.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author lgh on 2018/09/29.
 */
@Slf4j
@Service
@AllArgsConstructor
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService {

    private final Logger logger = LoggerFactory.getLogger(SkuServiceImpl.class);
    private final SkuMapper skuMapper;
    private final SkuLangMapper skuLangMapper;
    private final SkuLangService skuLangService;
    private final LangManager langManager;

    @Override
    @Cacheable(cacheNames = CacheNames.SKU_LIST_KEY,  key = "#prodId")
    public List<Sku> listSkuByProdId(Long prodId) {
        List<Sku> skuList = skuMapper.listByProdId(prodId);
        langManager.getSkuAndLang(skuList);
        if(CollectionUtils.isEmpty(skuList)){
            return null;
        }
        return skuList;
    }

    @Override
    public List<Sku> listSkuAndSkuNameByProdId(Long prodId) {
        SkuServiceImpl skuService = (SkuServiceImpl) AopContext.currentProxy();
        List<Sku> skuList = skuService.listSkuByProdId(prodId);
        langManager.handleSkuLang(skuList);
        return skuList;
    }

    @Override
    public List<Sku> listSkuAndSkuStock(Long prodId, Integer mold) {
        List<Sku> skuList = this.listSkuByProdId(prodId);
        if(CollectionUtils.isEmpty(skuList)){
            logger.info("商品不存在或已被删除，返回空");
            return null;
        }
        for (Sku sku : skuList) {
            sku.setSkuLangList(null);
        }
        return skuList;
    }

    @Override
    public List<Sku> listSkuAndSkuStockAndLang(Long prodId, Integer mold) {
        SkuServiceImpl skuService = (SkuServiceImpl) AopContext.currentProxy();
        List<Sku> skuList = skuService.listSkuByProdId(prodId);
        if(CollectionUtils.isEmpty(skuList)){
            logger.info("商品不存在或已被删除，返回空");
            return null;
        }
        for (Sku sku : skuList) {
            sku.setMold(mold);
        }
        if (CollUtil.isEmpty(skuList)) {
            return skuList;
        }
        return skuList;
    }

    @Override
    public List<Sku> listPutOnSkuAndSkuStock(Long prodId, Integer mold) {
        List<Sku> skus = listSkuAndSkuStock(prodId,mold);
        if (Objects.isNull(skus)) {
            return new ArrayList<>();
        }
        // 这个接口都是用户端在用，不需要返回完整的国际化信息
        for (Sku sku : skus) {
            sku.setSkuLangList(null);
        }
        return skus.stream().filter(sku -> Objects.equals(sku.getStatus(), StatusEnum.ENABLE.value())).collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = CacheNames.SKU_KEY, key = "#skuId")
    public Sku getSkuBySkuId(Long skuId) {
        Sku sku = skuMapper.getSkuBySkuId(skuId);
        langManager.getSkuAndLang(sku);

        return sku;
    }


    @Override
    public Sku getSkuListBySkuId(Long skuId) {
        Sku sku = skuMapper.getSkuBySkuId(skuId);
        langManager.getSkuAndLang(sku);
        return sku;
    }

    @Override
    public void removeSkuCacheBySkuId(Long skuId, Long prodId) {
        List<String> keys = new ArrayList<>();
        // cacheNames = "sku"
        keys.add(CacheNames.SKU_KEY + CacheNames.UNION + skuId);
        // cacheNames = "skuList"
        keys.add(CacheNames.SKU_LIST_KEY + CacheNames.UNION + prodId);
        keys.add(CacheNames.SKU_LIST_KEY + CacheNames.UNION + prodId);
        // cacheNames = "listSkuStockByProdId",
        keys.add(CacheNames.LIST_SKU_STOCK_KEY + CacheNames.UNION + prodId);
        RedisUtil.del(keys);
    }

    @Override
    public void removeSkuStockCache(Long skuId, Long prodId) {
        List<String> keys = new ArrayList<>();
        // cacheNames = "sku"
        keys.add(CacheNames.SKU_KEY + CacheNames.UNION + skuId);
        // cacheNames = "listSkuStockByProdId",
        keys.add(CacheNames.LIST_SKU_STOCK_KEY + CacheNames.UNION + prodId);
        RedisUtil.del(keys);
    }

    @Override
    public void batchRemoveSkuStockCache(Map<Long, Long > skuMap) {
        if (MapUtil.isEmpty(skuMap)) {
            return;
        }
        List<String> keys = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : skuMap.entrySet()) {
            // cacheNames = "sku"
            keys.add(CacheNames.SKU_KEY + CacheNames.UNION + entry.getKey());
            Long prodId = entry.getValue();
            // cacheNames = "skuList"
            keys.add(CacheNames.SKU_LIST_KEY + CacheNames.UNION + prodId);
            keys.add(CacheNames.SKU_LIST_KEY + CacheNames.UNION + prodId);
            // cacheNames = "listSkuStockByProdId",
            keys.add(CacheNames.LIST_SKU_STOCK_KEY + CacheNames.UNION + prodId);
        }
        RedisUtil.del(keys);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSku(Sku sku, Integer mold) {
        // 移除空字符串
        if (Objects.nonNull(sku) && StrUtil.isBlank(sku.getPic())) {
            sku.setPic(null);
        }
        sku.setUpdateTime(new Date());
        Integer changeStock = sku.getChangeStock();
        // 设置库存为null, 不更新库存
        Integer tempStock = sku.getStocks();
        // 更新sku,该方法不更新库存
        skuMapper.updateSkuById(sku);
        skuLangMapper.batchUpdateBySkuIdAndLang(sku.getSkuLangList());

    }

    @Override
    public List<SkuStockVO> insertBatchAndLang(List<Sku> skuList, Long shopId, Integer mold, Integer spuStatus, String mobile, Long employeeId) {
        // 移除空字符串
        for (Sku sku : skuList) {
            if (StrUtil.isBlank(sku.getPic()) && Objects.nonNull(sku)) {
                sku.setPic(null);
            }
        }
        skuMapper.insertBatchReturnId(skuList);
        // 保存语言表
        saveSkuLang(skuList);
        return null;
    }

    /**
     * 保存sku库存信息
     * @param skuList
     */
    private List<SkuStockVO> saveSkuStocks(List<Sku> skuList, Long shopId, Integer spuMold, Integer spuStatus, Long employeeId, String mobile) {
        Long prodId = skuList.get(0).getProdId();
        Integer sysType = SysTypeEnum.PLATFORM.value();
        List<SkuStockVO> skuStocks = new ArrayList<>(Constant.INITIAL_CAPACITY);

        return skuStocks;
    }

    @Override
    public List<String> listSkuByProdIds(List<Long> prodIds,Long disProdId) {
        if (CollectionUtils.isNotEmpty(prodIds)){
            return skuMapper.listSkuPartyCodeByProdIds(prodIds,disProdId);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Sku> listSkuAndLangByProdIds(List<Long> prodIds) {
        List<Sku> skuList = skuMapper.listSkuAndLangByProdIds(prodIds);
        langManager.handleSkuAndLang(skuList);
        return skuList;
    }

    @Override
    public Sku getSkuByShopIdAndPartyCode(Long shopId, String partyCode, Integer notProdType) {
        Sku sku = skuMapper.getSkuByPartyCode(shopId, partyCode, notProdType);
        if (Objects.isNull(sku)) {
            return null;
        }
        langManager.getProdNameToSku(sku);
        return sku;
    }





    @Override
    public List<SkuDto> getProdDetailSkuInfo(Product product) {
        List<Sku> skuList = skuMapper.getProdDetailSkuInfo(product.getProdId());
        if (CollUtil.isEmpty(skuList)) {
            return new ArrayList<>();
        }
        langManager.getSkuLang(skuList);
        return BeanUtil.mapAsList(skuList, SkuDto.class);
    }

    private void setColumnWidth(Sheet sheet) {
        int index = -1;
        // 商品编码
        sheet.setColumnWidth(++index, 50 * 256);
        // 商品名称
        sheet.setColumnWidth(++index, 80 * 256);
        // 规格
        sheet.setColumnWidth(++index, 50 * 256);
        // 商品库存
        sheet.setColumnWidth(++index, 20 * 256);
        // 商品价格
        sheet.setColumnWidth(++index, 20 * 256);
    }

    @Override
    public IPage<Sku> pageSku(PageParam<Sku> page, ProductParam product) {
        IPage<Sku> skuPage = skuMapper.pageSku(page, product);
        langManager.getSkuLang(skuPage.getRecords());
        // 插入库存
        skuPage.getRecords().forEach(sku -> sku.setDeliveryModeVO(Json.parseObject(sku.getDeliveryMode(), Product.DeliveryModeVO.class)));
        return skuPage;
    }

    private void saveSkuLang(List<Sku> skuList) {
        List<SkuLang> skuLangList = new ArrayList<>();
        for (Sku sku : skuList) {
            checkProdLang(sku.getSkuLangList());
            for (SkuLang skuLang : sku.getSkuLangList()) {
                skuLang.setSkuId(sku.getSkuId());
            }
            skuLangList.addAll(sku.getSkuLangList());
        }
        skuLangService.saveBatch(skuLangList);
    }

    private void checkProdLang(List<SkuLang> skuLangList) {
        Integer lang = langManager.getDefaultLang();
        boolean containsMaster = false;
        for (SkuLang skuLang : skuLangList) {
            if (Objects.equals(skuLang.getLang(), lang)) {
                containsMaster = true;
            }
        }
        if (!containsMaster) {
            // 语言数据已更新，请重新录入商品信息
            throw new YamiShopBindException("yami.sku.exception.langUpdate");
        }
    }

    @Override
    public List<Sku> listSkuBySkuIds(List<Long> skuIds) {
        if (CollUtil.isEmpty(skuIds)) {
            return new ArrayList<>();
        }
        return skuMapper.listSkuBySkuIds(skuIds);
    }

    @Override
    public Long stockWarningCount(Long shopId) {
        List<Sku> skus = skuMapper.stockWarningCount(shopId);
        long count = skus.stream().filter(sku -> Objects.nonNull(sku.getStockWarning()) && sku.getStocks() <= sku.getStockWarning()).count();
        return count;
    }

    @Override
    public List<SkuVO> listSkuWithLang(SkuDto skuDto) {
        return skuMapper.listSkuWithLang(skuDto);
    }

    /**
     * 扣减sku库存
     * @param skuCountMap skuId与扣减数量
     */
    @Override
    public void deductSkuStock(Map<Long, Integer> skuCountMap) {
        // 校验扣减参数是否为空
        if (MapUtil.isEmpty(skuCountMap)) {
            return;
        }
        List<Long> skuIds = new ArrayList<>(skuCountMap.keySet());
        List<Sku> skuList = skuMapper.listSkuBySkuIds(skuIds);
        Map<Long, Sku> skuMap = skuList.stream().collect(Collectors.toMap(Sku::getSkuId, sku -> sku));
        // 遍历扣减明细，逐个校验并扣减库存
        for (Map.Entry<Long, Integer> entry : skuCountMap.entrySet()) {
            Long skuId = entry.getKey();
            Integer count = entry.getValue();
            // 跳过无效的扣减数量
            if (Objects.isNull(count) || count <= 0) {
                continue;
            }
            Sku sku = skuMap.get(skuId);
            // sku不存在直接返回库存不足
            if (Objects.isNull(sku)) {
                throw new YamiShopBindException(ServerResponseEntity.fail(ResponseEnum.NOT_STOCK, skuId));
            }
            Integer stock = sku.getStocks();
            // -1表示无限库存，不做扣减
            if (Objects.equals(stock, -1)) {
                continue;
            }
            // 校验库存不足
            if (Objects.isNull(stock) || stock < count) {
                throw new YamiShopBindException(ServerResponseEntity.fail(ResponseEnum.NOT_STOCK, skuId));
            }
            int updateCount = skuMapper.deductStock(skuId, count);
            // 并发情况下扣减失败，返回库存不足
            if (updateCount < 1) {
                throw new YamiShopBindException(ServerResponseEntity.fail(ResponseEnum.NOT_STOCK, skuId));
            }
        }
    }

    @Override
    public Map<Long, Integer> listSkuStock(List<Long> prodIds) {
        if (CollUtil.isEmpty(prodIds)) {
            return new HashMap<>();
        }

        List<Sku> skuList = this.lambdaQuery().in(Sku::getProdId, prodIds).eq(Sku::getIsDelete, 0).select(Sku::getProdId, Sku::getStocks).list();
        return skuList.stream().collect(Collectors.groupingBy(Sku::getProdId,
                        Collectors.summingInt(sku -> Objects.isNull(sku.getStocks()) ? 0 : sku.getStocks())
                ));
    }



}
