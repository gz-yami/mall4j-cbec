/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.api.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Snowflake;
import com.yami.shop.bean.app.dto.*;
import com.yami.shop.bean.app.param.OrderShopParam;
import com.yami.shop.bean.app.param.SubmitOrderParam;
import com.yami.shop.bean.enums.*;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.constants.OrderCacheNames;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ResponseEnum;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.CacheManagerUtil;
import com.yami.shop.common.util.RedisUtil;
import com.yami.shop.manager.SubmitOrderManager;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * 提交订单适配
 * @author FrozenWatermelon
 * @date 2020/12/07
 */
@Component
@AllArgsConstructor
public class SubmitOrderImplManager implements SubmitOrderManager {

    private final CacheManagerUtil cacheManagerUtil;

    private final ApplicationContext applicationContext;

    private final Snowflake snowflake;


    @Override
    public ServerResponseEntity<ShopCartOrderMergerDto> checkSubmitInfo(SubmitOrderParam submitOrderParam, String userId) {
        ShopCartOrderMergerDto mergerOrder = cacheManagerUtil.getCache(OrderCacheNames.ORDER_CONFIRM_KEY, String.valueOf(userId));
        // 看看订单有没有过期
        if (Objects.isNull(mergerOrder)) {
            // 订单已过期，请重新下单
            throw new YamiShopBindException("yami.order.expired");
        }
        // 防止重复、同时提交
        boolean cad = RedisUtil.cad(OrderCacheNames.ORDER_CONFIRM_UUID_KEY + OrderCacheNames.UNION + userId, userId);
        if (!cad) {
            // 订单状态已经发生改变，请重新下单
            OrderNumbersDto orderNumbersDto = new OrderNumbersDto(null);
            orderNumbersDto.setDuplicateError(1);
            return ServerResponseEntity.fail(ResponseEnum.REPEAT_ORDER);
        }
        // 看看订单的标记有没有过期
        if (cacheManagerUtil.getCache(OrderCacheNames.ORDER_CONFIRM_KEY, userId) == null) {
            // 订单已过期，请重新下单
            throw new YamiShopBindException("yami.order.expired");
        }
        mergerOrder.setUserId(userId);
        // 订单拆单
        // 需要拆分订单(订单类型为普通订单才需要拆单)
        if (needSplitShopCartOrder(mergerOrder)) {
            // 组合商品处理及虚拟商品拆分订单
            splitShopCartOrderDtoByComboAndVirtual(mergerOrder);
        }
        // 设置备注和生成订单id
        setOrderRemarkAndId(mergerOrder, submitOrderParam);
        return ServerResponseEntity.success(mergerOrder);
    }

    private void setOrderRemarkAndId(ShopCartOrderMergerDto mergerOrder, SubmitOrderParam submitOrderParam){
        List<OrderShopParam> orderParams = submitOrderParam.getOrderShopParams();
        List<ShopCartOrderDto> shopCartOrders = mergerOrder.getShopCartOrders();
        for (ShopCartOrderDto shopCartOrder : shopCartOrders) {
            if (CollectionUtil.isNotEmpty(orderParams)) {
                for (OrderShopParam orderParam : orderParams) {
                    if (!Objects.equals(shopCartOrder.getShopId(), orderParam.getShopId())) {
                        continue;
                    }
                    shopCartOrder.setRemarks(orderParam.getRemarks());
                }
            }
        }
        for (ShopCartOrderDto shopCartOrder : shopCartOrders) {
            // 使用雪花算法生成的订单号
            String orderNumber = String.valueOf(snowflake.nextId());
            shopCartOrder.setOrderNumber(orderNumber);
        }
    }

    private void splitShopCartOrderDtoByComboAndVirtual(ShopCartOrderMergerDto mergerOrder) {
        List<ShopCartOrderDto> vitrualShopCartOrders = new ArrayList<>(Constant.INITIAL_CAPACITY);
        List<ShopCartOrderDto> normalShopCartOrderList = new ArrayList<>(Constant.INITIAL_CAPACITY);
        for (ShopCartOrderDto shopCartOrder : mergerOrder.getShopCartOrders()) {
            double levelFreeTransfee = Objects.isNull(shopCartOrder.getPlatformFreeFreightAmount()) ? 0L : shopCartOrder.getPlatformFreeFreightAmount();
             Iterator<ShopCartItemDiscountDto> shopCartIterator = shopCartOrder.getShopCartItemDiscounts().iterator();
            double transFee = shopCartOrder.getTransFee();
            List<ShopCartOrderDto> virtualShopCartOrderList = new ArrayList<>();
            List<ShopCartItemDto> normalShopCartItemList = new ArrayList<>(Constant.INITIAL_CAPACITY);
            int itemCount = shopCartOrder.getShopCartItemDiscounts().get(0).getShopCartItems().size();
            // 如果当前店铺的商品数量为1,则不需要拆单直接跳过
            if(itemCount == 1 && shopCartOrder.getShopCartItemDiscounts().size() == 1){
                ShopCartItemDto shopCartItemDto = shopCartOrder.getShopCartItemDiscounts().get(0).getShopCartItems().get(0);
                if (Objects.equals(shopCartItemDto.getMold(), ProdMoldEnum.VIRTUAL.value())) {
                    ShopCartOrderDto shopCartOrderVO = getVirtualShopCartOrgetder(shopCartOrder, shopCartItemDto);
                    virtualShopCartOrderList.add(shopCartOrderVO);
                }else{
                    // 普通的商品放入同一个list
                    normalShopCartItemList.add(shopCartItemDto);
                }
            }else {
                while (shopCartIterator.hasNext()) {
                    ShopCartItemDiscountDto shopCartItemDiscountDto = shopCartIterator.next();
                    Iterator<ShopCartItemDto> iterator = shopCartItemDiscountDto.getShopCartItems().iterator();
                    while (iterator.hasNext()) {
                        ShopCartItemDto shopCartItemDto = iterator.next();
                        // 普通虚拟商品直接拆分即可
                        if (Objects.equals(shopCartItemDto.getMold(), ProdMoldEnum.VIRTUAL.value())) {
                            ShopCartOrderDto shopCartOrderVO = getVirtualShopCartOrgetder(shopCartOrder, shopCartItemDto);
                            virtualShopCartOrderList.add(shopCartOrderVO);
                        } else {
                            // 普通的商品放入同一个list
                            normalShopCartItemList.add(shopCartItemDto);
                        }
                    }
                }
            }
            // 重新算一下商品
            if (CollectionUtil.isNotEmpty(virtualShopCartOrderList)) {
                for (ShopCartOrderDto shopCartOrderVO : virtualShopCartOrderList) {
                    // 店铺订单计算
                    // 能被拆分的都是虚拟商品,运费都为0
                    shopCartOrderVO.setTransFee(0.0);
                    shopCartOrderVO.setPlatformFreeFreightAmount(0.0);
                    loadShopCartOrderDto(shopCartOrderVO);
                    vitrualShopCartOrders.add(shopCartOrderVO);
                }
            }
            // 普通商品在一个order
            if (CollectionUtil.isNotEmpty(normalShopCartItemList)) {
                ShopCartItemDiscountDto shopCartItemDiscount = new ShopCartItemDiscountDto();
                shopCartItemDiscount.setShopCartItems(normalShopCartItemList);
                shopCartOrder.setShopCartItemDiscounts(Collections.singletonList(shopCartItemDiscount));
                shopCartOrder.setTransFee(transFee);
                shopCartOrder.setPlatformFreeFreightAmount(Math.min(levelFreeTransfee, shopCartOrder.getTransFee()));
                loadShopCartOrderDto(shopCartOrder);
                normalShopCartOrderList.add(shopCartOrder);
            }
        }
        mergerOrder.setShopCartOrders(new ArrayList<>(Constant.INITIAL_CAPACITY));
        if (CollectionUtil.isNotEmpty(normalShopCartOrderList)) {
            mergerOrder.setShopCartOrders(normalShopCartOrderList);
        }
        if (CollUtil.isNotEmpty(vitrualShopCartOrders)) {
            mergerOrder.getShopCartOrders().addAll(vitrualShopCartOrders);
        }
    }
    private void loadShopCartOrderDto(ShopCartOrderDto shopCartOrderVO) {
        if (Objects.isNull(shopCartOrderVO.getPlatformFreeFreightAmount())) {
            shopCartOrderVO.setPlatformFreeFreightAmount(0.0);
        }
        if (Objects.isNull(shopCartOrderVO.getTransFee())) {
            shopCartOrderVO.setTransFee(0.0);
        }
        shopCartOrderVO.setUseScore(0L);
        shopCartOrderVO.setActualTotal(shopCartOrderVO.getTransFee() - shopCartOrderVO.getPlatformFreeFreightAmount());
        shopCartOrderVO.setTotal(0.0);
        shopCartOrderVO.setTotalCount(0);
        shopCartOrderVO.setDiscountReduce(0.0);
        shopCartOrderVO.setCouponReduce(0.0);
        shopCartOrderVO.setScoreReduce(0.0);
        shopCartOrderVO.setLevelReduce(0.0);
        shopCartOrderVO.setShopComboAmount(0.0);
        shopCartOrderVO.setShopReduce(0.0);
        List<ShopCartItemDiscountDto> shopCartItemDiscounts = shopCartOrderVO.getShopCartItemDiscounts();
        for (ShopCartItemDiscountDto shopCartItemDiscount : shopCartItemDiscounts) {
            for (ShopCartItemDto cartItemDto : shopCartItemDiscount.getShopCartItems()) {
                // 订单项的金额在生成订单时已经算好了，这里只需要相加即可，不需要别的处理
                shopCartOrderVO.setActualTotal(shopCartOrderVO.getActualTotal() + cartItemDto.getActualTotal());
                shopCartOrderVO.setTotal(shopCartOrderVO.getTotal() + cartItemDto.getProductTotalAmount());
                shopCartOrderVO.setTotalCount(shopCartOrderVO.getTotalCount() + cartItemDto.getProdCount());
                shopCartOrderVO.setShopReduce(shopCartOrderVO.getShopReduce() + cartItemDto.getShareReduce());
                if (Objects.nonNull(cartItemDto.getDiscountAmount())) {
                    shopCartOrderVO.setDiscountReduce(shopCartOrderVO.getDiscountReduce() + cartItemDto.getDiscountAmount());
                }
                if (Objects.nonNull(cartItemDto.getShopCouponAmount())) {
                    shopCartOrderVO.setCouponReduce(shopCartOrderVO.getCouponReduce() + cartItemDto.getShopCouponAmount());
                }
                if (Objects.nonNull(cartItemDto.getScorePayReduce())) {
                    shopCartOrderVO.setScoreReduce(shopCartOrderVO.getScoreReduce() + cartItemDto.getScorePayReduce());
                }
                if (Objects.nonNull(cartItemDto.getLevelReduce())) {
                    shopCartOrderVO.setLevelReduce(shopCartOrderVO.getLevelReduce() + cartItemDto.getLevelReduce());
                }
                if (Objects.nonNull(cartItemDto.getScorePrice())) {
                    shopCartOrderVO.setUseScore(shopCartOrderVO.getUseScore() + cartItemDto.getScorePrice());
                }
                if (Objects.nonNull(cartItemDto.getComboAmount())) {
                    shopCartOrderVO.setShopComboAmount(shopCartOrderVO.getShopComboAmount() + cartItemDto.getComboAmount());
                }
            }
        }
    }

    private ShopCartOrderDto getVirtualShopCartOrgetder(ShopCartOrderDto shopCartOrder, ShopCartItemDto shopCartItemDto) {
        ShopCartOrderDto shopCartOrderVO = new ShopCartOrderDto();
        // 拆分虚拟商品
        shopCartOrderVO.setShopId(shopCartOrder.getShopId());
        shopCartOrderVO.setShopName(shopCartOrder.getShopName());
        shopCartOrderVO.setRemarks(shopCartOrder.getRemarks());
        shopCartOrderVO.setDvyType(DeliveryType.NO_EXPRESS.getValue());
        shopCartOrderVO.setShopCartItemDiscounts(new ArrayList<>(Constant.INITIAL_CAPACITY));
        ShopCartItemDiscountDto shopCartItemDiscount = new ShopCartItemDiscountDto();
        shopCartItemDiscount.setShopCartItems(Collections.singletonList(shopCartItemDto));
        shopCartOrderVO.getShopCartItemDiscounts().add(shopCartItemDiscount);
        return shopCartOrderVO;
    }

    /**
     * 是否需要拆单
     * 1.含有虚拟商品需要拆单
     *
     * @param mergerOrder
     * @return
     */
    private boolean needSplitShopCartOrder(ShopCartOrderMergerDto mergerOrder) {
        // 普通订单和团购才需要拆单
        if (!Objects.equals(mergerOrder.getOrderType().value(), OrderType.ORDINARY.value())) {
            return Boolean.FALSE;
        }
        int orderItemCount = 0;
        boolean hasVirtualProd = false;
        for (ShopCartOrderDto shopCartOrder : mergerOrder.getShopCartOrders()) {
            for (ShopCartItemDiscountDto shopCartItemDiscount : shopCartOrder.getShopCartItemDiscounts()) {
                orderItemCount += shopCartItemDiscount.getShopCartItems().size();
                // 供应商需要拆单
                for (ShopCartItemDto shopCartItem : shopCartItemDiscount.getShopCartItems()) {
                    // 判断下是否为虚拟商品,如果是虚拟商品且多于一个商品项时进行拆单
                    if (Objects.nonNull(shopCartItem.getMold()) && Objects.equals(shopCartItem.getMold(), ProdMoldEnum.VIRTUAL.value())) {
                        hasVirtualProd = Boolean.TRUE;
                        break;
                    }
                }
            }
        }
        return orderItemCount > 1 && hasVirtualProd;
    }

}
