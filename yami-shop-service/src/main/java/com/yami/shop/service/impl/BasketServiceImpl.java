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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.app.dto.ShopCartItemDto;
import com.yami.shop.bean.app.param.ChangeShopCartParam;
import com.yami.shop.bean.app.param.CheckShopCartItemParam;
import com.yami.shop.bean.app.param.ShopCartParam;
import com.yami.shop.bean.model.Basket;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.constants.CacheNames;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.util.RedisUtil;
import com.yami.shop.dao.BasketMapper;
import com.yami.shop.service.BasketService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lgh on 2018/10/18.
 */
@Service
@AllArgsConstructor
public class BasketServiceImpl extends ServiceImpl<BasketMapper, Basket> implements BasketService {

    private final BasketMapper basketMapper;

    @Override
    @CacheEvict(cacheNames = CacheNames.SHOP_CART_ITEM_COUNT, key = "#userId")
    public void deleteShopCartItemsByBasketIds(String userId, List<Long> basketIds) {
        if (CollUtil.isEmpty(basketIds)) {
            return;
        }
        basketMapper.deleteShopCartItemsByBasketIds(userId, basketIds);
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.SHOP_CART_ITEM_COUNT, key = "#userId")
    public void addShopCartItem(ChangeShopCartParam param, String userId) {
        Basket basket = new Basket();
        basket.setBasketCount(param.getCount());
        basket.setBasketDate(new Date());
        basket.setProdId(param.getProdId());
        basket.setShopId(param.getShopId());
        basket.setUserId(userId);
        basket.setSkuId(param.getSkuId());
        basket.setDiscountId(0L);
        basket.setIsChecked(1);
        basket.setDistributionCardNo(param.getDistributionCardNo());
        basketMapper.insert(basket);
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.SHOP_CART_ITEM_COUNT, key = "#basket.userId")
    public void updateShopCartItem(Basket basket) {
        basketMapper.updateById(basket);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = CacheNames.SHOP_CART_ITEM_COUNT, key = "#userId")
    public void updateShopCartItemBatch(String userId, List<Basket> basketList) {
        updateBatchById(basketList);
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.SHOP_CART_ITEM_COUNT, key = "#userId")
    public void deleteAllShopCartItems(String userId) {
        basketMapper.deleteAllShopCartItems(userId);
    }

    @Override
    public List<ShopCartItemDto> getShopCartExpiryItems(String userId) {
        Integer lang = I18nMessage.getLang();
        return basketMapper.getShopCartExpiryItems(userId, lang);
    }

    @Override
    public void cleanExpiryProdList(String userId) {
        basketMapper.cleanExpiryProdList(userId);
        removeCacheByUserIds(Collections.singletonList(userId));
    }

    @Override
    public void updateBasketByShopCartParam(String userId, List<ShopCartParam> shopCartParams) {
        shopCartParams = shopCartParams.stream().filter(shopCart -> Objects.nonNull(shopCart.getDiscountId())).collect(Collectors.toList());
        basketMapper.updateDiscountItemId(userId, shopCartParams);
    }

    @Override
    public List<String> listUserIdByProdId(Long prodId) {
        return basketMapper.listUserIdByProdId(prodId);
    }

    @Override
    public List<String> listUserIdByProdIds(List<Long> pIds) {
        if (CollUtil.isEmpty(pIds)) {
            return new ArrayList<>();
        }
        return basketMapper.listUserIdByProdIds(pIds);
    }

    @Override
    public void removeCacheByUserIds(List<String> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        List<String> keys = new ArrayList<>();
        for (String userId : userIds) {
            keys.add(CacheNames.SHOP_CART_ITEM_COUNT + Constant.UNION + userId);
        }
        RedisUtil.del(keys);
    }

    @Override
    public void removeByComboIdAndSkuIds(Long comboId, List<Long> skuIds) {
        List<String> userIds = basketMapper.removeByComboIdAndSkuIds(comboId, skuIds);
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        basketMapper.delete(new LambdaQueryWrapper<Basket>()
                .eq(Basket::getComboId, comboId)
                .in(CollUtil.isNotEmpty(skuIds), Basket::getSkuId, skuIds)
        );
        removeCacheByUserIds(userIds);
    }

    @Override
    @Cacheable(cacheNames = CacheNames.SHOP_CART_ITEM_COUNT, key = "#userId")
    public Integer getShopCartProdNum(String userId) {
        return basketMapper.getShopCartProdNum(userId);
    }

    @Override
    public void checkShopCartItems(String userId, List<CheckShopCartItemParam> params) {
        basketMapper.checkShopCartItems(userId, params);
    }

}
