/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.api.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.yami.shop.bean.app.dto.*;
import com.yami.shop.bean.enums.*;
import com.yami.shop.bean.event.SubmitOrderEvent;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.vo.VirtualRemarkVO;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.Arith;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.common.util.Json;
import com.yami.shop.dao.OrderSettlementMapper;
import com.yami.shop.dao.SkuMapper;
import com.yami.shop.service.*;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 确认订单信息时的默认操作
 *
 * @author LGH
 */
@Component("defaultSubmitOrderListener")
@AllArgsConstructor
public class SubmitOrderListener {

    private final ProductService productService;

    private final SkuService skuService;

    private final OrderSettlementMapper orderSettlementMapper;

    private final OrderItemService orderItemService;

    private final BasketService basketService;

    private final UserAddrOrderService userAddrOrderService;

    private final SkuMapper skuMapper;

    /**
     * 计算订单金额
     */
    @EventListener(SubmitOrderEvent.class)
    public void defaultSubmitOrderListener(SubmitOrderEvent event) {
        ShopCartOrderMergerDto mergerOrder = event.getMergerOrder();
        String userId = mergerOrder.getUserId();
        // 订单商品参数
        List<ShopCartOrderDto> shopCartOrders = mergerOrder.getShopCartOrders();

        List<Long> basketIds = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(shopCartOrders)) {
            // 每个店铺生成一个订单
            for (ShopCartOrderDto shopCartOrderDto : shopCartOrders) {
                shopCartOrderDto.setPlatformFreeFreightAmount(null == shopCartOrderDto.getPlatformFreeFreightAmount() ? 0.0 : shopCartOrderDto.getPlatformFreeFreightAmount());
                buildOrder(event, mergerOrder, basketIds, shopCartOrderDto);
            }
        }

        Date now = new Date();
        // 订单地址id
        Long addrId = null;
        //收件人姓名
        String receiverName = null;
        //收件人手机号码
        String receiverMobile = null;
        long count = event.getOrders().stream().filter(order -> Objects.isNull(order.getOrderMold()) || order.getOrderMold() == 0).count();
        // 不为虚拟商品的地址信息
        if (count > 0 ){
            // 把订单地址保存到数据库
            UserAddrOrder userAddrOrder = BeanUtil.map(mergerOrder.getUserAddr(), UserAddrOrder.class);
            if (userAddrOrder == null) {
                // 请填写收货地址
                throw new YamiShopBindException("yami.delivery.address");
            }
            userAddrOrder.setCreateTime(now);
            userAddrOrder.setUserId(userId);
            userAddrOrderService.save(userAddrOrder);
            addrId = userAddrOrder.getAddrOrderId();
            receiverName = userAddrOrder.getReceiver();
            receiverMobile = userAddrOrder.getMobile();
        }

        //循环订单，插入地址信息
        for (com.yami.shop.bean.model.Order order:event.getOrders()){
            if (order.getOrderMold() == 0) {
                order.setAddrOrderId(addrId);
                order.setReceiverName(receiverName);
                order.setReceiverMobile(receiverMobile);
            }
        }

        // 删除购物车的商品信息
        if (!basketIds.isEmpty()) {
            basketService.deleteShopCartItemsByBasketIds(userId, basketIds);
        }
    }

    // 构建单店铺订单并写入订单相关信息
    private void buildOrder(SubmitOrderEvent event, ShopCartOrderMergerDto mergerOrder, List<Long> basketIds, ShopCartOrderDto shopCartOrderDto) {
        Map<Long, Integer> dvyTypeMap = mergerOrder.getDvyTypes().stream().collect(Collectors.toMap(DvyTypeDTO::getShopId, DvyTypeDTO::getDvyType));
        Date now = new Date();
        String userId = mergerOrder.getUserId();
        String orderNumber = shopCartOrderDto.getOrderNumber();
        shopCartOrderDto.setOrderNumber(orderNumber);
        Long shopId = shopCartOrderDto.getShopId();
        // 订单商品名称
        StringBuilder orderProdName = new StringBuilder(100);

        List<OrderItem> orderItems = new ArrayList<>();
        // 构建订单项并获取预售时间
        buildOrderItem(basketIds, shopCartOrderDto, orderNumber, shopId,
                orderProdName, orderItems,userId);
        // 下单直接扣减sku库存
        deductSkuStockByOrderItems(orderItems);

        //中英文
        orderProdName = new StringBuilder(orderProdName.subSequence(0, Math.min(orderProdName.length() - 1, 100)));

        if (orderProdName.lastIndexOf(StrUtil.COMMA) == orderProdName.length() - 1) {
            orderProdName.deleteCharAt(orderProdName.length() - 1);
        }

        // 订单信息
        com.yami.shop.bean.model.Order order = new com.yami.shop.bean.model.Order();

        order.setShopId(shopId);
        order.setOrderNumber(orderNumber);
        order.setProductNums(shopCartOrderDto.getTotalCount());
        // 订单商品名称
        order.setProdName(orderProdName.toString());
        // 用户id
        order.setUserId(userId);
        // 商品总额
        order.setTotal(shopCartOrderDto.getTotal());
        // 实际总额
        order.setActualTotal(shopCartOrderDto.getActualTotal());
        order.setStatus(OrderStatus.UNPAY.value());
        order.setUpdateTime(now);
        order.setCreateTime(now);
        order.setIsPayed(0);
        order.setDeleteStatus(0);
        order.setReduceAmount(shopCartOrderDto.getShopReduce());
        order.setFreightAmount(shopCartOrderDto.getTransFee());
        order.setRemarks(shopCartOrderDto.getRemarks());
        order.setOrderType(mergerOrder.getOrderType().value());
        order.setOrderItems(orderItems);
        order.setScore(shopCartOrderDto.getUseScore());
        order.setDvyType(dvyTypeMap.get(shopId));
        order.setDvyId(mergerOrder.getStationId());
        order.setPlatformFreeFreightAmount(shopCartOrderDto.getPlatformFreeFreightAmount());
        order.setFreeTransfee(Objects.nonNull(shopCartOrderDto.getFreeTransFee()) ? -shopCartOrderDto.getFreeTransFee() : 0.0);
        order.setChangeAmountVersion(0);
        order.setDiscountAmount(shopCartOrderDto.getDiscountReduce());
        order.setShopCouponAmount(shopCartOrderDto.getCouponReduce());
//        order.setPlatformCouponAmount(shopCartOrderDto.getPlatformCouponReduce());
        order.setMemberAmount(shopCartOrderDto.getLevelReduce());
        order.setScoreAmount(shopCartOrderDto.getScoreReduce());
        order.setIsSettled(0);
        order.setShopComboAmount(shopCartOrderDto.getShopComboAmount());
        order.setOrderMold(mergerOrder.getMold());
        order.setAddrOrderId(mergerOrder.getUserAddr().getAddrId());
        event.getOrders().add(order);
        // 插入订单结算表
        insertOrderSettlement(mergerOrder, shopCartOrderDto, now, userId, orderNumber, order);
        // 插入订单项
        orderItemService.insertBatchOrderItem(orderItems);
    }

    private void insertOrderSettlement(ShopCartOrderMergerDto mergerOrder, ShopCartOrderDto shopCartOrderDto, Date now, String userId, String orderNumber, com.yami.shop.bean.model.Order order) {
        OrderSettlement orderSettlement = new OrderSettlement();
        orderSettlement.setUserId(userId);
        orderSettlement.setCreateTime(now);
        orderSettlement.setOrderNumber(orderNumber);
        orderSettlement.setPayAmount(order.getActualTotal());
        orderSettlement.setPayStatus(0);
        orderSettlement.setVersion(0);
        orderSettlement.setPayScore(0L);
        //如果用使用积分，结算表将积分价格插入
        if(mergerOrder.getIsScorePay() != null && mergerOrder.getIsScorePay() == 1){
            orderSettlement.setPayScore(shopCartOrderDto.getUseScore());
        }
        orderSettlementMapper.insert(orderSettlement);
    }


    private void buildOrderItem(List<Long> basketIds, ShopCartOrderDto shopCartOrderDto, String orderNumber, Long shopId, StringBuilder orderProdName, List<OrderItem> orderItems, String userId) {
        Date now = new Date();

        List<ShopCartItemDiscountDto> shopCartItemDiscounts = shopCartOrderDto.getShopCartItemDiscounts();
        for (ShopCartItemDiscountDto shopCartItemDiscount : shopCartItemDiscounts) {
            List<ShopCartItemDto> shopCartItems = shopCartItemDiscount.getShopCartItems();
            for (ShopCartItemDto shopCartItem : shopCartItems) {
                OrderItem orderItem = loadOrderItem(basketIds, shopCartOrderDto, orderNumber, shopId, orderProdName, now, userId, shopCartItem);
                orderItems.add(orderItem);
            }
        }
    }

    private OrderItem loadOrderItem(List<Long> basketIds, ShopCartOrderDto shopCartOrderDto, String orderNumber, Long shopId, StringBuilder orderProdName, Date now, String userId, ShopCartItemDto shopCartItem) {
        OrderItem orderItem = new OrderItem();
        Sku sku = skuService.getSkuListBySkuId(shopCartItem.getSkuId());
        Product product = productService.getProductByProdId(shopCartItem.getProdId());
        // 如果是虚拟商品且需要核销，放进去
        checkVirtualOrder(shopCartOrderDto, orderItem, product);
        orderItem.setShopId(shopId);
        orderItem.setCategoryId(product.getCategoryId());
        orderItem.setOrderNumber(orderNumber);
        // sku信息
        orderItem.setProdId(sku.getProdId());
        orderItem.setSkuId(sku.getSkuId());
        orderItem.setSkuName(sku.getSkuName());
        orderItem.setProperties(sku.getProperties());
        orderItem.setProdName(product.getProdName());
        orderItem.setPreSellTime(Objects.equals(product.getPreSellStatus(), 1) ? product.getPreSellTime() : null);
        orderItem.setPic(StrUtil.isBlank(sku.getPic()) ? product.getPic() : sku.getPic());
        orderItem.setProdCount(shopCartItem.getProdCount());
        orderItem.setPrice(shopCartItem.getPrice());
        orderItem.setUserId(userId);
        orderItem.setProductTotalAmount(shopCartItem.getProductTotalAmount());
        orderItem.setRecTime(now);
        orderItem.setCommSts(0);
        orderItem.setBasketDate(shopCartItem.getBasketDate());
        orderItem.setStockPointId(shopCartItem.getStockPointId());
        orderItem.setStockPointType(shopCartItem.getStockPointType());
//        // 平台的补贴优惠金额
//        orderItem.setPlatformShareReduce(shopCartItem.getPlatformShareReduce());
        // 实际订单项支付金额
        orderItem.setActualTotal(shopCartItem.getActualTotal());
        // 分摊优惠金额
        orderItem.setShareReduce(shopCartItem.getShareReduce());
        orderProdName.append(orderItem.getProdName()).append(StrUtil.COMMA);
        //推广员卡号
        orderItem.setDistributionCardNo(shopCartItem.getDistributionCardNo());
        orderItem.setDiscountAmount(shopCartItem.getDiscountAmount());
        orderItem.setShopCouponAmount(shopCartItem.getShopCouponAmount());
        orderItem.setMemberAmount(shopCartItem.getLevelReduce());
        orderItem.setScoreAmount(shopCartItem.getScorePayReduce());
        orderItem.setComboAmount(shopCartItem.getComboAmount());
        //使用积分价格
        orderItem.setUseScore(shopCartItem.getScorePrice());
        orderItem.setChangeAmountVersion(0);
        orderItem.setMold(Objects.isNull(product.getMold()) ? 0 : product.getMold());
        orderItem.setActivityId(shopCartItem.getActivityId());
        orderItem.setActivityType(shopCartItem.getActivityType());
        if (shopCartItem.getBasketId() != null && !Objects.equals(shopCartItem.getBasketId(), 0L) && !Objects.equals(shopCartItem.getBasketId(), -1L)) {
            basketIds.add(shopCartItem.getBasketId());
        }
        return orderItem;
    }

    /**
     * 按订单项扣减sku库存
     * @param orderItems 订单项列表
     */
    private void deductSkuStockByOrderItems(List<OrderItem> orderItems) {
        // 订单项为空直接返回
        if (CollectionUtil.isEmpty(orderItems)) {
            return;
        }
        Map<Long, Integer> skuCountMap = new HashMap<>();
        // 汇总各sku的扣减数量
        for (OrderItem orderItem : orderItems) {
            // skuId为空或数量为空不参与扣减
            if (Objects.isNull(orderItem.getSkuId()) || Objects.isNull(orderItem.getProdCount())) {
                continue;
            }
            Integer count = skuCountMap.get(orderItem.getSkuId());
            // 累加sku扣减数量
            if (Objects.isNull(count)) {
                skuCountMap.put(orderItem.getSkuId(), orderItem.getProdCount());
            } else {
                skuCountMap.put(orderItem.getSkuId(), count + orderItem.getProdCount());
            }
        }
        // 进行sku库存扣减
        skuService.deductSkuStock(skuCountMap);
    }

    private List<OrderItem> giveawayOrderItems(ShopCartItemDto shopCartItem, String orderNumber, Long shopId, String userId, Date now) {
        if (CollUtil.isEmpty(shopCartItem.getGiveawayShopCartItemList())) {
            return Collections.emptyList();
        }
        List<OrderItem> orderItemList = new ArrayList<>(shopCartItem.getGiveawayShopCartItemList().size());
        for (ShopCartItemDto shopCartItemVO : shopCartItem.getGiveawayShopCartItemList()) {
            OrderItem orderItem = BeanUtil.map(shopCartItemVO, OrderItem.class);
            orderItem.setShopId(shopId);
            orderItem.setOrderNumber(orderNumber);
            // sku信息
            orderItem.setRecTime(now);
            orderItem.setCommSts(0);
            //推广员卡号
            orderItem.setMemberAmount(shopCartItem.getLevelReduce());
            orderItem.setScoreAmount(shopCartItem.getScorePayReduce());
            orderItem.setComboAmount(Constant.ZERO_DOUBLE);
            orderItem.setActivityType(OrderActivityType.GIVEAWAY.value());
            orderItem.setGiveawayAmount(Arith.mul(shopCartItemVO.getGiveawayRefundPrice(), orderItem.getProdCount()));
            orderItem.setUserId(userId);
            setAmount(orderItem);
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    private void checkVirtualOrder(ShopCartOrderDto shopCartOrderDto, OrderItem orderItem, Product product){
        shopCartOrderDto.setWriteOffNum(product.getWriteOffNum());
        shopCartOrderDto.setIsRefund(product.getIsRefund());
        if(Objects.equals(product.getMold(), 1) && !Objects.equals(product.getWriteOffNum(),0)){
            Date startTime = new Date();
            Date endTime;
            // 判断有效期
            switch (product.getWriteOffTime()){
                case -1:
                    endTime = null;
                    break;
                case 0:
                    startTime = product.getWriteOffStart();
                    endTime = product.getWriteOffEnd();
                    break;
                case 1:
                    endTime = DateUtil.endOfDay(startTime);
                    break;
                default:
                    endTime = DateUtil.offsetDay(startTime,product.getWriteOffTime());
                    break;
            }
            shopCartOrderDto.setWriteOffStart(startTime);
            shopCartOrderDto.setWriteOffEnd(endTime);
            if (Objects.equals(product.getWriteOffNum(), -1)) {
                shopCartOrderDto.setWriteOffMultipleCount(product.getWriteOffMultipleCount());
            }
//            orderItem.setStatus(0);
        }
    }
    private static void setAmount(OrderItem orderItem) {
        // 实际订单项支付金额
        orderItem.setActualTotal(Constant.ZERO_DOUBLE);
        // 分摊优惠金额
        orderItem.setShareReduce(Constant.ZERO_DOUBLE);
        orderItem.setMemberAmount(Constant.ZERO_DOUBLE);
        orderItem.setScoreAmount(Constant.ZERO_DOUBLE);
        orderItem.setDistributionAmount(Constant.ZERO_DOUBLE);
        orderItem.setDistributionParentAmount(Constant.ZERO_DOUBLE);
        orderItem.setShopCouponAmount(Constant.ZERO_DOUBLE);
        orderItem.setDiscountAmount(Constant.ZERO_DOUBLE);
        orderItem.setPlatformFreeFreightAmount(Constant.ZERO_DOUBLE);
        //使用积分价格
        orderItem.setUseScore(Constant.ZERO_LONG);
        // 改价之后才会商家免运费，注意：要是改变了规则，这里也要改
        orderItem.setFreeFreightAmount(Constant.ZERO_DOUBLE);
        // 获取积分，确认收货之后才会获取积分
        orderItem.setGainScore(Constant.ZERO_LONG);
        // 生成订单之后才会进行订单改价
        orderItem.setShopChangeFreeAmount(Constant.ZERO_DOUBLE);
        orderItem.setChangeAmountVersion(Constant.ZERO);
        orderItem.setCommSts(Constant.ZERO);
    }
}
