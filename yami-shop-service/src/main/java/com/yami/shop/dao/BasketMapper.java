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
import com.yami.shop.bean.app.dto.ShopCartItemDto;
import com.yami.shop.bean.app.param.CheckShopCartItemParam;
import com.yami.shop.bean.app.param.ShopCartParam;
import com.yami.shop.bean.model.Basket;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yami
 */
public interface BasketMapper extends BaseMapper<Basket> {

    /**
     * 根据用户ID获取购物车项内容列表
     *
     * @param userId 用户ID
     * @return 购物车信息项列表
     */
    List<ShopCartItemDto> getShopCartItems(@Param("userId") String userId);

    /**
     * 获取用户购物车项
     * @return
     */
    List<ShopCartItemDto> getUserShopCartItems(@Param("userId") String userId);

    /**
     * 根据用户ID与购物车ID删除购物车信息
     *
     * @param userId    用户ID
     * @param basketIds 购物车ID
     */
    void deleteShopCartItemsByBasketIds(@Param("userId") String userId, @Param("basketIds") List<Long> basketIds);

    /**
     * 根据用户ID删除该用户下的购物车信息
     *
     * @param userId 用户ID
     */
    void deleteAllShopCartItems(@Param("userId") String userId);

    /**
     * 根据用户ID获取购物车中已下架的商品列表
     *
     * @param userId 用户ID
     * @return 商品已下架的购物车项列表
     */
    List<ShopCartItemDto> getShopCartExpiryItems(@Param("userId") String userId,
                                                 @Param("lang") Integer lang);

    /**
     * 根据用户ID删除该用户购物车中已下架的商品列表
     *
     * @param userId 用户ID
     */
    void cleanExpiryProdList(@Param("userId") String userId);

    /**
     * 根据用户ID批量更新用户的购物车信息
     *
     * @param userId         用户ID
     * @param shopCartParams 购物车信息列表
     */
    void updateDiscountItemId(@Param("userId") String userId, @Param("shopCartParams") List<ShopCartParam> shopCartParams);

    /**
     * 根据商品ID批量获取购物车中有该商品的用户ID列表
     *
     * @param prodId 商品ID
     * @return 购物车中有该商品的用户ID列表
     */
    List<String> listUserIdByProdId(@Param("prodId") Long prodId);

    /**
     * 根据商品id获取购物车中拥有某件商品的用户
     *
     * @param pIds
     * @return
     */
    List<String> listUserIdByProdIds(@Param("prodIds") List<Long> pIds);

    /**
     * 获取加购了某个套餐的用户id
     *
     * @param comboId
     * @param skuIds
     * @return
     */
    List<String> removeByComboIdAndSkuIds(@Param("comboId") Long comboId, @Param("skuIds") List<Long> skuIds);

    /**
     * 获取购物车商品数量
     *
     * @param userId
     * @return
     */
    Integer getShopCartProdNum(@Param("userId") String userId);

    /**
     * 勾选购车车状态
     * @param userId 用户id
     * @param checkShopCartItems 参数
     */
    void checkShopCartItems(@Param("userId") String userId, @Param("checkShopCartItems") List<CheckShopCartItemParam> checkShopCartItems);
}
