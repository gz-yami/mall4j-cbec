/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.delivery.multishop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yami.shop.bean.enums.DvyType;
import com.yami.shop.bean.enums.OrderActivityType;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.model.OrderItem;
import com.yami.shop.bean.model.UserAddrOrder;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.delivery.common.model.DeliveryOrder;
import com.yami.shop.delivery.common.param.DeliveryOrderItemParam;
import com.yami.shop.delivery.common.param.DvyOrderParam;
import com.yami.shop.delivery.common.param.OrderParam;
import com.yami.shop.delivery.common.service.DeliveryOrderService;
import com.yami.shop.service.OrderItemService;
import com.yami.shop.service.OrderService;
import com.yami.shop.service.UserAddrOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author lhd on 2020/05/15.
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/order/delivery")
@Tag(name = "商家端物流接口")
public class OrderDeliveryController {

    private final OrderItemService orderItemService;

    private final UserAddrOrderService userAddrOrderService;

    private final OrderService orderService;

    private final DeliveryOrderService deliveryOrderService;



    /**
     * 订单项待发货数量查询
     */
    @GetMapping("/getOrderItemUnDelivery/{orderNumber}")
    @Operation(summary = "订单项待发货数量查询")
    @Parameter(name = "orderNumber", description = "订单号" )
    @PreAuthorize("@pms.hasPermission('order:delivery:unDeliveryInfo')")
    public ServerResponseEntity<Order> getOrderItemUnDelivery(@PathVariable("orderNumber") String orderNumber) {
        Long shopId = Constant.PLATFORM_SHOP_ID;
        Order order = orderService.getOrderByOrderNumberAndShopId(orderNumber, shopId, true);
        if (Objects.isNull(order)) {
            // 订单不存在
            throw new YamiShopBindException("yami.order.no.exist");
        }
        UserAddrOrder userAddrOrder = userAddrOrderService.getById(order.getAddrOrderId());
        order.setUserAddrOrder(userAddrOrder);
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderNumber(orderNumber);
        if (CollectionUtils.isEmpty(orderItems)) {
            // 订单不存在
            throw new YamiShopBindException("yami.order.no.exist");
        }
        //查询已发货数量
        for (OrderItem orderItem : orderItems) {
            //根据状态查询可发货数量
            orderItem.setChangeNum(orderItem.getStatus() == -1 ? orderItem.getProdCount() : orderItem.getStatus());
            orderItem.setProdCount(orderItem.getChangeNum());
        }
        order.setOrderItems(orderItems);
        return ServerResponseEntity.success(order);
    }

    @PutMapping("/orderItemsDelivery")
    @Operation(summary = "订单项分包裹发货")
    @PreAuthorize("@pms.hasPermission('order:delivery:orderItemsDelivery')")
    public ServerResponseEntity<Void> orderItemsDelivery(@RequestBody DeliveryOrderItemParam deliveryOrderParam) {
        Long shopId = Constant.PLATFORM_SHOP_ID;
        deliveryOrderService.saveDeliveriesInfo(deliveryOrderParam, shopId);
        return ServerResponseEntity.success();
    }

    @PutMapping("/getOrderDeliveries/{orderNumber}")
    @Operation(summary = "根据订单号查询订单物流包裹信息")
    @Parameter(name = "orderNumber", description = "订单号" )
    @PreAuthorize("@pms.hasPermission('order:delivery:deliveryInfo')")
    public ServerResponseEntity<List<DeliveryOrder>> getOrderDeliveries(@PathVariable("orderNumber") String orderNumber) {
        List<DeliveryOrder> deliveryOrders = deliveryOrderService.listDetailDelivery(orderNumber);
        return ServerResponseEntity.success(deliveryOrders);
    }

    @PutMapping("/updateOrderDeliveries")
    @Operation(summary = "修改订单物流包裹信息")
    @PreAuthorize("@pms.hasPermission('order:delivery:update')")
    public ServerResponseEntity<Void> updateOrderDeliveries(@RequestBody DvyOrderParam dvyOrderParam) {
        List<DeliveryOrder> deliveryOrders = dvyOrderParam.getDeliveryOrders();
        deliveryOrderService.updateOrderDeliveries(deliveryOrders);
        return ServerResponseEntity.success();
    }


    @GetMapping("/orderInfo/{orderNumber}")
    @PreAuthorize("@pms.hasPermission('order:get:info')")
    @Operation(summary = "根据订单号获取订单物流包裹信息")
    @Parameter(name = "orderNumber", description = "订单号" )
    public ServerResponseEntity<OrderParam> info(@PathVariable("orderNumber") String orderNumber) {
        Long shopId = Constant.PLATFORM_SHOP_ID;
        Order order = orderService.getOrderDetailByOrderNumberAndShopId(orderNumber, shopId);
        if (Objects.isNull(order)) {
            // 订单不存在
            throw new YamiShopBindException("yami.order.no.exist");
        }
        orderItemService.handleActivityOrderItem(order.getOrderItems(),null);
        OrderParam orderParam = BeanUtil.map(order, OrderParam.class);
        orderParam.setFreeFreightAmount(order.getFreeTransfee());
        orderParam.setScore(orderParam.getOrderItems().stream().mapToLong(OrderItem::getUseScore).sum());
        //查询包裹信息
        List<DeliveryOrder> deliveryOrders = deliveryOrderService.getAndDeliveryItemInfo(orderNumber);
        orderParam.setDeliveryExpresses(deliveryOrders);
        return ServerResponseEntity.success(orderParam);
    }

    @GetMapping("/deliveryOrder/{orderDeliveryId}")
    @Operation(summary = "物流包裹查询接口" , description = "根据orderDeliveryId查询订单包裹信息")
    @Parameter(name = "orderDeliveryId", description = "订单物流包裹id" )
    @PreAuthorize("@pms.hasPermission('order:delivery:info')")
    public ServerResponseEntity<DeliveryOrder> info(@PathVariable("orderDeliveryId") Long orderDeliveryId) {
        DeliveryOrder deliveryOrder = deliveryOrderService.deliveryOrderItemInfo(orderDeliveryId);
        return ServerResponseEntity.success(deliveryOrder);
    }
}
