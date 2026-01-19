package com.yami.shop.manager.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.yami.shop.bean.app.dto.ProductItemDto;
import com.yami.shop.bean.app.dto.ShopCartDto;
import com.yami.shop.bean.app.dto.ShopCartItemDiscountDto;
import com.yami.shop.bean.app.dto.ShopCartItemDto;
import com.yami.shop.common.util.Arith;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 购物车适配器
 * @author FrozenWatermelon
 */
@Component
@AllArgsConstructor
public class ShopCartAdapter {

    /**
     * 将参数转换成组装好的购物项
     * @param shopCartItems 订单参数
     * @return 组装好的购物项
     */
    public List<ShopCartDto> getShopCarts(List<ShopCartItemDto> shopCartItems) {
        // 根据店铺ID划分item
        Map<Long, List<ShopCartItemDto>> shopCartMap = shopCartItems.stream().collect(Collectors.groupingBy(ShopCartItemDto::getShopId));
        // 返回一个店铺的所有信息
        List<ShopCartDto> shopCartDtoList = Lists.newArrayList();
        List<Long> shopIds=Lists.newArrayList();
        for (ShopCartItemDto shopCartItem : shopCartItems) {
            if(!shopIds.contains(shopCartItem.getShopId())){
                shopIds.add(shopCartItem.getShopId());
            }
        }
        for (Long shopId : shopIds) {
            // 获取店铺的所有商品项
            List<ShopCartItemDto> shopCartItemDtoList = shopCartMap.get(shopId);
            // 获取店铺所有投放状态的优惠券

            // 构建每个店铺的购物车信息
            ShopCartDto shopCart = buildShopCart(shopId, shopCartItemDtoList);

            // 自营店的购物车位置是放在第一的
            if (Objects.equals(1L, shopId)) {
                shopCartDtoList.add(0, shopCart);
            } else {
                shopCartDtoList.add(shopCart);
            }
        }

        return shopCartDtoList;
    }

    private ShopCartDto buildShopCart(Long shopId, List<ShopCartItemDto> shopCartItems) {
        // 构建每个店铺的购物车信息
        ShopCartDto shopCart = new ShopCartDto();
        // 店铺信息
        shopCart.setShopId(shopId);
        ShopCartItemDto shopCartItemDto = shopCartItems.get(0);
        shopCart.setShopName(shopCartItemDto.getShopName());
        shopCart.setShopCityStatus(shopCartItemDto.getShopCityStatus());
        shopCart.setCategoryId(shopCartItemDto.getCategoryId());
        shopCart.setShopReduce(0.0);
        shopCart.setComboReduce(0.0);
        shopCart.setCouponReduce(0.0);
        shopCart.setDiscountReduce(0.0);
        double total = 0.0;
        int totalCount = 0;
        double reduce = 0.0;
        long scoreTotal = 0;
        double actualTotal = 0.0;

        for (ShopCartItemDto shopCartItem : shopCartItems) {
            if (Objects.nonNull(shopCartItem.getComboId()) && shopCartItem.getComboId() != 0) {
                continue;
            }
            total = Arith.add(total, shopCartItem.getProductTotalAmount());
            actualTotal = Arith.add(actualTotal, shopCartItem.getActualTotal());
            reduce = shopCartItem.getShareReduce() == null ? 0.0 : shopCartItem.getShareReduce();
            scoreTotal += shopCartItem.getScorePrice() == null ? 0 : shopCartItem.getScorePrice();;
            totalCount += shopCartItem.getProdCount() == null ? 0 : shopCartItem.getProdCount();
        }

        ShopCartItemDiscountDto shopCartItemDiscountVO = new ShopCartItemDiscountDto();
        shopCartItemDiscountVO.setShopCartItems(shopCartItems);
        shopCart.setShopCartItemDiscounts(Collections.singletonList(shopCartItemDiscountVO));
        shopCart.setTotal(total);
        shopCart.setShopReduce(reduce);
        shopCart.setTotalCount(totalCount);
        shopCart.setActualTotal(actualTotal);
        shopCart.setScoreTotal(scoreTotal);
        return shopCart;
    }
}
