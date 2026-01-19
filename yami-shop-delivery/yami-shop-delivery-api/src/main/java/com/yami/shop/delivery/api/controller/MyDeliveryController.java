/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.delivery.api.controller;

import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.delivery.common.model.DeliveryOrder;
import com.yami.shop.delivery.common.service.DeliveryOrderService;
import com.yami.shop.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Lanhai
 */
@Slf4j
@RestController
@RequestMapping("/p/myDelivery")
@Tag(name = "我的物流接口")
@AllArgsConstructor
public class MyDeliveryController {

    private final DeliveryOrderService deliveryOrderService;
    private final DeliveryService deliveryService;

    @GetMapping("/orderInfo/{orderNumber}")
    @Operation(summary = "物流列表查询接口" , description = "根据orderNumber查询订单所有包裹信息")
    @Parameter(name = "orderNumber", description = "订单号" )
    public ServerResponseEntity<List<DeliveryOrder>> deliveryItemList(@PathVariable("orderNumber") String orderNumber) {
        //查询包裹信息
        List<DeliveryOrder> deliveryOrders = deliveryOrderService.getAndDeliveryItemInfo(orderNumber);
        return ServerResponseEntity.success(deliveryOrders);
    }


    @GetMapping("/deliveryOrder/{orderDeliveryId}")
    @Operation(summary = "物流包裹查询接口" , description = "根据orderDeliveryId查询订单包裹信息")
    @Parameter(name = "orderDeliveryId", description = "订单物流包裹id" )
    public ServerResponseEntity<DeliveryOrder> info(@PathVariable("orderDeliveryId") Long orderDeliveryId) {
        DeliveryOrder deliveryOrder = deliveryOrderService.deliveryOrderItemInfo(orderDeliveryId);
        return ServerResponseEntity.success(deliveryOrder);
    }
}
