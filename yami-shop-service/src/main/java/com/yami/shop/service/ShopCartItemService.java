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


import com.yami.shop.bean.app.dto.DvyTypeDTO;
import com.yami.shop.bean.app.dto.ShopCartItemDto;
import com.yami.shop.bean.enums.OrderType;

import java.util.List;

/**
 * 购物车
 *
 * @author YXF
 * @date 2023-11-24 15:47:32
 */
public interface ShopCartItemService {

    /**
     * 获取购物项
     * @param shopCartItems 订单项\购物车项数据
     * @param dvyTypes 店铺物流类型
     * @param orderType 订单类型 普通订单可以参与赠品活动
     * @return 购物项
     */
    List<ShopCartItemDto> handleShopCartItem(List<ShopCartItemDto> shopCartItems, List<DvyTypeDTO> dvyTypes, OrderType orderType);

    /**
     * 获取购物项
     * @param userId
     * @param addrId
     * @param dvyTypes
     * @return 购物项
     */
    List<ShopCartItemDto> getCheckedShopCartItems(String userId,Long addrId, List<DvyTypeDTO> dvyTypes);


    /**
     * 组装商品购物项的数据
     *
     * @param shopCartItems 订单项\购物车项数据
     * @param mainShopCartItem
     */
    void assembleShopCartItem(List<ShopCartItemDto> shopCartItems, ShopCartItemDto mainShopCartItem);
}
