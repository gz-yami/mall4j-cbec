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
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.app.dto.MyOrderDto;
import com.yami.shop.bean.app.dto.MyOrderItemDto;
import com.yami.shop.bean.app.dto.OrderItemDto;
import com.yami.shop.bean.app.dto.OrderShopDto;
import com.yami.shop.bean.enums.*;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.vo.OrderItemVO;
import com.yami.shop.common.enums.SysTypeEnum;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.Arith;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.security.api.util.SecurityUtils;
import com.yami.shop.security.common.util.AuthUserContext;
import com.yami.shop.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LGH
 */
@RestController
@RequestMapping("/p/myOrder")
@Tag(name = "我的订单接口")
@AllArgsConstructor
public class MyOrderController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final OrderService orderService;
    private final ProductService productService;
    private final SkuService skuService;
    private final MyOrderService myOrderService;
    private final OrderItemService orderItemService;

    @GetMapping("/orderDetail")
    @Operation(summary = "订单详情信息" , description = "根据订单号获取订单详情信息")
    @Parameter(name = "orderNumbers", description = "订单号（多个订单以，相隔）" , required = true)
    public ServerResponseEntity<OrderShopDto> orderDetail(@RequestParam(value = "orderNumber") String orderNumber) {
        String userId = SecurityUtils.getUser().getUserId();
        Long stationId = SecurityUtils.getUser().getStationId();
        if (Objects.equals(AuthUserContext.get().getSysType(), SysTypeEnum.STATION)) {
            userId = null;
        }
        OrderShopDto orderShopDto = orderService.orderDetail(orderNumber, userId, stationId);

        if (Objects.isNull(orderShopDto)) {
            // 订单不存在
            throw new YamiShopBindException("yami.order.no.exist");
        }
        // 返回拼团/秒杀优惠扣除店铺改价金额和平台优惠金额
        orderShopDto.setShopAmount(Arith.sub(orderShopDto.getReduceAmount(), orderShopDto.getShopChangeFreeAmount()));
        return ServerResponseEntity.success(orderShopDto);
    }

    @GetMapping("/myOrder")
    @Operation(summary = "订单列表信息" , description = "根据订单状态获取订单列表信息，状态为0时获取所有订单")
    @Parameter(name = "status", description = "订单状态 0:获取所有订单 1:待付款 2:待发货 3:待收货 4:待评价 5:成功 6:失败 7待成团" )
    public ServerResponseEntity<IPage<MyOrderDto>> myOrder(@RequestParam(value = "status") Integer status, @RequestParam(value = "prodName", required = false ) String prodName, PageParam<MyOrderDto> page) {
        String userId = SecurityUtils.getUser().getUserId();
        IPage<MyOrderDto> myOrderDtoIpage = myOrderService.pageMyOrderByUserIdAndStatus(page, userId, status, prodName);
        return ServerResponseEntity.success(myOrderDtoIpage);
    }

    @GetMapping("/myOrderSearch")
    @Operation(summary = "订单列表信息查询" , description = "根据订单编号或者订单中商品名称搜索")
    @Parameters({
            @Parameter(name = "status", description = "订单状态 1:待付款 2:待发货 3:待收货 4:待评价 5:成功 6:失败" ),
            @Parameter(name = "orderMold", description = "订单类别 0.实物商品订单 1. 虚拟商品订单" ),
            @Parameter(name = "orderName", description = "订单编号或者订单中商品名称" ),
            @Parameter(name = "orderTimeStatus", description = "0全部订单 1最近七天 2最近三个月 3三个月之前 订单" ),
            @Parameter(name = "orderType", description = "0全部订单 1拼团订单 2秒杀订单 3积分订单" ),
            @Parameter(name = "orderNumber", description = "订单编号" ),
            @Parameter(name = "shopId", description = "店铺id" )
    })
    public ServerResponseEntity<IPage<MyOrderDto>> myOrderSearch(@RequestParam(value = "status") Integer status,
                                                           @RequestParam(value = "orderName") String orderName,
                                                           @RequestParam(value = "orderTimeStatus", required = false) Integer orderTimeStatus,
                                                           @RequestParam(value = "orderType", required = false) Integer orderType,
                                                           @RequestParam(value = "orderMold") Integer orderMold,
                                                           @RequestParam(value = "orderNumber") String orderNumber,
                                                           @RequestParam(value = "shopId", required = false) Long shopId,
                                                           PageParam<MyOrderDto> page) {
        String userId = SecurityUtils.getUser().getUserId();
        IPage<MyOrderDto> myOrderDtoIpage = myOrderService.pageMyOrderByParams(page, userId, status, orderName, orderTimeStatus, orderType, orderNumber, shopId,orderMold);
        return ServerResponseEntity.success(myOrderDtoIpage);
    }

    @GetMapping("/getOrderItem")
    @Operation(summary = "获取订单项信息" , description = "根据订单项Id获取订单项信息")
    @Parameter(name = "orderItemId", description = "订单项Id" )
    public ServerResponseEntity<OrderItemVO> getOrderItem(@RequestParam(value = "orderItemId") Long orderItemId) {
        OrderItem orderItem = orderItemService.getByIdI18n(orderItemId);
        orderItem.setPic(orderItem.getPic());
        Order one = orderService.getOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNumber,orderItem.getOrderNumber()));
        OrderItemVO orderItemVO = BeanUtil.map(orderItem, OrderItemVO.class);
        orderItemVO.setPayDate(one.getPayTime());
        return ServerResponseEntity.success(orderItemVO);
    }

    @GetMapping("/getOrderItems")
    @Operation(summary = "获取订单项信息" , description = "根据订单编号orderNumber获取订单项信息")
    @Parameter(name = "orderNumber", description = "订单编号" )
    public ServerResponseEntity<List<OrderItem>> getOrderItems(@RequestParam(value = "orderNumber") String orderNumber) {
        List<OrderItem> orderItems = orderItemService.listAndPayTimeByOrderNumber(orderNumber);
        return ServerResponseEntity.success(orderItems);
    }

    @GetMapping("/myOrderComment")
    @Operation(summary = "订单评价列表接口" , description = "根据订单评价状态获取订单列表信息")
    public ServerResponseEntity<IPage<MyOrderDto>> myOrderComment(PageParam<MyOrderDto> page) {
        String userId = SecurityUtils.getUser().getUserId();
        IPage<MyOrderDto>  myOrderDtoIpage = myOrderService.myOrderComment(page, userId);
        return ServerResponseEntity.success(myOrderDtoIpage);
    }

    @GetMapping("/myOrderItemsComment")
    @Operation(summary = "订单项评价列表接口" , description = "根据订单评价状态获取订单列表信息")
    @Parameter(name = "commStatus", description = "订单状态 0:待评价 1已评价" )
    public ServerResponseEntity<IPage<MyOrderItemDto>> myOrderItemsComment(@RequestParam(value = "commStatus") Integer commStatus,
                                                                     PageParam<MyOrderItemDto> page) {
        String userId = SecurityUtils.getUser().getUserId();
        IPage<MyOrderItemDto> myOrderDtoIpage = myOrderService.myOrderItemsComment(page, userId, commStatus);
        return ServerResponseEntity.success(myOrderDtoIpage);
    }


    @PutMapping("/receipt/{orderNumber}")
    @Operation(summary = "根据订单号确认收货" , description = "根据订单号确认收货")
    @Parameter(name = "orderNumber", description = "订单编号" , required = true)
    public ServerResponseEntity<String> receipt(@PathVariable("orderNumber") String orderNumber) {
        String userId = SecurityUtils.getUser().getUserId();
        Order order = orderService.getOrderByOrderNumberAndUserId(orderNumber, userId, true);

        if (!Objects.equals(order.getStatus(), OrderStatus.CONSIGNMENT.value())) {
            // 订单不处于待收货状态，无法确认收货
            throw new YamiShopBindException("yami.order.no.delivery");
        }
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderNumber(orderNumber);
        order.setOrderItems(orderItems);
        // 确认收货
        orderService.receiptOrder(Collections.singletonList(order));

        for (OrderItem orderItem : orderItems) {
            productService.removeProdCacheByProdId(orderItem.getProdId());
            skuService.removeSkuCacheBySkuId(orderItem.getSkuId(), orderItem.getProdId());
        }
        return ServerResponseEntity.success();
    }

    @DeleteMapping("/{orderNumber}")
    @Operation(summary = "根据订单号删除订单" , description = "根据订单号删除订单")
    @Parameter(name = "orderNumber", description = "订单号" , required = true)
    public ServerResponseEntity<String> delete(@PathVariable("orderNumber") String orderNumber) {
        String userId = SecurityUtils.getUser().getUserId();
        Order order = orderService.getOrderByOrderNumberAndUserId(orderNumber, userId, true);
        if (!Objects.equals(order.getStatus(), OrderStatus.SUCCESS.value()) && !Objects.equals(order.getStatus(), OrderStatus.CLOSE.value())) {
            // 订单未完成或未关闭，无法删除订单
            throw new YamiShopBindException("yami.order.no.success");
        }
        // 删除订单
        orderService.deleteOrders(Collections.singletonList(order));
        // 删除成功
        return ServerResponseEntity.success(I18nMessage.getMessage("yami.delete.successfully"));
    }

    @GetMapping("/getOrderStatus")
    @Operation(summary = "根据订单编号获取订单状态")
    public ServerResponseEntity<OrderShopDto> getOrderStatus(@RequestParam("orderNumber") String orderNumber) {
        List<String> orderNumberList = Arrays.asList(orderNumber.split(StrUtil.COMMA));
        List<Order> orderList = orderService.getOrdersStatus(orderNumberList);
        Order order = orderList.stream().filter(item -> Objects.equals(item.getOrderMold(), 0)).toList().get(0);
        OrderShopDto orderShopDto = new OrderShopDto();
        orderShopDto.setStatus(order.getStatus());
        orderShopDto.setRefundStatus(order.getRefundStatus());
        // 如果是虚拟商品订单，处理下虚拟商品订单相关数据
        if(Objects.equals(order.getOrderMold(),1)) {
            Long stationId = SecurityUtils.getUser().getStationId();
            orderShopDto.setOrderMold(order.getOrderMold());
            // 虚拟商品留言信息
            orderShopDto.setVirtualRemark(order.getVirtualRemark());
            orderShopDto.setWriteOffStart(order.getWriteOffStart());
            orderShopDto.setWriteOffEnd(order.getWriteOffEnd());
            orderShopDto.setWriteOffNum(order.getWriteOffNum());
            orderShopDto.setWriteOffMultipleCount(order.getWriteOffMultipleCount());
        }
        return ServerResponseEntity.success(orderShopDto);
    }

    @PutMapping("/cancel/{orderNumber}")
    @Operation(summary = "根据订单编号取消订单" , description = "根据订单编号取消订单")
    @Parameter(name = "orderNumber", description = "订单编号" , required = true)
    public ServerResponseEntity<String> cancel(@PathVariable("orderNumber") String orderNumber) {
        String userId = SecurityUtils.getUser().getUserId();
        Order order = orderService.getOrderByOrderNumberAndUserId(orderNumber, userId, true);
        if (!Objects.equals(order.getStatus(), OrderStatus.UNPAY.value())) {
            // 订单已支付，无法取消订单
            throw new YamiShopBindException("yami.order.status.change");
        }

        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderNumber(orderNumber);
        order.setOrderItems(orderItems);
        // 取消订单
        orderService.cancelOrders(Collections.singletonList(order));
        // 清除缓存
        for (OrderItem orderItem : orderItems) {
            productService.removeProdCacheByProdId(orderItem.getProdId());
            skuService.removeSkuCacheBySkuId(orderItem.getSkuId(), orderItem.getProdId());
        }
        return ServerResponseEntity.success();
    }

}
