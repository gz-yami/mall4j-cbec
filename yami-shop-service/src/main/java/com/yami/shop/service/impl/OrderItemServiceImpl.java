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
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.app.dto.MyOrderItemDto;
import com.yami.shop.bean.enums.*;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.param.PayTopParam;
import com.yami.shop.bean.param.ProdAnalysisSurveyParam;
import com.yami.shop.bean.vo.OrderDetailVO;
import com.yami.shop.bean.vo.OrderItemDetailVO;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.common.util.RedisUtil;
import com.yami.shop.dao.OrderItemMapper;
import com.yami.shop.dao.OrderMapper;
import com.yami.shop.manager.impl.LangManager;
import com.yami.shop.service.OrderItemService;
import lombok.AllArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author lgh on 2018/09/15.
 */
@Service
@AllArgsConstructor
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    private final OrderItemMapper orderItemMapper;
    private final RedissonClient redissonClient;
    private final OrderMapper orderMapper;
    private final LangManager langManager;

    @Override
    public List<OrderItem> getOrderItemsByOrderNumber(String orderNumber) {
        List<OrderItem> orderItems = orderItemMapper.listByOrderNumber(orderNumber);
        return orderItems;
    }

    @Override
    public List<OrderItem> getUnGiveawayOrderItemsByOrderNumber(String orderNumber) {
        return orderItemMapper.getUnGiveawayOrderItemsByOrderNumber(orderNumber);
    }

    @Override
    public IPage<PayTopParam> getPayAmounts(PageParam<OrderItem> page, ProdAnalysisSurveyParam param) {
        IPage<PayTopParam> resPage = orderItemMapper.getPayAmounts(page, param);
        List<Long> prodIds = resPage.getRecords().stream().map(PayTopParam::getProdId).filter(prodId -> Objects.nonNull(prodId)).collect(Collectors.toList());
        Map<Long, ProdLang> prodLangMap = langManager.getProdLangMap(prodIds);
        for (PayTopParam payTopParam : resPage.getRecords()) {
            ProdLang prodLang = prodLangMap.get(payTopParam.getProdId());
            if (Objects.nonNull(prodLang)) {
                payTopParam.setProdName(prodLang.getProdName());
            }
        }
        return resPage;
    }

    @Override
    public Integer getPayPersonNumByParam(Long prodId, Long shopId, Date startTime, Date endTime) {
        return orderItemMapper.getPayPersonNumByParam(prodId, shopId, startTime, endTime);
    }

    @Override
    public void insertBatchOrderItem(List<OrderItem> orderItems) {
        saveBatch(orderItems);
        List<OrderItem> activityOrderItems = new ArrayList<>(Constant.INITIAL_CAPACITY);
        for (OrderItem orderItem : orderItems) {
            if(CollectionUtil.isNotEmpty(orderItem.getGiveawayList())){
                orderItem.getGiveawayList().forEach(item -> item.setActivityId(orderItem.getOrderItemId()));
                activityOrderItems.addAll(orderItem.getGiveawayList());
            }
            if(CollectionUtil.isNotEmpty(orderItem.getComboList())){
                orderItem.getComboList().forEach(item -> item.setActivityId(orderItem.getOrderItemId()));
                activityOrderItems.addAll(orderItem.getComboList());
            }
        }
        if(CollectionUtil.isNotEmpty(activityOrderItems)) {
            saveBatch(activityOrderItems);
        }
    }

    @Override
    public OrderItem getByOrderItemId(Long orderItemId) {
        return orderItemMapper.getByOrderItemId(orderItemId);
    }

    @Override
    public OrderItem getOrderItemAndRefundByOrderItemId(Long orderItemId) {
        return orderItemMapper.getInfoByOrderItemId(orderItemId);
    }

    @Override
    public OrderDetailVO listDetailByParam(String orderNumber, String refundSn, Integer reason, Long shopId) {
        Order order = orderMapper.getOrderByOrderNumber(orderNumber);
        boolean notShopOrder = Objects.isNull(order)
                || (!Objects.equals(shopId, Constant.PLATFORM_SHOP_ID) && !Objects.equals(order.getShopId(), shopId));
        if (notShopOrder) {
            throw new YamiShopBindException("yami.order.no.exist");
        }
        OrderDetailVO orderDetailVO = BeanUtil.map(order, OrderDetailVO.class);
        // 订单项信息
        List<OrderItemDetailVO> orderItems = orderItemMapper.listDetailByOrderNumber(orderNumber, order.getShopId());
        List<Long> categoryIds = orderItems.stream().map(OrderItemDetailVO::getCategoryId).filter(categoryId -> Objects.nonNull(categoryId)).collect(Collectors.toList());
        Map<Long, CategoryLang> categoryLangMap = langManager.getCategoryLangMap(categoryIds);
        List<OrderItemDetailVO> orderItemDetailList = new ArrayList<>();
        for (OrderItemDetailVO detailVO : orderItems) {
            CategoryLang categoryLang = categoryLangMap.get(detailVO.getCategoryId());
            if (Objects.nonNull(categoryLang)) {
                detailVO.setCategoryName(categoryLang.getCategoryName());
            }
            if (Objects.equals(order.getOrderType(), OrderType.SECKILL.value())) {
                detailVO.setSeckillAmount(detailVO.getMultishopReduce());
            } else if (Objects.equals(order.getOrderType(), OrderType.GROUP.value())) {
                detailVO.setGroupAmount(detailVO.getMultishopReduce());
            }
            if (Objects.equals(order.getShopId(), Constant.PLATFORM_SHOP_ID)) {
                detailVO.setShopName(Constant.PLATFORM_SHOP_NAME);
            }
            OrderItemDetailVO orderItemDetailVO = BeanUtil.map(detailVO, OrderItemDetailVO.class);
            orderItemDetailVO.setRefundAmount(0.00);
//            if (Objects.isNull(orderItemDetailVO.getPlatformShareReduce())) {
//                orderItemDetailVO.setPlatformShareReduce(0.00);
//            }
            orderItemDetailList.add(orderItemDetailVO);
        }
        orderDetailVO.setOrderItemDetailList(orderItemDetailList);
        orderDetailVO.setFreeFreightAmount(order.getFreeTransfee());
        orderDetailVO.setPlatformFreeFreightAmount(order.getPlatformFreeFreightAmount());
        orderDetailVO.setFreightAmount(order.getFreightAmount());
        return orderDetailVO;
    }

    @Override
    public Integer getOrderItemProdNum(Long orderItemId) {
        return orderItemMapper.getOrderItemProdNum(orderItemId);
    }

    @Override
    public OrderItem getByIdI18n(Long orderItemId) {
        return orderItemMapper.getByOrderItemId(orderItemId);
    }

    @Override
    public List<OrderItem> listAndPayTimeByOrderNumber(String orderNumber) {
        List<OrderItem> orderItems = orderItemMapper.listAndPayTimeByOrderNumber(orderNumber);
        Set<Long> refundOrderItemIdSet = new HashSet<>();

        handleActivityOrderItem(orderItems,null);
        return orderItems;
    }


    @Override
    public List<Long> getSoldNumRankByShopIdAndTime(String key,
                                                    Long shopId,
                                                    Integer dayNum,
                                                    Integer expireTime,
                                                    Integer esRenovationSpuSort,
                                                    Long primaryCategoryId) {
        Date time = null;
        if (dayNum != 0) {
            time = DateUtil.offsetDay(new DateTime(), -dayNum);
        }

        List<Long> spuIds = RedisUtil.getListRange(key, 0L, 10L);
        if (CollectionUtil.isNotEmpty(spuIds)) {
            return spuIds;
        }

        // 加锁，防止缓存击穿
        RLock rLock = redissonClient.getLock("redisson_lock:sold_num_rank:" + key);
        try {
            int lockWait = 30;
            if (rLock.tryLock(lockWait, lockWait, TimeUnit.SECONDS)) {
                spuIds = RedisUtil.getListRange(key, 0L, 10L);
                if (CollectionUtil.isEmpty(spuIds)) {
                    spuIds = orderItemMapper.getSoldNumRankByShopIdAndTime(shopId, time, esRenovationSpuSort, null, primaryCategoryId);
                    RedisUtil.setRightPushAll(key, spuIds, expireTime);
                    return spuIds;
                }
            } else {
                // 网络繁忙，请稍后再试
                throw new YamiShopBindException("yami.order.item.exception.networkBusy");
            }
        } catch (InterruptedException e) {
            log.error("InterruptedException:", e);
        } finally {
            try {
                if (rLock.isLocked()) {
                    rLock.unlock();
                }
            } catch (Exception e) {
                log.error("Exception:", e);
            }
        }
        return spuIds;
    }

    @Override
    public void handleActivityOrderItem(List<OrderItem> orderItems, List<MyOrderItemDto> myOrderItemDtos) {
        if(CollectionUtil.isNotEmpty(myOrderItemDtos)){
            // 处理一下赠品和组合商品子订单项
            Map<Long, List<MyOrderItemDto>> giveawawMap = myOrderItemDtos.stream()
                    .filter(s -> Objects.equals(s.getActivityType(), OrderActivityType.GIVEAWAY.value()))
                    .collect(Collectors.groupingBy(MyOrderItemDto::getActivityId));
            Map<Long, List<MyOrderItemDto>> comboMap = myOrderItemDtos.stream()
                    .filter(s -> Objects.equals(s.getActivityType(), OrderActivityType.COMBO_PROD.value()))
                    .collect(Collectors.groupingBy(MyOrderItemDto::getActivityId));

            Iterator<MyOrderItemDto> iterator = myOrderItemDtos.iterator();
            while (iterator.hasNext()) {
                MyOrderItemDto orderItem = iterator.next();
                if (Objects.equals(orderItem.getActivityType(), OrderActivityType.COMBO_PROD.value()) || Objects.equals(orderItem.getActivityType(), OrderActivityType.GIVEAWAY.value())) {
                    iterator.remove();
                    continue;
                }
                // 主订单项插入赠品订单项
                orderItem.setGiveawayList(giveawawMap.get(orderItem.getOrderItemId()));
                // 主订单项插入组合子订单项
                orderItem.setComboList(comboMap.get(orderItem.getOrderItemId()));
            }
            return;
        }
        // 处理一下赠品和组合商品子订单项
        Map<Long, List<OrderItem>> giveawawMap = orderItems.stream()
                .filter(s -> Objects.equals(s.getActivityType(), OrderActivityType.GIVEAWAY.value()))
                .collect(Collectors.groupingBy(OrderItem::getActivityId));
        Map<Long, List<OrderItem>> comboMap = orderItems.stream()
                .filter(s -> Objects.equals(s.getActivityType(), OrderActivityType.COMBO_PROD.value()))
                .collect(Collectors.groupingBy(OrderItem::getActivityId));

        Iterator<OrderItem> iterator = orderItems.iterator();
        while (iterator.hasNext()) {
            OrderItem orderItem = iterator.next();
            if (Objects.equals(orderItem.getActivityType(), OrderActivityType.COMBO_PROD.value()) || Objects.equals(orderItem.getActivityType(), OrderActivityType.GIVEAWAY.value())) {
                iterator.remove();
                continue;
            }
            // 主订单项插入赠品订单项
            orderItem.setGiveawayList(giveawawMap.get(orderItem.getOrderItemId()));
            // 主订单项插入组合子订单项
            orderItem.setComboList(comboMap.get(orderItem.getOrderItemId()));
        }
    }

    @Override
    public List<OrderItem> listUnCommOrderItem(Date endTime) {
        return orderItemMapper.listUnCommOrderItem(endTime);
    }

//    @Override
//    public List<OrderItem> getUnCloseRefundOrderItemByOrderNumber(String orderNumber) {
//        return orderItemMapper.getUnCloseRefundOrderItemByOrderNumber(orderNumber);
//    }

}
