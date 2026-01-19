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

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.app.dto.MyOrderDto;
import com.yami.shop.bean.app.dto.MyOrderItemDto;
import com.yami.shop.bean.enums.*;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.model.Product;
import com.yami.shop.bean.model.ShopDetail;
import com.yami.shop.bean.vo.DeliveryOrderVO;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.PageAdapter;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.dao.OrderMapper;
import com.yami.shop.dao.ShopDetailMapper;
import com.yami.shop.service.DeliveryService;
import com.yami.shop.service.MyOrderService;
import com.yami.shop.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lgh on 2018/09/15.
 */
@Service
@AllArgsConstructor
public class MyOrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements MyOrderService {

    private final OrderMapper orderMapper;
    private final ShopDetailMapper shopDetailMapper;
    private final ProductService productService;
    private final DeliveryService deliveryService;

    @Override
    public IPage<MyOrderDto> pageMyOrderByUserIdAndStatus(Page<MyOrderDto> page, String userId, Integer status, String prodName) {
        List<MyOrderDto> myOrderDtos = orderMapper.listMyOrderByUserIdAndStatus(new PageAdapter(page), userId, status, prodName);
        // 移除开启过预售活动但现在已经关闭的预售商品的预售时间
        setInfo(status, myOrderDtos);
        page.setRecords(myOrderDtos);
        page.setTotal(orderMapper.countMyOrderByUserIdAndStatus(userId, status, prodName, null, null));
        return page;
    }

    private void setInfo(Integer status, List<MyOrderDto> myOrderDtos) {
        boolean checkDeliveryNum = Objects.equals(status, OrderStatus.PADYED.value()) || Objects.equals(status, 0);
        Map<String, Integer> deliveryCountMap = new HashMap<>(16);
        if (checkDeliveryNum) {
            List<String> orderNumberList = myOrderDtos.stream().map(MyOrderDto::getOrderNumber).collect(Collectors.toList());
            List<DeliveryOrderVO> countList = deliveryService.listDeliveryCountByOrderNumber(orderNumberList);
            deliveryCountMap = countList.stream().collect(Collectors.toMap(DeliveryOrderVO::getOrderNumber, DeliveryOrderVO::getDeliveryCount));
        }
        for (MyOrderDto myOrderDto : myOrderDtos) {
            if (StrUtil.isBlank(myOrderDto.getShopName())) {
                myOrderDto.setShopName(Constant.PLATFORM_SHOP_NAME);
                myOrderDto.setShopId(Constant.PLATFORM_SHOP_ID);
            }
            // 如果是秒杀商品，则返回秒杀id
            if (Objects.equals(myOrderDto.getOrderType(), OrderType.SECKILL.value())) {
                Product prodDb = productService.getProductByProdId(myOrderDto.getOrderItemDtos().get(0).getProdId());
                if (Objects.equals(prodDb.getProdType(), ProdType.PROD_TYPE_SECKILL.value())) {
                    myOrderDto.setSeckillId(prodDb.getActivityId());
                }
            }
            if (checkDeliveryNum) {
                if (deliveryCountMap.containsKey(myOrderDto.getOrderNumber())) {
                    myOrderDto.setDeliveryCount(deliveryCountMap.get(myOrderDto.getOrderNumber()));
                }
            }

            List<MyOrderItemDto> orderItemDtos = myOrderDto.getOrderItemDtos();

            Map<Long, List<MyOrderItemDto>> comboMap = myOrderDto.getOrderItemDtos().stream().filter(orderItemVO -> Objects.equals(orderItemVO.getActivityType(), OrderActivityType.COMBO_PROD.value()))
                    .collect(Collectors.groupingBy(MyOrderItemDto::getActivityId));
            // 处理一下赠品和组合商品子订单项
            Map<Long, List<MyOrderItemDto>> giveawawMap = myOrderDto.getOrderItemDtos().stream()
                    .filter(s -> Objects.equals(s.getActivityType(), OrderActivityType.GIVEAWAY.value()))
                    .collect(Collectors.groupingBy(MyOrderItemDto::getActivityId));
            Iterator<MyOrderItemDto> iterator = myOrderDto.getOrderItemDtos().iterator();
            int score = 0;
            Set<String> giveawayIdsSet = null;
            Long mainOrderItemId = null;
            for (MyOrderItemDto orderItemDto : orderItemDtos) {
                score += orderItemDto.getUseScore() == null ? 0 : orderItemDto.getUseScore();

                // 主订单项插入赠品订单项
                orderItemDto.setGiveawayList(giveawawMap.get(orderItemDto.getOrderItemId()));
                // 主订单项插入组合子订单项
                orderItemDto.setComboList(comboMap.get(orderItemDto.getOrderItemId()));

            }
            List<MyOrderItemDto> result = orderItemDtos.stream().filter(orderItemDto ->
                    !Objects.equals(orderItemDto.getActivityType(),OrderActivityType.GIVEAWAY.value()) && !Objects.equals(orderItemDto.getActivityType(),OrderActivityType.COMBO_PROD.value())
            ).collect(Collectors.toList());
            myOrderDto.setOrderItemDtos(result);
            myOrderDto.setUserScore(score);
        }
    }

    @Override
    public IPage<MyOrderDto> pageMyOrderByParams(Page<MyOrderDto> page, String userId, Integer status, String orderName, Integer orderTimeStatus,
                                                 Integer orderType, String orderNumber, Long shopId, Integer orderMold) {
        String preTime = "";

        List<MyOrderDto> myOrderDtoList = orderMapper.listMyOrderByParams(new PageAdapter(page), userId, status, orderName,
                orderTimeStatus, preTime, orderType, orderNumber, shopId, orderMold);
        setInfo(status, myOrderDtoList);
        page.setRecords(myOrderDtoList);
        page.setTotal(orderMapper.countMyOrderByParams(userId, status, orderName, orderTimeStatus, preTime, orderType, orderNumber, shopId, orderMold));
        return page;
    }

    @Override
    public IPage<MyOrderDto> myOrderComment(PageParam<MyOrderDto> page, String userId) {
        List<MyOrderDto> myOrderDtos = orderMapper.orderCommentByUserIdAndStatus(new PageAdapter(page), userId);
        for (MyOrderDto myOrderDto : myOrderDtos) {
            if (StrUtil.isBlank(myOrderDto.getShopName())) {
                myOrderDto.setShopName(Constant.PLATFORM_SHOP_NAME);
            }
            List<MyOrderItemDto> orderItemDtos = myOrderDto.getOrderItemDtos();
            Set<Long> refundOrderItemIdSet = new HashSet<>();
            Map<Long, MyOrderItemDto> myOrderItemMap = new LinkedHashMap<>();
            for (MyOrderItemDto orderItemDto : orderItemDtos) {
                // 过滤掉组合商品的订单项
                if (Objects.equals(orderItemDto.getActivityType(), OrderActivityType.COMBO_PROD.value())){
                    continue;
                }
                myOrderDto.setIsComm(orderItemDto.getCommSts());
                Long orderItemId = orderItemDto.getOrderItemId();
                myOrderItemMap.put(orderItemId, orderItemDto);

            }
            // 如果退款成功的订单项Id集合非空，则移除相应的订单项
            if (CollectionUtil.isNotEmpty(refundOrderItemIdSet)) {
                refundOrderItemIdSet.forEach(orderItemId -> myOrderItemMap.remove(orderItemId));
            }
            List<MyOrderItemDto> myOrderItemDtos = new ArrayList<>(myOrderItemMap.values());
            myOrderDto.setOrderItemDtos(new ArrayList<>(myOrderItemMap.values()));
            // 处理赠品和组合商品
            handleActivityOrder(myOrderDto);
        }
        page.setRecords(myOrderDtos);
        page.setTotal(orderMapper.countMyOrderByUserIdAndComm(userId));
        return page;
    }

    @Override
    public IPage<MyOrderItemDto> myOrderItemsComment(PageParam<MyOrderItemDto> page, String userId, Integer isComm) {
        List<MyOrderItemDto> myOrderDtos = orderMapper.orderItemCommentByUserIdAndStatus(new PageAdapter(page), userId, isComm);
        page.setRecords(myOrderDtos);
        page.setTotal(orderMapper.countOrderItemComment(userId, isComm));
        return page;
    }


    @Override
    public IPage<MyOrderDto> getOrderListByShopId(PageParam<MyOrderDto> page, Long shopId, String userId) {
        ShopDetail shopDetail = shopDetailMapper.selectOne(new LambdaQueryWrapper<ShopDetail>().eq(ShopDetail::getShopId, shopId).eq(ShopDetail::getShopStatus, 1));
        if (Objects.isNull(shopDetail)) {
            // 未找到店铺信息
            throw new YamiShopBindException("yami.store.not.exist");
        }
        List<MyOrderDto> orderList = orderMapper.getOrderListByStationId(new PageAdapter(page), null, userId, shopId);
        for (MyOrderDto myOrderDto : orderList) {
            handleActivityOrder(myOrderDto);
        }
        page.setRecords(orderList);
        page.setTotal(orderMapper.countOrderListByStationId(null, userId, shopId));
        return page;
    }

    /**
     * 获取当前时间前7天的时间点
     */
    private String getStatetime(int reduce) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        // reduce = -7 前7天
        c.add(Calendar.DATE, -reduce);
        Date monday = c.getTime();
        String preMonday = sdf.format(monday);
        return preMonday;
    }

    /**
     * 获取当前时间 前三个月的时间点
     */
    private String getReduceMonth(int reduce) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        // reduce = -7 前7天
        c.add(Calendar.MONTH, -reduce);
        Date month = c.getTime();
        String priMonth = sdf.format(month);
        return priMonth;
    }

    private static void handleActivityOrder(MyOrderDto myOrderDto) {
        Map<Long, List<MyOrderItemDto>> comboMap = myOrderDto.getOrderItemDtos().stream().filter(orderItemVO -> Objects.equals(orderItemVO.getActivityType(), OrderActivityType.COMBO_PROD.value()))
                .collect(Collectors.groupingBy(MyOrderItemDto::getActivityId));
        // 处理一下赠品和组合商品子订单项
        Map<Long, List<MyOrderItemDto>> giveawawMap = myOrderDto.getOrderItemDtos().stream()
                .filter(s -> Objects.equals(s.getActivityType(), OrderActivityType.GIVEAWAY.value()))
                .collect(Collectors.groupingBy(MyOrderItemDto::getActivityId));
        Iterator<MyOrderItemDto> iterator = myOrderDto.getOrderItemDtos().iterator();
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
    }

}
