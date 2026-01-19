/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.api.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.yami.shop.bean.app.dto.*;
import com.yami.shop.bean.app.param.OrderParam;
import com.yami.shop.bean.app.param.OrderPayInfoParam;
import com.yami.shop.bean.app.param.SubmitOrderParam;
import com.yami.shop.bean.enums.*;
import com.yami.shop.bean.event.EsProductUpdateEvent;
import com.yami.shop.bean.event.SubmitSeckillOrderEvent;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.param.ChooseCouponParam;
import com.yami.shop.bean.vo.UserDeliveryInfoVO;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.response.ResponseEnum;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.Arith;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.delivery.api.manager.DeliveryOrderManager;
import com.yami.shop.manager.*;
import com.yami.shop.manager.impl.ConfirmOrderManager;
import com.yami.shop.manager.impl.ShopCartAdapter;
import com.yami.shop.manager.impl.ShopCartItemAdapter;
import com.yami.shop.security.api.util.SecurityUtils;
import com.yami.shop.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author LGH
 */
@Slf4j
@RestController
@RequestMapping("/p/order")
@Tag(name = "订单接口")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final SubmitOrderManager submitOrderManager;
    private final SkuService skuService;
    private final ProductService productService;
    private final ApplicationContext applicationContext;
    private final UserAddrOrderService userAddrOrderService;
    private final ApplicationEventPublisher eventPublisher;
    private final ShopCartAdapter shopCartAdapter;
    private final ShopCartItemAdapter shopCartItemAdapter;
    private final ConfirmOrderManager confirmOrderManager;
    private final DeliveryOrderManager deliveryOrderManager;;
    private final UserAddrService userAddrService;

    @PostMapping("/confirm")
    @Operation(summary = "结算，生成普通订单信息" , description = "传入下单所需要的参数进行下单")
    public ServerResponseEntity<ShopCartOrderMergerDto> confirm(@Valid @RequestBody OrderParam orderParam) throws ExecutionException, InterruptedException {
        //pc端 立即购买设置配送方式
        setParmamDvytype(orderParam);
        String userId = SecurityUtils.getUser().getUserId();
        // 将要返回给前端的完整的订单信息，初始化
        ShopCartOrderMergerDto shopCartOrderMerger = setMergerDto(orderParam, userId);
        // 组装获取用户提交的购物车商品项
        List<ShopCartItemDto> shopCartItems = shopCartItemAdapter.getShopCartItemsByOrderItems(orderParam, userId, orderParam.getAddrId());
        //pc端 购物车下单设置配送方式
        this.setCartDvytype(orderParam, shopCartItems, shopCartOrderMerger);
        // 获取用户地址信息
        UserDeliveryInfoVO userDeliveryInfoVO = confirmOrderManager.getUserDeliveryInfoVO(shopCartItems, userId, orderParam.getDvyTypes(), orderParam.getAddrId());
        // 筛选过滤掉不同配送的商品
        // 该商品不满足任何的配送方式
        if (CollectionUtil.isEmpty(shopCartItems)) {
            log.info("该商品不满足任何的配送方式，组装用户地址");
            seMergerUserAddr(orderParam, userId, shopCartOrderMerger);
            return ServerResponseEntity.success(shopCartOrderMerger);
        }

        // 商品类别 0.实物商品 1. 虚拟商品 2.组合商品
        int mold = 0;
        // 判断订单类型
        checkOrderType(shopCartOrderMerger, shopCartItems, orderParam, mold);
        // 购物车
        List<ShopCartDto> shopCarts = shopCartAdapter.getShopCarts(shopCartItems);


        // 计算运费
        UserDeliveryInfoVO userDeliveryInfo = deliveryOrderManager.calculateAndGetDeliverInfo(shopCartItems, userDeliveryInfoVO);

        // 当算完一遍店铺的各种满减活动时，重算一遍订单金额
        confirmOrderManager.recalculateAmountWhenFinishingCalculateShop(shopCartOrderMerger, shopCarts, userDeliveryInfo);

        double orderShopReduce = shopCartOrderMerger.getOrderReduce();
        confirmOrderManager.reCalAmountWhenFinishCalShop(shopCartOrderMerger);

        // 结束平台优惠的计算之后，还要重算一遍金额
        confirmOrderManager.recalculateAmountWhenFinishingCalculatePlatform(shopCartOrderMerger);
        shopCartOrderMerger.setOrderShopReduce(orderShopReduce);
        // 缓存计算
        confirmOrderManager.cacheCalculatedInfo(userId, shopCartOrderMerger);
        return ServerResponseEntity.success(shopCartOrderMerger);
    }

    @PostMapping("/submit")
    @Operation(summary = "提交订单，返回支付流水号" , description = "根据传入的参数判断是否为购物车提交订单，同时对购物车进行删除，用户开始进行支付，根据店铺进行拆单")
    public ServerResponseEntity<OrderNumbersDto> submitOrders(@Valid @RequestBody SubmitOrderParam submitOrderParam) {
        String userId = SecurityUtils.getUser().getUserId();
        ServerResponseEntity<ShopCartOrderMergerDto> orderCheckResult = submitOrderManager.checkSubmitInfo(submitOrderParam, userId);
        if (!orderCheckResult.isSuccess()) {
            if(StrUtil.equals(ResponseEnum.REPEAT_ORDER.value(),orderCheckResult.getCode())){
                OrderNumbersDto orderNumbersDto = new OrderNumbersDto(null);
                orderNumbersDto.setDuplicateError(1);
                return ServerResponseEntity.success(orderNumbersDto);
            }
        }
        ShopCartOrderMergerDto mergerOrder = orderCheckResult.getData();

        List<ShopCartOrderDto> shopCartOrders = mergerOrder.getShopCartOrders();
        for (ShopCartOrderDto shopCartOrder : shopCartOrders) {
            List<ShopCartItemDiscountDto> shopCartItemDiscounts = shopCartOrder.getShopCartItemDiscounts();
            for (ShopCartItemDiscountDto shopCartItemDiscount : shopCartItemDiscounts) {
                List<ShopCartItemDto> shopCartItems = shopCartItemDiscount.getShopCartItems();
                for (ShopCartItemDto shopCartItem : shopCartItems) {
                    Long prodId = shopCartItem.getProdId();
                    Product productById = productService.getProductById(prodId);
                    if(Objects.isNull(productById) || !productById.getStatus().equals(ProdStatusEnums.NORMAL.getValue())){
                        throw new YamiShopBindException(String.format(I18nMessage.getMessage("yami.order.exception.prodOffline"), productById.getProdName()));
                    }
                }
            }
        }
        List<Order> orders = orderService.submit(mergerOrder);
        StringBuilder orderNumbers = new StringBuilder();
        for (Order order : orders) {
            orderNumbers.append(order.getOrderNumber()).append(StrUtil.COMMA);
        }
        orderNumbers.deleteCharAt(orderNumbers.length() - 1);

        // 移除缓存
        for (ShopCartOrderDto shopCartOrder : shopCartOrders) {
            for (ShopCartItemDiscountDto shopCartItemDiscount : shopCartOrder.getShopCartItemDiscounts()) {
                for (ShopCartItemDto shopCartItem : shopCartItemDiscount.getShopCartItems()) {
                    skuService.removeSkuCacheBySkuId(shopCartItem.getSkuId(), shopCartItem.getProdId());
                    productService.removeProdCacheByProdId(shopCartItem.getProdId());
                }
            }
        }
        orderService.removeConfirmOrderCache(userId + submitOrderParam.getUuid());

        // 直接支付成功
//        List<String> numbers = orders.stream().map(Order::getOrderNumber).collect(Collectors.toList());
//        orderService.updateByToPaySuccess(numbers);

        return ServerResponseEntity.success(new OrderNumbersDto(orderNumbers.toString()));
    }

    @GetMapping("/getOrderPayInfoByOrderNumber")
    @Operation(summary = "获取订单支付信息" , description = "获取订单支付的商品/地址信息")
    @Parameter(name = "orderNumbers", description = "订单流水号" , required = true)
    public ServerResponseEntity<OrderPayInfoParam> getOrderPayInfoByOrderNumber(@RequestParam("orderNumbers") String orderNumbers) {
        String userId = SecurityUtils.getUser().getUserId();
        List<String> orderNumberList = Arrays.asList(orderNumbers.split(StrUtil.COMMA));
        //获取订单信息
        List<Order> orderList = orderService.getOrderPayInfoByOrderNumber(orderNumberList);
        List<String> prodNameList = Lists.newArrayList();
        Long addrOrderId = null;
        Date endTime = null;
        int totalScore = 0;
        double totalFee = 0.0;
        boolean isStation = false;
        boolean hasAddr = false;
        Integer status = 1;
        Order orderDb = new Order();
        //获取商品名集合
        for (Order order : orderList) {
            if (!Objects.equals(userId, order.getUserId())) {
                throw new YamiShopBindException("yami.order.no.exist");
            }
            for (OrderItem orderItem : order.getOrderItems()) {
                prodNameList.add(orderItem.getProdName());
                totalScore += orderItem.getUseScore() != null ? orderItem.getUseScore() : 0;
            }
            //第一次循环，获取订单地址id，订单过期时间
            if (Objects.isNull(addrOrderId)) {
                addrOrderId = order.getAddrOrderId();
                if (Objects.equals(2, order.getOrderType())) {
                    // 获取秒杀订单的取消订单时间
                    Integer maxCancelTime = 0;
                    if (maxCancelTime <= 0) {
                        maxCancelTime = 30;
                    }
                    endTime = DateUtil.offsetMinute(order.getCreateTime(), maxCancelTime);
                } else {
                    endTime = DateUtil.offsetMinute(order.getCreateTime(), 30);
                }
            }
            totalFee = Arith.add(totalFee, order.getActualTotal());
            if (Objects.equals(order.getDvyType(), DvyType.STATION.value())) {
                isStation = true;
                orderDb = order;
            }
            if (!Objects.equals(order.getStatus(), OrderStatus.UNPAY.value())) {
                status = order.getStatus();
            }
            if (!hasAddr && !Objects.equals(order.getOrderMold(), 1)) {
                addrOrderId = order.getAddrOrderId();
                hasAddr = true;
            }
        }
        OrderPayInfoParam orderPayInfoParam = new OrderPayInfoParam();
        orderPayInfoParam.setStatus(status);
        orderPayInfoParam.setProdNameList(prodNameList);
        orderPayInfoParam.setEndTime(endTime);
        orderPayInfoParam.setTotalFee(totalFee);
        orderPayInfoParam.setTotalScore(totalScore);
        if (isStation) {
            orderPayInfoParam.setUserAddr("");
            orderPayInfoParam.setReceiver(orderDb.getReceiverName());
            orderPayInfoParam.setMobile(orderDb.getReceiverMobile());
        } else if (hasAddr) {
            //写入商品名、收货地址/电话
            UserAddrOrder userAddrOrder = userAddrOrderService.getById(addrOrderId);
            String addr = userAddrOrder.getProvince() +
                    (Objects.nonNull(userAddrOrder.getCity()) && !Objects.equals(userAddrOrder.getCity(), "") ? "/" + userAddrOrder.getCity() : "") +
                    (Objects.nonNull(userAddrOrder.getArea()) && !Objects.equals(userAddrOrder.getArea(), "") ? "/" + userAddrOrder.getArea() : "") +
                    "/" + userAddrOrder.getAddr();
            orderPayInfoParam.setUserAddr(addr);
            orderPayInfoParam.setReceiver(userAddrOrder.getReceiver());
            orderPayInfoParam.setMobile(userAddrOrder.getMobile());
        }
        return ServerResponseEntity.success(orderPayInfoParam);
    }

    private ShopCartOrderMergerDto setMergerDto(OrderParam orderParam, String userId) {
        ShopCartOrderMergerDto shopCartOrderMerger = new ShopCartOrderMergerDto();
        shopCartOrderMerger.setUserId(userId);
        shopCartOrderMerger.setIsScorePay(orderParam.getIsScorePay());
        shopCartOrderMerger.setDvyTypes(orderParam.getDvyTypes());
        shopCartOrderMerger.setUsableScore(orderParam.getUserUseScore());
        shopCartOrderMerger.setOrderType(OrderType.ORDINARY);
        return shopCartOrderMerger;
    }

    private void setParmamDvytype(OrderParam orderParam) {
        if (Objects.isNull(orderParam.getDvyTypes()) && Objects.nonNull(orderParam.getOrderItem())){
            log.info("pc端直接下单，默认配送方式为快递");
            orderParam.setDvyTypes(Collections.singletonList(new DvyTypeDTO(orderParam.getOrderItem().getShopId(), DvyType.DELIVERY.value())));
        }
    }

    private void setCartDvytype(OrderParam orderParam, List<ShopCartItemDto> shopCartItemsDb, ShopCartOrderMergerDto shopCartOrderMerger) {
        if (Objects.isNull(orderParam.getDvyTypes()) && CollectionUtil.isNotEmpty(shopCartItemsDb)) {
            //购物车下单
            log.info("PC端购物车下单，默认配送方式为快递");
            List<DvyTypeDTO> dvyTypes = new ArrayList<>();
            Set<Long> shopIdSet = shopCartItemsDb.stream().map(ShopCartItemDto::getShopId).collect(Collectors.toSet());
            shopIdSet.forEach(shopId -> dvyTypes.add(new DvyTypeDTO(shopId, DvyType.DELIVERY.value())));
            orderParam.setDvyTypes(dvyTypes);
            shopCartOrderMerger.setDvyTypes(dvyTypes);
        }
    }

    private void seMergerUserAddr(OrderParam orderParam, String userId, ShopCartOrderMergerDto shopCartOrderMerger){
        //订单的地址信息
        UserAddr userAddr = userAddrService.getUserAddrByUserId(orderParam.getAddrId(), userId);
        shopCartOrderMerger.setUserAddr(BeanUtil.map(userAddr, UserAddrDto.class));
        shopCartOrderMerger.setShopCartOrders(new ArrayList<>());
    }

    private void checkOrderType(ShopCartOrderMergerDto shopCartOrderMerger, List<ShopCartItemDto> shopCartItems, OrderParam orderParam, int mold) {
        ShopCartItemDto firstShopCartItem = shopCartItems.get(0);
        // 是否为预售订单
        orderParam.setPreSellStatus(firstShopCartItem.getPreSellStatus());
        shopCartOrderMerger.setPreSellStatus(firstShopCartItem.getPreSellStatus());
        if (shopCartItems.stream().filter(shopCartItemDto -> shopCartItemDto.getMold() == 1).count() == shopCartItems.size()) {
            // 订单项中的所有商品都为虚拟商品时，才是虚拟订单
            log.info("判断该订单所有商品是否都是虚拟商品");
            mold = 1;
            shopCartOrderMerger.getDvyTypes().forEach(s->s.setDvyType(DeliveryType.NO_EXPRESS.getValue()));
        }
        shopCartOrderMerger.setMold(mold);
    }
}
