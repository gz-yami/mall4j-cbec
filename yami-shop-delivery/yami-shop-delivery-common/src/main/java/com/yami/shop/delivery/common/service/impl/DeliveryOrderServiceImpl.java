/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.delivery.common.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.app.dto.DeliveryDto;
import com.yami.shop.bean.app.dto.DeliveryInfoDto;
import com.yami.shop.bean.enums.*;
import com.yami.shop.bean.model.Delivery;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.model.OrderItem;
import com.yami.shop.bean.vo.DeliveryOrderSimpleVO;
import com.yami.shop.common.enums.StatusEnum;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.dao.OrderItemMapper;
import com.yami.shop.delivery.common.dao.DeliveryOrderMapper;
import com.yami.shop.delivery.common.model.DeliveryOrder;
import com.yami.shop.delivery.common.model.DeliveryOrderItem;
import com.yami.shop.delivery.common.param.DeliveryOrderItemParam;
import com.yami.shop.delivery.common.param.OrderItemParam;
import com.yami.shop.delivery.common.service.DeliveryOrderItemService;
import com.yami.shop.delivery.common.service.DeliveryOrderService;
import com.yami.shop.service.DeliveryService;
import com.yami.shop.service.OrderService;
import com.yami.shop.service.ShopDetailService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 *
 * @author lhd
 * @date 2020-04-22 15:46:05
 */
@Service
@AllArgsConstructor
public class DeliveryOrderServiceImpl extends ServiceImpl<DeliveryOrderMapper, DeliveryOrder> implements DeliveryOrderService {

    private final DeliveryOrderMapper deliveryOrderMapper;
    private final DeliveryOrderItemService deliveryOrderItemService;
    private final OrderService orderService;
    private final DeliveryService deliveryService;
    private final OrderItemMapper orderItemMapper;
    private static final Logger logger = LoggerFactory.getLogger(DeliveryOrderServiceImpl.class);


    /**
     * 生成物流信息及保存物流与订单关联
     * @param deliveryOrderParam 物流信息
     * @param shopId 店铺id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDeliveriesInfo(DeliveryOrderItemParam deliveryOrderParam, Long shopId) {
        List<OrderItem> selectOrderItems = deliveryOrderParam.getSelectOrderItems();
        for (OrderItem selectOrderItem : selectOrderItems) {
            if (Objects.equals(selectOrderItem.getDvyType(), DvyType.DELIVERY.value())) {
                selectOrderItem.setDvyType(DvyType.DELIVERY.value());
            } else {
                selectOrderItem.setDvyType(deliveryOrderParam.getDeliveryType());
            }
        }
        String orderNumber = deliveryOrderParam.getOrderNumber();
        Order order = orderService.getOrderAndOrderItemByOrderNumber(orderNumber);
        if(!Objects.equals(order.getShopId(),shopId)){
            // 订单不存在
            throw new YamiShopBindException("yami.order.no.exist");
        }
        deliveryOrderParam.setReceiverMobile(order.getReceiverMobile());
        deliveryOrderParam.setReceiverName(order.getReceiverName());
        deliveryOrderParam.setAddrOrderId(order.getAddrOrderId());
        boolean isSend = Objects.equals(deliveryOrderParam.getDeliveryType(),DvyType.DELIVERY.value()) ||
                Objects.equals(deliveryOrderParam.getDeliveryType(), DvyType.ONLINE.value()) ||
                Objects.equals(deliveryOrderParam.getDeliveryType(),DvyType.NOT_DELIVERY.value()) ||
                Objects.equals(deliveryOrderParam.getDeliveryType(),DvyType.SAME_CITY.value());
        DeliveryOrder deliveryOrder = checkIsSend(deliveryOrderParam, selectOrderItems, order, isSend);
        orderItemMapper.updateByDeliveries(selectOrderItems);
        //判断是否发货完成
        boolean isDelivery = checkDeliveryComplete(orderNumber, order);
        // 判断发货商品名称
        if(selectOrderItems.size() != order.getOrderItems().size()){
            Set<String> names = selectOrderItems.stream().map(OrderItem::getProdName).collect(Collectors.toSet());
            order.setProdName(names.toString());
        }
        //修改为发货状态
        isDelivery(deliveryOrderParam, isDelivery, orderNumber, order);
    }


    private void isDelivery(DeliveryOrderItemParam deliveryOrderParam, boolean isDelivery, String orderNumber, Order order) {
        Date date = new Date();
        order.setUpdateTime(date);
        order.setDvyTime(date);
        //判断是无需物流还是快递发货
        order.setDvyType(deliveryOrderParam.getDeliveryType());
        // 如果是无需快递，则判断该订单是否包含有物流的项，若有物流则配送类型为快递配送，没有物流则不变
        if (Objects.equals(order.getDvyType(), DvyType.NOT_DELIVERY.value())) {
            List<DeliveryOrder> deliveryOrders = list(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getOrderNumber, orderNumber)
                    .eq(DeliveryOrder::getDeliveryType,DvyType.DELIVERY.value()));
            if (!CollectionUtils.isEmpty(deliveryOrders)){
                order.setDvyType(1);
            }
        }
        if(isDelivery) {
            order.setStatus(OrderStatus.CONSIGNMENT.value());
            orderService.updateById(order);
        }
        String dvyName = "";
        if(Objects.equals(order.getDvyType(),DvyType.SAME_CITY.value())){
            dvyName = "店铺同城配送";
            order.setDvyFlowId("-");
        }
    }

    private DeliveryOrder checkIsSend(DeliveryOrderItemParam deliveryOrderParam, List<OrderItem> selectOrderItems, Order order, boolean isSend) {
        DeliveryOrder deliveryOrder = new DeliveryOrder();
        deliveryOrder.setProductNums(0);
        if (isSend){
            //校验订单状态，发货数量及获取发货的总数
            int prodNumSum = checkDelivery(order, selectOrderItems);
            deliveryOrder.setOrderNumber(deliveryOrderParam.getOrderNumber());
            if (Objects.equals(deliveryOrderParam.getDeliveryType(),DvyType.DELIVERY.value())){
                deliveryOrder.setDvyId(deliveryOrderParam.getDvyId());
                deliveryOrder.setDvyFlowId(deliveryOrderParam.getDvyFlowId());
            } else if (Objects.equals(deliveryOrderParam.getDeliveryType(),DvyType.ONLINE.value())){
                // 线上发货DeliveryCompanyId为DeliveryCompanyType中的type
                DeliveryCompanyType deliveryCompanyType = DeliveryCompanyType.instance(deliveryOrderParam.getDeliveryCompanyType());
                if (Objects.isNull(deliveryCompanyType)) {
                    throw new YamiShopBindException("yami.delivery.info.is.null");
                }
                deliveryOrder.setDvyId(Long.valueOf(deliveryCompanyType.getType()));
            }
            deliveryOrder.setCreateTime(new Date());
            deliveryOrder.setUpdateTime(new Date());
            deliveryOrder.setStatus(1);
            deliveryOrder.setReceiverMobile(order.getReceiverMobile());
            deliveryOrder.setProductNums(prodNumSum);
            deliveryOrder.setDeliveryType(deliveryOrderParam.getDeliveryType());
            //保存订单物流信息
            save(deliveryOrder);
            //保存需要添加的关联信息
            List<DeliveryOrderItem> addDeliveries = new ArrayList<>();
            for (OrderItem selectOrderItem : selectOrderItems) {
                DeliveryOrderItem deliveryOrderItem = new DeliveryOrderItem();
                deliveryOrderItem.setOrderItemId(selectOrderItem.getOrderItemId());
                deliveryOrderItem.setProdCount(selectOrderItem.getChangeNum());
                deliveryOrderItem.setOrderDeliveryId(deliveryOrder.getOrderDeliveryId());
                addDeliveries.add(deliveryOrderItem);
            }
            if(CollectionUtils.isNotEmpty(addDeliveries)){
                deliveryOrderItemService.saveBatch(addDeliveries);
            }
        }
        return deliveryOrder;
    }


    /**
     * 修改物流信息
     * @param deliveryOrders 物流修改的信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderDeliveries(List<DeliveryOrder> deliveryOrders) {
        deliveryOrders.forEach(deliveryOrder-> deliveryOrder.setUpdateTime(new Date()));
        updateBatchById(deliveryOrders);
    }

    @Override
    public List<DeliveryOrder> getAndDeliveryItemInfo(String orderNumber) {
        List<DeliveryOrder> deliveryOrders = deliveryOrderMapper.getAndDeliveryItemInfo(orderNumber, null);
        if (CollUtil.isEmpty(deliveryOrders)) {
            return new ArrayList<>();
        }
        // 查询物流详情信息
        // 物流公司名称、官网、订单号、物流详情信息
        DeliveryOrder deliveryOrder = deliveryOrders.get(0);
        for (DeliveryOrder order : deliveryOrders) {
            Map<Long, List<OrderItemParam>> itemMap = order.getOrderItems().stream().collect(Collectors.groupingBy(OrderItemParam::getOrderItemId));
            List<OrderItemParam> orderItemParams = new ArrayList<>();
            for (Long orderItemId : itemMap.keySet()) {
                int sum = itemMap.get(orderItemId).stream().mapToInt(OrderItemParam::getProdCount).sum();
                OrderItemParam orderItemParam = itemMap.get(orderItemId).get(0);
                orderItemParam.setProdCount(sum);
                orderItemParams.add(orderItemParam);
            }
            // 根据订单项id排序
            orderItemParams = orderItemParams.stream().sorted(Comparator.comparing(OrderItemParam::getOrderItemId)).collect(Collectors.toList());
            order.setOrderItems(orderItemParams);
            for (OrderItemParam orderItem : deliveryOrder.getOrderItems()) {
                orderItem.setOrderType(deliveryOrder.getOrderType());
                if (Objects.equals(orderItem.getActivityType(),OrderActivityType.GIVEAWAY.value())){
                    orderItem.setType(1);
                }
            }
        }
        return deliveryOrders;
    }

    @Override
    public List<DeliveryOrder> listDetailDelivery(String orderNumber) {
        return deliveryOrderMapper.listDetailDelivery(orderNumber);
    }

    @Override
    public DeliveryOrder deliveryOrderItemInfo(Long orderDeliveryId) {
        List<DeliveryOrder> deliveryOrders = deliveryOrderMapper.getAndDeliveryItemInfo(null, orderDeliveryId);
        for (DeliveryOrder deliveryOrder : deliveryOrders) {
            for (OrderItemParam orderItem : deliveryOrder.getOrderItems()) {
                orderItem.setOrderType(deliveryOrder.getOrderType());
                if (Objects.equals(orderItem.getActivityType(),OrderActivityType.GIVEAWAY.value())){
                    orderItem.setType(1);
                }

                if (!Objects.equals(orderItem.getPreSellStatus(), StatusEnum.ENABLE.value())){
                    orderItem.setPreSaleTime(null);
                }
            }
        }
        if (CollUtil.isEmpty(deliveryOrders)) {
            return new DeliveryOrder();
        }
        DeliveryOrder deliveryOrder = deliveryOrders.get(0);
        // 处理下相同的sku信息集合
        Map<Long, List<OrderItemParam>> itemMap = deliveryOrder.getOrderItems().stream().collect(Collectors.groupingBy(OrderItemParam::getSkuId));
        List<OrderItemParam> orderItemParams = new ArrayList<>();
        for (Long skuId : itemMap.keySet()) {
            int sum = itemMap.get(skuId).stream().mapToInt(OrderItemParam::getProdCount).sum();
            OrderItemParam orderItemParam = itemMap.get(skuId).get(0);
            orderItemParam.setProdCount(sum);
            orderItemParams.add(orderItemParam);
        }
        deliveryOrder.setOrderItems(orderItemParams);
        return deliveryOrder;
    }

    @Override
    public List<DeliveryOrderSimpleVO> listUnpushDelivery() {
        return deliveryOrderMapper.listUnpushDelivery();
    }


    /**
     * 校验可发货数量并返回总数
     * @param order 订单信息
     * @param selectOrderItems 选择发货的订单项
     */
    private int checkDelivery(Order order, List<OrderItem> selectOrderItems) {
        if (!Objects.equals(order.getStatus(), OrderStatus.PADYED.value()) && Objects.equals(order.getDvyType(), DvyType.STATION.value())) {
            // 订单不处于待入库状态，无法进行入库
            throw new YamiShopBindException("yami.order.status.no.right");
        } else if (!Objects.equals(order.getStatus(), OrderStatus.PADYED.value())) {
            // 订单不处于待发货状态，无法进行发货
            throw new YamiShopBindException("yami.order.delivery.no.right");
        }
        Map<Long, OrderItem> itemMap = order.getOrderItems().stream().collect(Collectors.toMap(OrderItem::getOrderItemId, orderItem -> orderItem));
        //获取发货总数
        int prodNumSum = 0;
        for (OrderItem selectOrderItem : selectOrderItems) {
            prodNumSum += selectOrderItem.getChangeNum();
            if(!itemMap.containsKey(selectOrderItem.getOrderItemId())){
                // 订单不存在
                throw new YamiShopBindException("yami.order.no.exist");
            }
            OrderItem orderItemDb = itemMap.get(selectOrderItem.getOrderItemId());
            // 这里把数据库查询到的数量放进去，以免前端传了错误的数量
            selectOrderItem.setProdCount(Objects.equals(orderItemDb.getStatus(),-1) ? orderItemDb.getProdCount() : orderItemDb.getStatus());
            if(!Objects.equals(orderItemDb.getStatus(),-1) && orderItemDb.getStatus() < selectOrderItem.getChangeNum()){
                // 订单发货数量不足，请刷新后再试
                throw new YamiShopBindException("yami.order.delivery.num.check");
            }
        }
        return prodNumSum;
    }


    private boolean checkDeliveryComplete(String orderNumber, Order order) {
        boolean isDelivery = true;
        List<OrderItem> orderItems;
        // 处理一下组合商品的发货
        if(order.getOrderItems().stream().filter(orderItem -> Objects.equals(orderItem.getMold(), ProdMoldEnum.COMBO.value())).count() > 0){
            orderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderNumber, orderNumber));
            Map<Long, List<OrderItem>> childComboItems = orderItems.stream().filter(orderItem -> Objects.equals(orderItem.getActivityType(), OrderActivityType.COMBO_PROD.value()) && !Objects.equals(orderItem.getStatus(),-1))
                    .collect(Collectors.groupingBy(OrderItem::getActivityId));
            List<OrderItem> updateOrderItems = new ArrayList<>();
            for (Long mainItemId : childComboItems.keySet()) {
                // 检查下是否所有的子组合项都发货了
                List<OrderItem> items = childComboItems.get(mainItemId);
                long unDeliveryCount = items.stream().filter(orderItem -> !Objects.equals(orderItem.getStatus(), 0)).count();
                if(unDeliveryCount == 0){
                    long count = items.stream().filter(orderItem -> Objects.equals(orderItem.getDvyType(), DvyType.DELIVERY.value())).count();
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderItemId(mainItemId);
                    orderItem.setStatus(0);
                    // 如果有一个快递发货的方式,那就是快递发货,否则就是第一个的发货方式,因为如果是自提/同城配送/无需快递那就都是这三
                    orderItem.setDvyType(count > 0 ? DvyType.DELIVERY.value() : items.get(0).getDvyType());
                    orderItemMapper.updateById(orderItem);
                }
            }
        }
        if (Objects.isNull(order.getRefundStatus())){
            orderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderNumber, orderNumber));
        }else {
            orderItems = orderItemMapper.getListByOrderNumber(orderNumber);
            List<Long> orderItemIds = orderItems.stream().map(OrderItem::getOrderItemId).toList();
            orderItems.removeIf(orderItem -> Objects.equals(orderItem.getActivityType(),OrderActivityType.GIVEAWAY.value()) && !orderItemIds.contains(orderItem.getActivityId()));
            orderItems.removeIf(orderItem -> Objects.equals(orderItem.getActivityType(),OrderActivityType.COMBO_PROD.value()));
        }
        // 有多个商品的订单，有退款记录的，并且有退款成功的单个商品，
        // 其余的商品都是发货完成的订单，改成发货状态
        // 在退款处理的时候将状态改为0即可
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getStatus() != 0) {
                isDelivery = false;
                break;
            }
        }
        List<DeliveryOrder> deliveryOrders = listDetailDelivery(orderNumber);
        if (deliveryOrders.size() >= 10 && !isDelivery) {
            throw new YamiShopBindException("yami.order.deliver.size.exist");
        }
        return isDelivery;
    }
}
