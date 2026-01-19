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
import cn.hutool.core.util.StrUtil;
import com.yami.shop.bean.app.dto.DvyTypeDTO;
import com.yami.shop.bean.app.dto.ShopCartItemDto;
import com.yami.shop.bean.enums.*;
import com.yami.shop.bean.model.Product;
import com.yami.shop.bean.model.ShopDetail;
import com.yami.shop.bean.model.Sku;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.enums.StatusEnum;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.Arith;
import com.yami.shop.common.util.Json;
import com.yami.shop.dao.BasketMapper;
import com.yami.shop.manager.impl.LangManager;
import com.yami.shop.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 购物车项数据组装
 *
 * @author FrozenWatermelon
 * @date 2020-11-20 15:47:32
 */
@Slf4j
@Service
public class ShopCartItemServiceImpl implements ShopCartItemService {

    @Autowired
    private ProductService productService;
    @Autowired
    private LangManager langManager;
    @Autowired
    private SkuService skuService;
    @Autowired
    private BasketMapper basketMapper;
    @Autowired
    private ShopDetailService shopDetailService;


    @Override
    public List<ShopCartItemDto> handleShopCartItem(List<ShopCartItemDto> shopCartItems, List<DvyTypeDTO> dvyTypes, OrderType orderType) {
        // 组装购物车项数据
        this.assembleShopCartItem(shopCartItems, null);
        return shopCartItems;
    }

    @Override
    public List<ShopCartItemDto> getCheckedShopCartItems(String userId, Long addrId, List<DvyTypeDTO> dvyTypes) {
        List<ShopCartItemDto> shopCartItems = basketMapper.getUserShopCartItems(userId);
        shopCartItems.forEach(shopCartItemVO -> shopCartItemVO.setIsChecked(1));
        this.handleShopCartItem(shopCartItems, dvyTypes, OrderType.ORDINARY);
        return shopCartItems;
    }

    @Override
    public void assembleShopCartItem(List<ShopCartItemDto> shopCartItems, ShopCartItemDto mainShopCartItem) {
        List<Long> skuIds = new ArrayList<>(shopCartItems.size());
        for (ShopCartItemDto shopCartItem : shopCartItems) {
            skuIds.add(shopCartItem.getSkuId());
        }
        // 立即提交的套餐没有prodId，所以用sku来获取prodId
        Map<Long, Sku> skuMap = this.mapSkuByCache(skuIds);
        List<Long> prodIds = skuMap.values().stream().map(Sku::getProdId).toList();
        Map<Long, Product> spuMap = this.mapSpuByCache(prodIds);
        Map<Long, ShopDetail> shopMap = new HashMap<>(Constant.INITIAL_CAPACITY);
        Iterator<ShopCartItemDto> iterator = shopCartItems.iterator();
        while (iterator.hasNext()) {
            ShopCartItemDto shopCartItem = iterator.next();
            Sku sku = skuMap.get(shopCartItem.getSkuId());
            Product prod = spuMap.get(sku.getProdId());

            // 从购物车提交的订单，需要去除失效商品
            if (Objects.nonNull(shopCartItem.getBasketId())) {
                boolean invalidProduct = Objects.isNull(prod) || Objects.isNull(sku) ||
                        !Objects.equals(sku.getStatus(), StatusEnum.ENABLE.value()) ||
                        Objects.equals(sku.getIsDelete(), StatusEnum.ENABLE.value()) ||
                        !Objects.equals(prod.getStatus(), StatusEnum.ENABLE.value()) ||
                        // 购物车的预售商品，且不参与活动时，不能从购物车提交订单
                        (Objects.equals(prod.getPreSellStatus(), StatusEnum.ENABLE.value()) && Objects.isNull(shopCartItem.getComboId()) && shopCartItem.getComboId() == 0);
                if (invalidProduct) {
                    iterator.remove();
                    continue;
                }
            }

            // 填充购物车项基本数据
            handShopCartItemBaseInfo(shopCartItem, prod, sku,mainShopCartItem);

            // 店铺信息
            if (0 == prod.getShopId()) {
                shopCartItem.setShopId(Constant.PLATFORM_SHOP_ID);
                shopCartItem.setShopName(Constant.PLATFORM_SHOP_NAME);
            } else {
                ShopDetail shopDetail = shopMap.get(prod.getShopId());
                if (Objects.isNull(shopDetail)) {
                    shopDetail = shopDetailService.getShopDetailByShopId(prod.getShopId());
                    shopMap.put(prod.getShopId(), shopDetail);
                }

                shopCartItem.setShopId(shopDetail.getShopId());
                shopCartItem.setShopName(shopDetail.getShopName());
            }
        }

        // 订单项金额从小到大排序
        shopCartItems.sort(Comparator.comparingDouble(ShopCartItemDto::getActualTotal));
    }

    private static void handShopCartItemBaseInfo(ShopCartItemDto shopCartItem, Product prod, Sku sku, ShopCartItemDto mainShopCartItem) {
        if (Objects.isNull(shopCartItem.getBasketId())) {
            // 立即购买的订单
            shopCartItem.setBasketId(-1L);
        }
        // prod
        shopCartItem.setProdId(prod.getProdId());
        shopCartItem.setProdType(prod.getProdType());
        shopCartItem.setCategoryId(prod.getCategoryId());
        shopCartItem.setProdCount(shopCartItem.getProdCount());
        shopCartItem.setMold(Objects.isNull(prod.getMold()) ? 0 : prod.getMold());
        shopCartItem.setProdName(prod.getProdName());
        shopCartItem.setCategoryId(prod.getCategoryId());
        shopCartItem.setPreSellTime(Objects.equals(prod.getPreSellStatus(), 1) ? prod.getPreSellTime() : null);
        shopCartItem.setPreSellStatus(prod.getPreSellStatus());
        shopCartItem.setDeliveryMode(prod.getDeliveryMode());
        shopCartItem.setDeliveryTemplateId(prod.getDeliveryTemplateId());
        shopCartItem.setDeliveryAmount(prod.getDeliveryAmount());
        Product.DeliveryModeVO deliveryModeVO = Json.parseObject(prod.getDeliveryMode(), Product.DeliveryModeVO.class);
        shopCartItem.setDeliveryModeVO(deliveryModeVO);
        // sku
        shopCartItem.setSkuId(sku.getSkuId());
        shopCartItem.setSkuName(sku.getSkuName());
        shopCartItem.setProperties(sku.getProperties());
        shopCartItem.setBasketDate(new Date());
        shopCartItem.setDistributionCardNo(shopCartItem.getDistributionCardNo());
        if (Objects.isNull(shopCartItem.getDiscountId())) {
            shopCartItem.setDiscountId(0L);
        }
        shopCartItem.setVolume(sku.getVolume());
        shopCartItem.setWeight(sku.getWeight());

        if (Objects.equals(prod.getProdType(), ProdType.PROD_TYPE_SCORE.value())) {
            shopCartItem.setScorePrice(sku.getSkuScore() * shopCartItem.getProdCount());
        }
        shopCartItem.setPic(StrUtil.isBlank(sku.getPic()) ? prod.getPic() : sku.getPic());

        // 处理订单项的活动价格
        if (Objects.isNull(shopCartItem.getPrice()) || shopCartItem.getPrice() < 0) {
            // 普通订单sku价格
            shopCartItem.setPrice(sku.getPrice());
            // 初始化商品实际金额
            shopCartItem.setShareReduce(0.0);
            shopCartItem.setProductTotalAmount(Arith.mul(sku.getPrice(), shopCartItem.getProdCount()));
            shopCartItem.setActualTotal(shopCartItem.getProductTotalAmount());
        } else {
            // 如果有活动价并且活动价大于0则使用活动价格
            // 商品实际金额(优惠金额),在外面就放入shopCartItem中了
            shopCartItem.setActualTotal(Arith.mul(shopCartItem.getProdCount(), shopCartItem.getPrice()));
            // 如果活动价大于商品价格，则设置下原价
            if (shopCartItem.getPrice() > sku.getPrice()) {
                shopCartItem.setProductTotalAmount(Arith.mul(shopCartItem.getPrice(), shopCartItem.getProdCount()));
            } else {
                shopCartItem.setProductTotalAmount(Arith.mul(sku.getPrice(), shopCartItem.getProdCount()));
            }
            double shareReduce = Math.max(Arith.sub(shopCartItem.getProductTotalAmount(), shopCartItem.getActualTotal()), 0.0);
            shopCartItem.setShareReduce(shareReduce);
        }
    }

    private Map<Long, Product> mapSpuByCache(List<Long> spuIds) {
        if (CollUtil.isEmpty(spuIds)) {
            return Collections.emptyMap();
        }
        Map<Long, Product> spuMap = new HashMap<>(Constant.INITIAL_CAPACITY);
        if (spuIds.size() == 1) {
            Product product = productService.getProductByProdId(spuIds.get(0));
            langManager.handleProdLang(product);
            spuMap.put(spuIds.get(0),product);
        }else {
            for (Long spuId : spuIds) {
                Product product = productService.getProductByProdId(spuId);
                langManager.handleProdLang(product);
                if (Objects.isNull(product)) {
                    // 商品不存在或者已删除
                    throw new YamiShopBindException("yami.shop.cart.item.exception.prodDelete");
                }
                spuMap.put(spuId, product);
            }
        }
        return spuMap;
    }

    private Map<Long, Sku> mapSkuByCache(List<Long> skuIds) {
        if (CollUtil.isEmpty(skuIds)) {
            return Collections.emptyMap();
        }
        Map<Long, Sku> skuMap = new HashMap<>(Constant.INITIAL_CAPACITY);
        if (skuIds.size() == 1) {
            Sku sku = skuService.getSkuBySkuId(skuIds.get(0));
            skuMap.put(skuIds.get(0), sku);
            langManager.handleSkuAndLang(sku);
            return skuMap;
        }
        //TODO
//        Map<Long, Sku> skuMap = RedisUtil.mapByIds(CacheNames.SKU_KEY, skuIds , Sku.class);
//        skuIds.removeAll(skuMap.keySet());
//        if (CollUtil.isEmpty(skuIds)) {
//            return skuMap;
//        }
        for (Long skuId : skuIds) {
            Sku sku = skuService.getSkuBySkuId(skuId);
            skuMap.put(skuId, sku);
            langManager.handleSkuAndLang(sku);
        }
        return skuMap;
    }
}
