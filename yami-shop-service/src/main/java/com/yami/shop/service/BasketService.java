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

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.app.dto.ShopCartItemDto;
import com.yami.shop.bean.app.param.ChangeShopCartParam;
import com.yami.shop.bean.app.param.CheckShopCartItemParam;
import com.yami.shop.bean.app.param.ShopCartParam;
import com.yami.shop.bean.model.Basket;

import java.util.List;

/**
 * @author lgh on 2018/10/18.
 */
public interface BasketService extends IService<Basket> {

    /**
     * 根据用户ID与购物车ID删除购物车信息
     * @param userId 用户ID
     * @param basketIds 购物车id列表删除参数
     */
    void deleteShopCartItemsByBasketIds(String userId, List<Long> basketIds);

    /**
     * 添加购物车信息
     * @param param 购物车信息参数
     * @param userId 用户ID
     */
    void addShopCartItem(ChangeShopCartParam param, String userId);

    /**
     * 更新购物车信息
     * @param basket 购物车信息参数
     */
    void updateShopCartItem(Basket basket);

    /**
     * 批量更新购物车信息
     * @param userId 用户id
     * @param basketList 购物车信息列表
     */
    void updateShopCartItemBatch(String userId, List<Basket> basketList);

    /**
     * 根据用户ID删除该用户下的所有购物车信息
     * @param userId 用户ID
     */
    void deleteAllShopCartItems(String userId);

    /**
     * 根据用户ID获取购物车中已下架的商品列表
     * @param userId 用户ID
     * @return 商品已下架的购物车项列表
     */
    List<ShopCartItemDto> getShopCartExpiryItems(String userId);

    /**
     * 根据用户ID删除该用户购物车中已下架的商品列表
     * @param userId 用户ID
     */
    void cleanExpiryProdList(String userId);

    /**
     * 根据用户ID批量更新用户的购物车信息
     * @param userId 用户ID
     * @param shopCartParams 购物车信息列表
     */
    void updateBasketByShopCartParam(String userId, List<ShopCartParam> shopCartParams);

    /**
     * 获取购物车中拥有某件商品的用户，用于清除该用户购物车的缓存
     * @param prodId 商品id
     * @return 用户id
     */
    List<String> listUserIdByProdId(Long prodId);

    /**
     * 根据商品id获取购物车中拥有某件商品的用户，用于清除该用户购物车的缓存
     * @param pIds 商品id列表
     * @return 用户id列表
     */
    List<String> listUserIdByProdIds(List<Long> pIds);

    /**
     * 根据用户id列表清除缓存
     * @param userIds 用户id列表
     */
    void removeCacheByUserIds(List<String> userIds);

    /**
     * 删除购物车中的套餐信息
     * @param comboId 套餐id
     * @param skuIds 规格id列表
     */
    void removeByComboIdAndSkuIds(Long comboId, List<Long> skuIds);

    /**
     * 获取购物车商品数量
     * @param userId 用户id
     * @return 购物车商品数量
     */
    Integer getShopCartProdNum(String userId);

    /**
     * 勾选购物车状态
     * @param userId 用户id
     * @param params 参数
     */
    void checkShopCartItems(String userId, List<CheckShopCartItemParam> params);
}
