/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.yami.shop.bean.app.dto.*;
import com.yami.shop.bean.enums.*;
import com.yami.shop.bean.event.*;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.param.OrderParam;
import com.yami.shop.bean.vo.*;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.constants.CacheNames;
import com.yami.shop.common.enums.PayType;
import com.yami.shop.common.enums.StatusEnum;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.response.ResponseEnum;
import com.yami.shop.common.util.*;
import com.yami.shop.dao.OrderMapper;
import com.yami.shop.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lgh on 2018/09/15.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    private  final OrderMapper orderMapper;
    private  final DeliveryService deliveryService;
    private  final OrderItemService orderItemService;
    private  final ApplicationEventPublisher eventPublisher;


    @Override
    public Order getOrderByOrderNumber(String orderNumber) {
        return orderMapper.getOrderByOrderNumber(orderNumber);
    }

    @Override
    public Order getOrderByOrderNumberAndUserId(String orderNumber, String userId, boolean valid) {
        Order order = orderMapper.getOrderByOrderNumberAndUserId(orderNumber, userId);
        if (valid && Objects.isNull(order)) {
            // 订单不存在
            throw new YamiShopBindException("yami.order.no.exist");
        }
        return order;
    }

    @Override
    public Order getOrderByOrderNumberAndShopId(String orderNumber, Long shopId, boolean valid) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNumber, orderNumber).eq(Order::getShopId, shopId));
        if (valid && order == null) {
            // 订单不存在
            throw new YamiShopBindException("yami.order.no.exist");
        }
        return order;
    }


    @Override
    @CacheEvict(cacheNames = "ConfirmOrderCache", key = "#userId")
    public void removeConfirmOrderCache(String userId) {
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Order> submit(ShopCartOrderMergerDto mergerOrder) {
        String userId = mergerOrder.getUserId();

        List<Order> orderList = new ArrayList<>();
        // 提交订单
        eventPublisher.publishEvent(new SubmitOrderEvent(mergerOrder, orderList));
        List<OrderItem> orderItems = orderList.stream().flatMap(order -> order.getOrderItems().stream()).collect(Collectors.toList());
        // 插入订单
        saveBatch(orderList);

        return orderList;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delivery(Order order) {
        orderMapper.updateById(order);
        // 消息推送-发货提醒
        Delivery delivery = deliveryService.getById(order.getDvyId());
        String dvyName = "";
        if (delivery.getDvyName() != null) {
            dvyName = delivery.getDvyName();
        }
    }


    @Override
    public List<Order> listUnRefundOrderAndOrderItems(Integer orderStatus, DateTime lessThanUpdateTime) {
        return orderMapper.listUnRefundOrderAndOrderItems(orderStatus, lessThanUpdateTime);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiptOrder(List<Order> orders) {
        if (CollectionUtils.isEmpty(orders)) {
            return;
        }
        int updateCount = orderMapper.receiptOrder(orders);
        if (Objects.equals(updateCount, 0)) {
            return;
        }
        // 更新数量不一致，获取更新成功的数据
        if (!Objects.equals(updateCount, orders.size())) {
            Map<Long, Order> orderMap = orders.stream().collect(Collectors.toMap(Order::getOrderId, x -> x));
            List<Order> updateOrders = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                    .eq(Order::getStatus, OrderStatus.SUCCESS.value())
                    .in(Order::getOrderId, orderMap.keySet()));
            orders = new ArrayList<>(updateOrders.size());
            for (Order updateOrder : updateOrders) {
                orders.add(orderMap.get(updateOrder.getOrderId()));
            }
        }
        for (Order order : orders) {
            eventPublisher.publishEvent(new ReceiptOrderEvent(order));
        }

    }

    @Override
    public IPage<Order> pageOrdersDetailByOrderParam(Page<Order> page, OrderParam orderParam) {
        Date date = new Date();
        List<Order> orders = orderMapper.listOrdersDetailByOrderParam(new PageAdapter(page), orderParam);
        for (Order order : orders) {
            if (ObjectUtils.isEmpty(order.getReceiverName())) {
                order.setReceiverName(I18nMessage.getMessage("yami.user.off"));
            }
            order.setReceiverMobile(order.getReceiverMobile() != null ? PhoneUtil.hideBetween(order.getReceiverMobile()).toString() : null);
            if (Objects.nonNull(order.getUserAddrOrder())) {
                order.getUserAddrOrder().setMobile(order.getUserAddrOrder().getMobile() != null ? PhoneUtil.hideBetween(order.getUserAddrOrder().getMobile()).toString() : order.getUserAddrOrder().getMobile());
            }
            if (Objects.equals(order.getOrderMold(), 1) && Objects.nonNull(order.getWriteOffEnd()) && DateUtil.compare(order.getWriteOffEnd(), date) < 0) {
                order.setWriteOffCodes(new ArrayList<>());
            }
            orderItemService.handleActivityOrderItem(order.getOrderItems(),null);
        }
        page.setRecords(orders);
        page.setTotal(orderMapper.countOrderDetail(orderParam));
        return page;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrders(List<Order> orders) {
        orderMapper.deleteOrders(orders);
    }

    @Override
    public OrderCountData getOrderCount(String userId) {
        // 获取普通订单数据
        OrderCountData countData = orderMapper.getOrderCount(userId);
        return countData;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeAmount(Order order) {
        Order orderDb = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderId, order.getOrderId())
                .eq(Order::getOrderNumber, order.getOrderNumber())
        );
        if (!Objects.equals(orderDb.getStatus(), OrderStatus.UNPAY.value())) {
            // 订单状态异常，无法更改订单金额
            throw new YamiShopBindException("yami.order.unrecognized.update");
        }
        int changeAmountVersion = 0;
        if (Objects.nonNull(orderDb.getChangeAmountVersion())) {
            changeAmountVersion = orderDb.getChangeAmountVersion() + 1;
        }
        // 计算运费变化金额
        if (!Objects.equals(Arith.sub(orderDb.getFreightAmount(), orderDb.getPlatformFreeFreightAmount()), order.getFreightAmount())) {
            if (order.getFreightAmount() < 0) {
                // 运费金额不能小于0
                throw new YamiShopBindException("yami.order.exception.feeCannotLessThanZero");
            }
            double changeFreightAmount = Arith.sub(order.getFreightAmount(), orderDb.getFreightAmount());
            //修改运费信息
            if (orderDb.getFreightAmount() == 0 && orderDb.getFreeTransfee() < 0) {
                // 商家运费模板包邮
                orderDb.setFreeTransfee(0d);
            } else if (orderDb.getFreeTransfee() < 0 && changeFreightAmount < 0) {
                // 商家有商品运费模板包邮
                orderDb.setFreeTransfee(Arith.add(orderDb.getFreeTransfee(), changeFreightAmount));
            }
            orderDb.setFreightAmount(order.getFreightAmount());
            orderDb.setActualTotal(Arith.add(orderDb.getActualTotal(), changeFreightAmount));
        }
        orderDb.setChangeAmountVersion(changeAmountVersion);
        orderMapper.updateById(orderDb);
    }

    @Override
    public List<Order> getOrderPayInfoByOrderNumber(List<String> orderNumberList) {
        return orderMapper.getOrderPayInfoByOrderNumber(orderNumberList);
    }

    @Override
    public Order getOrderDetailByOrderNumberAndShopId(String orderNumber, Long shopId) {
        Order order = orderMapper.getOrderDetailByOrderNumberAndShopId(orderNumber, shopId);
        if (Objects.isNull(order) || CollUtil.isEmpty(order.getOrderItems())){
            return order;
        }
        if (ObjectUtils.isEmpty(order.getNickName())) {
            order.setNickName(I18nMessage.getMessage("yami.user.off"));
        }
        return order;
    }

    @Override
    public Integer countByOrderNumber(String orderNumber) {
        return orderMapper.countByOrderNumber(orderNumber);
    }

    @Override
    public UnDeliveryOrderExcelVO getOrderAndOrderItemsByOrderNumberAndShopId(String orderNumber, Long shopId) {
        return orderMapper.getOrderAndOrderItemsByOrderNumberAndShopId(orderNumber, shopId);
    }

    @Override
    public List<Order> getOrdersStatus(List<String> orderNumbers) {
        List<Order> orderList = list(new LambdaQueryWrapper<Order>()
                .select(Order::getOrderNumber,Order::getStatus,Order::getOrderType,Order::getActualTotal,Order::getUserId,Order::getShopId,Order::getCreateTime, Order::getOrderMold, Order::getDvyType).in(Order::getOrderNumber,orderNumbers));
        for (String orderNumber : orderNumbers) {
            boolean hasOrderNumber = false;
            for (Order orderStatusBO : orderList) {
                if (StrUtil.equals(orderStatusBO.getOrderNumber(), orderNumber)) {
                    hasOrderNumber = true;
                    break;
                }
            }
            if (!hasOrderNumber) {
                Order orderStatusBO = new Order();
                orderStatusBO.setOrderNumber(orderNumber);
                orderList.add(orderStatusBO);
            }
        }
        return orderList;
    }

    @Override
    public OrderAmountVO getOrdersAmountAndIfNoCancel(String userId, List<String> orderNumbers, Integer payType) {
        List<Order> ordersStatus = getOrdersStatus(orderNumbers);
        if (CollUtil.isEmpty(ordersStatus)) {
            // 订单不存在
            throw new YamiShopBindException("yami.order.no.exist");
        }
        Map<Long,ShopAmountVO> receiveMap = new HashMap<>(8);
        for (Order order : ordersStatus) {
            if (!Objects.equals(order.getUserId(), userId)) {
                throw new YamiShopBindException(ResponseEnum.UNAUTHORIZED);
            }
            if (!Objects.equals(order.getStatus(), OrderStatus.UNPAY.value())) {
                // 支付失败，订单不在未支付状态
                throw new YamiShopBindException("yami.order.pay.fail");
            }
            if (Objects.isNull(order.getStatus()) || Objects.equals(order.getStatus(), OrderStatus.CLOSE.value())) {
                // 订单已关闭
                throw new YamiShopBindException("yami.order.is.closed");
            }
            // 普通订单创建时间经过限定订单超时分钟，如果为true则早于当前时间表示过期，抛出异常
            boolean isExpired = !DateUtil.offsetMinute(order.getCreateTime(), Constant.ORDER_MAX_TIME).after(new Date());
            if(isExpired){
                // 秒杀订单已经过期，无法进行支付
                throw new YamiShopBindException("yami.order.expired");
            }
            order.setPayType(payType);
            update(order, Wrappers.lambdaUpdate(Order.class).eq(Order::getOrderNumber, order.getOrderNumber()));
            if(Objects.equals(order.getShopId(),0L)){
                continue;
            }
            if(receiveMap.containsKey(order.getShopId())){
                ShopAmountVO shopAmountVO = receiveMap.get(order.getShopId());
                shopAmountVO.setAmount(shopAmountVO.getAmount() + Arith.toLongCent(order.getActualTotal()));

            }else {
                ShopAmountVO shopAmountVO = new ShopAmountVO();
                shopAmountVO.setAmount(Arith.toLongCent(order.getActualTotal()));
                receiveMap.put(order.getShopId(),shopAmountVO);
            }
        }
        OrderAmountVO ordersActualAmount = orderMapper.getOrdersActualAmount(orderNumbers);
        ordersActualAmount.setReceiveList(new ArrayList<>(receiveMap.values()));
        return ordersActualAmount;
    }

    @Override
    public void updateByToPaySuccess(List<String> orderNumbers) {
        int updateNum = orderMapper.updateByToPaySuccess(orderNumbers);
        if (updateNum < 1) {
            throw new YamiShopBindException("yami.order.payed.repeat.operation");
        }
    }

    @Override
    public Order getOrderAndOrderItemByOrderNumber(String orderNumber) {
        return orderMapper.getOrderAndOrderItemByOrderNumber(orderNumber);
    }


    @Override
    public IPage<Order> pageByUserId(PageParam<Order> page, String userId) {
        return orderMapper.getPageByUserId(page, userId);
    }


    @Override
    public IPage<MyOrderDto> orderListByStatus(PageParam<MyOrderDto> page, Integer status, Long stationId) {
        List<MyOrderDto> myOrderDtos = orderMapper.orderListByStatusAndStationId(new PageAdapter(page), status, stationId);
        for (MyOrderDto myOrderDto : myOrderDtos) {
            for (MyOrderItemDto orderItemDto : myOrderDto.getOrderItemDtos()) {
                if (Objects.equals(myOrderDto.getOrderMold(), 1)) {
                    orderItemDto.setProdCount(myOrderDto.getProductNums());
                }
            }
            orderItemService.handleActivityOrderItem(null,myOrderDto.getOrderItemDtos());
        }
        page.setRecords(myOrderDtos);
        List<MyOrderDto> orderDtoList = page.getRecords();
        for (MyOrderDto myOrderDto : orderDtoList) {
            if (StrUtil.isBlank(myOrderDto.getShopName())) {
                myOrderDto.setShopName(Constant.PLATFORM_SHOP_NAME);
            }
        }
        page.setTotal(orderMapper.countByStatusAndStationId(status, stationId));
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean orderStationByOrderNumber(List<String> orderNumberList, String userId, Long stationId) {
        if (CollUtil.isEmpty(orderNumberList)) {
            // 请选择最少一个需要自提的订单
            throw new YamiShopBindException("yami.order.need.station");
        }
        List<Order> orderList = orderMapper.getStationOrderByOrderNumbers(orderNumberList, userId, null);
        Order orderParam = new Order();
        if (CollectionUtils.isEmpty(orderList)) {
            // 订单不存在
            throw new YamiShopBindException("yami.order.no.exist");
        }
        List<String> updateOrderNumbers = new ArrayList<>(16);
        for (Order order : orderList) {
            if (Objects.isNull(order)) {
                // 订单不存在
                throw new YamiShopBindException("yami.order.no.exist");
            }
            // 如果不是自提订单且不是核销订单就直接报错，如果是扫商家码进来的就不需要自提点id
            boolean notExist = !Objects.equals(order.getDvyType(), DvyType.STATION.value()) || (!Objects.equals(order.getDvyId(), stationId) && !Objects.equals(stationId, null));
            if (notExist)  {
                // 订单不存在
                throw new YamiShopBindException("yami.order.no.exist");
            }
            if (!Objects.equals(order.getStatus(), OrderStatus.PADYED.value())) {
                // 订单已提货
                throw new YamiShopBindException(I18nMessage.getMessage("yami.order") + order.getOrderNumber() + I18nMessage.getMessage("yami.order.station.finish"));
            }
            order.setStatus(OrderStatus.CONSIGNMENT.value());
            order.setDvyTime(new Date());
            order.setUpdateTime(new Date());
            updateOrderNumbers.add(order.getOrderNumber());
        }

        updateBatchById(orderList);
        // 修改订单对应的商品，将状态修改全部已发货
        if (!CollectionUtils.isEmpty(updateOrderNumbers)) {
            Integer allDelivery = 0;
            orderItemService.update(new LambdaUpdateWrapper<OrderItem>()
                    .set(OrderItem::getStatus, allDelivery)
                    .in(OrderItem::getOrderNumber, updateOrderNumbers));
        }
        return true;
    }

    @Override
    public IPage<Order> pageByUserIdAndShopId(PageParam<Order> page, String userId, Long shopId) {
        return orderMapper.getPageByUserIdAndShopId(page, userId, shopId);
    }

    @Override
    public OrderShopDto orderDetail(String orderNumber, String userId, Long stationId) {
        List<String> orderNumberList = Arrays.asList(orderNumber.split(StrUtil.COMMA));
        List<OrderShopDto> orderShopDtoList = orderMapper.orderDetailByOrderNumberList(orderNumberList,userId);
        if (CollUtil.isEmpty(orderShopDtoList)) {
            throw new YamiShopBindException("yami.order.no.exist");
        }
        OrderShopDto orderShopDto = new OrderShopDto();
        if(orderShopDtoList.size() == 1 ){
            orderShopDto = orderShopDtoList.get(0);
        } else {
            orderShopDtoList =orderShopDtoList.stream()
                    .filter(item ->  !Objects.equals(item.getOrderMold(), ProdMoldEnum.COMBO.value()))
                    .toList();
            orderShopDto = orderShopDtoList.get(0);
            BigDecimal acTotal = orderShopDtoList.stream()
                    .map(item -> BigDecimal.valueOf(item.getActualTotal()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            orderShopDto.setActualTotal(acTotal.doubleValue());
        }
        // 订单项
        List<OrderItem> orderItemList = orderItemService.list(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderNumber, orderShopDto.getOrderNumber()).orderByAsc(OrderItem::getOrderItemId));
        List<OrderItemDto> orderItems = BeanUtil.mapAsList(orderItemList, OrderItemDto.class);

        // 处理一下赠品和组合商品子订单项
        Map<Long, List<OrderItemDto>> giveawawMap = orderItems.stream()
                .filter(s -> Objects.equals(s.getActivityType(), OrderActivityType.GIVEAWAY.value()))
                .collect(Collectors.groupingBy(OrderItemDto::getActivityId));
        Map<Long, List<OrderItemDto>> comboMap = orderItems.stream()
                .filter(s -> Objects.equals(s.getActivityType(), OrderActivityType.COMBO_PROD.value()))
                .collect(Collectors.groupingBy(OrderItemDto::getActivityId));
        //计算订单使用积分
        Long score = 0L;
        double total = 0.0;
        Integer totalNum = 0;
        Iterator<OrderItemDto> iterator = orderItems.iterator();
        while (iterator.hasNext()) {
            OrderItemDto orderItem = iterator.next();
            if (!Objects.equals(orderItem.getPreSellStatus(), StatusEnum.ENABLE.value())){
                orderItem.setPreSellStatus(null);
            }
            if (Objects.equals(orderItem.getActivityType(), OrderActivityType.COMBO_PROD.value()) || Objects.equals(orderItem.getActivityType(), OrderActivityType.GIVEAWAY.value())) {
                iterator.remove();
                continue;
            }
            // 主订单项插入赠品订单项
            orderItem.setGiveawayList(giveawawMap.get(orderItem.getOrderItemId()));
            // 主订单项插入组合子订单项
            orderItem.setComboList(comboMap.get(orderItem.getOrderItemId()));
            score += orderItem.getUseScore();
            total = Arith.add(total, orderItem.getProductTotalAmount());
            totalNum += orderItem.getProdCount();
        }

//        // 插入平台名称、订单项列表
//        // 设置平台店铺名称
//        if (Objects.equals(orderShopDto.getShopId(), Constant.PLATFORM_SHOP_ID)) {
//            orderShopDto.setShopName(Constant.PLATFORM_SHOP_NAME);
//        }

        orderShopDto.setOrderScore(score);
        orderShopDto.setTotal(total);
        orderShopDto.setTotalNum(totalNum);

        orderShopDto.setOrderItemDtos(orderItems);

        return orderShopDto;
    }


    @Override
    public List<Order> listOrderAndOrderItemByOrderNumber(List<String> orderNumbers) {
        if (CollUtil.isEmpty(orderNumbers)) {
            return Collections.emptyList();
        }
        return orderMapper.listOrderAndOrderItemByOrderNumber(orderNumbers);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushDeliveryTo17Tracking(DeliveryOrderSimpleVO deliveryOrderSimpleVO) {
        Order order = new Order();
        order.setOrderId(deliveryOrderSimpleVO.getOrderId());
        order.setPushDeliveryStatus(1);
        updateById(order);
    }

    @Override
    public List<OrderAndPayInfoVO> listDeliveryOutTimeOrder(DateTime lessPayTime, DateTime dateTime) {
        return orderMapper.listDeliveryOutTimeOrder(lessPayTime,dateTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrders(List<Order> orders) {
        if (CollectionUtil.isEmpty(orders)) {
            return;
        }
        int updateCount = orderMapper.cancelOrders(orders);
    }



}
