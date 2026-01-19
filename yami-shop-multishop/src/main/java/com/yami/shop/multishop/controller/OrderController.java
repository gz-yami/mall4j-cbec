/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.multishop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.enums.DeliveryType;
import com.yami.shop.bean.enums.OrderStatus;
import com.yami.shop.bean.event.OrderChangeAddrEvent;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.model.OrderItem;
import com.yami.shop.bean.model.UserAddrOrder;
import com.yami.shop.bean.param.OrderChangeAddrParam;
import com.yami.shop.bean.param.OrderParam;
import com.yami.shop.bean.vo.UnDeliveryOrderExcelVO;
import com.yami.shop.common.annotation.SysLog;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.response.ResponseEnum;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lgh on 2018/09/15.
 */
@RestController
@RequestMapping("/order/order")
@Tag(name = "订单接口")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final OrderItemService orderItemService;

    private final UserAddrOrderService userAddrOrderService;

    private final ApplicationContext applicationContext;


    @GetMapping("/page")
    @Operation(summary = "分页获取订单列表" , description = "分页获取订单列表")
    @PreAuthorize("@pms.hasPermission('order:order:page')")
    public ServerResponseEntity<IPage<Order>> page(OrderParam orderParam, PageParam<Order> page) {
        Long shopId = Constant.PLATFORM_SHOP_ID;
        orderParam.setShopId(shopId);
        IPage<Order> orderPage = orderService.pageOrdersDetailByOrderParam(page, orderParam);
        return ServerResponseEntity.success(orderPage);
    }

    @GetMapping("/orderInfo/{orderNumber}")
    @Operation(summary = "根据订单号获取订单信息" , description = "根据订单号获取订单信息")
    @PreAuthorize("@pms.hasPermission('order:order:info')")
    public ServerResponseEntity<Order> info(@PathVariable("orderNumber") String orderNumber) {
        Long shopId = Constant.PLATFORM_SHOP_ID;
        Order order = orderService.getOrderByOrderNumberAndShopId(orderNumber, shopId, true);
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderNumber(orderNumber);
        order.setOrderItems(orderItems);
        UserAddrOrder userAddrOrder = userAddrOrderService.getById(order.getAddrOrderId());
        order.setUserAddrOrder(userAddrOrder);
        return ServerResponseEntity.success(order);
    }

    @PutMapping("/changeAmount")
    @Operation(summary = "修改订单金额" , description = "修改订单金额")
    @PreAuthorize("@pms.hasPermission('order:order:update')")
    public ServerResponseEntity<Void> changeAmount(@RequestBody Order order) {
        if (order.getFreightAmount() < 0) {
            // 运费不能小于零
            throw new YamiShopBindException("yami.product.dvy.fee");
        }
        if (Objects.isNull(order.getFreightAmount())) {
            order.setFreightAmount(0.00);
        }
        orderService.changeAmount(order);
        return ServerResponseEntity.success();
    }

    @GetMapping("/getChangeAmount")
    @Operation(summary = "查询修改订单地址后的运费")
    @PreAuthorize("@pms.hasPermission('order:order:update')")
    public ServerResponseEntity<Double> getChangeAmount(OrderChangeAddrParam order) {
        double changeAmount = 0.0;
        OrderChangeAddrEvent orderChangeAddrEvent = new OrderChangeAddrEvent(order, changeAmount);
        applicationContext.publishEvent(orderChangeAddrEvent);
        return ServerResponseEntity.success(orderChangeAddrEvent.getChangeAmount());
    }

    @PutMapping("/changeUserAddr")
    @Operation(summary = "修改用户订单地址" , description = "修改用户订单地址")
    @PreAuthorize("@pms.hasPermission('order:order:update')")
    public ServerResponseEntity<String> changeUserAddr(@RequestBody @Valid Order order) {
        Order orderDb = orderService.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderId, order.getOrderId())
                .eq(Order::getOrderNumber, order.getOrderNumber())
        );
        if (orderDb.getStatus() <= OrderStatus.PADYED.value() || Objects.equals(orderDb.getStatus(), OrderStatus.WAIT_GROUP.value())) {
            UserAddrOrder userAddrOrder = order.getUserAddrOrder();
            userAddrOrderService.updateAddrInfoById(userAddrOrder, orderDb);
        } else {
            // 订单状态异常，无法更改订单地址
            throw new YamiShopBindException("yami.order.status.error");
        }
        // 修改成功
        return ServerResponseEntity.success(ResponseEnum.OK.value(), I18nMessage.getMessage("yami.activity.update.success"));
    }

    @SysLog("修改订单备注")
    @PutMapping("/changeOrderRamark")
    @Operation(summary = "修改订单备注" , description = "修改订单备注")
    @PreAuthorize("@pms.hasPermission('order:order:update')")
    public ServerResponseEntity<String> changeOrderRamark(@RequestBody @Valid Order order) {
        Order orderDb = orderService.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderId, order.getOrderId())
                .eq(Order::getOrderNumber, order.getOrderNumber())
        );
        orderDb.setShopRemarks(order.getShopRemarks());
        orderService.updateById(orderDb);
        // 修改成功
        return ServerResponseEntity.success(ResponseEnum.SHOW_SUCCESS.value(), I18nMessage.getMessage("yami.activity.update.success"));
    }


    @GetMapping("/getOrderByUserId")
    @Operation(summary = "分页获取用户订单列表" , description = "分页获取用户订单列表")
    @Parameter(name = "userId", description = "用户id" )
    @PreAuthorize("@pms.hasPermission('order:order:pageByUserId')")
    public ServerResponseEntity<IPage<Order>> getOrderByUserId(PageParam<Order> page, String userId){
        Long shopId = Constant.PLATFORM_SHOP_ID;
        IPage<Order> pages = orderService.pageByUserIdAndShopId(page,userId,shopId);
        return ServerResponseEntity.success(pages);
    }


}
