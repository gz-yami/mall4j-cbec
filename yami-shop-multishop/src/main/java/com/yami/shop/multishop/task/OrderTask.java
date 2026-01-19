/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.multishop.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.yami.shop.bean.enums.OrderStatus;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.model.OrderItem;
import com.yami.shop.service.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * @author yami
 */
@Component
@AllArgsConstructor
public class OrderTask {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final OrderService orderService;
    private final ProductService productService;
    private final SkuService skuService;

    @XxlJob("cancelOrder")
    public void cancelOrder() {

        Date now = new Date();
        logger.info("取消超时未支付订单。。。");
        // 获取30分钟之前未支付的订单
        List<Order> orders = orderService.listUnRefundOrderAndOrderItems(OrderStatus.UNPAY.value(), DateUtil.offsetMinute(now, -30));
        if (CollectionUtil.isEmpty(orders)) {
            return;
        }
        orderService.cancelOrders(orders);
        // 移除缓存
        this.removeCache(orders);

    }


    /**
     * 确认收货
     */
    @XxlJob("confirmOrder")
    public void confirmOrder() {
        Date now = new Date();
        logger.info("系统自动确认收货订单。。。");
        // 获取15天之前等待确认收货的订单
        List<Order> orders = orderService.listUnRefundOrderAndOrderItems(OrderStatus.CONSIGNMENT.value(), DateUtil.offsetDay(now, -15));
        // 获取核销时间超出当前时间的，已支付但未完成的虚拟订单
        List<Order> overdueOrders = orderService.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderMold, 1)
                .eq(Order::getIsPayed, 1)
                .notIn(Order::getStatus, OrderStatus.SUCCESS.value(), OrderStatus.CLOSE.value())
                .lt(Order::getWriteOffEnd, new Date()));
        orders.addAll(overdueOrders);
        if (CollectionUtil.isEmpty(orders)) {
            return;
        }
        orderService.receiptOrder(orders);
        // 移除缓存
        this.removeCache(orders);
    }


    /**
     * 移除缓存
     */
    private void removeCache(List<Order> orders) {
        for (Order order : orders) {
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                productService.removeProdCacheByProdId(orderItem.getProdId());
                skuService.removeSkuCacheBySkuId(orderItem.getSkuId(), orderItem.getProdId());
            }
        }
    }
}
