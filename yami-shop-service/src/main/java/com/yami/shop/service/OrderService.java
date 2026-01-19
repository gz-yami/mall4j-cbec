/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.service;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.app.dto.MyOrderDto;
import com.yami.shop.bean.app.dto.OrderCountData;
import com.yami.shop.bean.app.dto.OrderShopDto;
import com.yami.shop.bean.app.dto.ShopCartOrderMergerDto;
import com.yami.shop.bean.bo.OrderChangeShopWalletAmountBO;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.model.OrderItem;
import com.yami.shop.bean.param.OrderParam;
import com.yami.shop.bean.vo.DeliveryOrderSimpleVO;
import com.yami.shop.bean.vo.OrderAmountVO;
import com.yami.shop.bean.vo.OrderAndPayInfoVO;
import com.yami.shop.bean.vo.UnDeliveryOrderExcelVO;
import com.yami.shop.common.util.PageParam;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author lgh on 2018/09/15.
 */
public interface OrderService extends IService<Order> {

    /**
     * 根据订单号获取订单信息
     * @param orderNumber 订单号
     * @return 订单信息
     */
    Order getOrderByOrderNumber(String orderNumber);

    /**
     * 通过用户id和订单编号获取订单
     * @param orderNumber 订单编号
     * @param userId 用户id
     * @param valid 是否校验订单属于用户
     * @return 订单
     */
    Order getOrderByOrderNumberAndUserId(String orderNumber,String userId, boolean valid);

    /**
     * 根据店铺id和订单号获取订单
     * @param orderNumber 订单编号
     * @param shopId 店铺id
     * @param valid 是否校验订单属于用户
     * @return 订单
     */
    Order getOrderByOrderNumberAndShopId(String orderNumber,Long shopId, boolean valid);

    /**
     * 移除用户订单缓存
     * @param userId 用户id
     */
    void removeConfirmOrderCache(String userId);

    /**
     * 提交订单
     * @param mergerOrder 确认好的订单信息
     * @return 订单列表
     */
    List<Order> submit(ShopCartOrderMergerDto mergerOrder);

    /**
     * 发货
     * @param order 订单
     */
    void delivery(Order order);

    /**
     * 根据订单状态，获取非退款状态下的订单
     * @param orderStatus 订单状态
     * @param lessThanUpdateTime 最后更新时间
     * @return 非退款状态下的订单
     */
    List<Order> listUnRefundOrderAndOrderItems(Integer orderStatus, DateTime lessThanUpdateTime);


    /**
     * 确认收货
     * @param orders 订单列表
     */
    void receiptOrder(List<Order> orders);


    /**
     * 获取订单详情列表
     * @param page 分页信息
     * @param orderParam 查询参数
     * @return 订单列表
     */
    IPage<Order> pageOrdersDetailByOrderParam(Page<Order> page, OrderParam orderParam);

    /**
     * 批量删除订单
     * @param orders 订单
     */
    void deleteOrders(List<Order> orders);

    /**
     * 获取各个订单状态的订单数量
     * @param userId 用户id
     * @return 各个订单状态下的订单数量
     */
    OrderCountData getOrderCount(String userId);


    /**
     * 订单改价
     * @param order
     */
    void changeAmount(Order order);

    /**
     * 根据订单id列表获取订单列表
     * @param orderNumberList 订单列表
     * @return 订单列表
     */
    List<Order> getOrderPayInfoByOrderNumber(List<String> orderNumberList);

    /**
     * 通过订单编号、店铺id获取订单详情
     * @param orderNumber 订单编号
     * @param shopId 店铺id
     * @return 订单详情
     */
    Order getOrderDetailByOrderNumberAndShopId(String orderNumber, Long shopId);

    /**
     * 根据订单号获取订单及订单项
     *
     * @param orderNumber 订单编号
     * @return 订单
     */
    Order getOrderAndOrderItemByOrderNumber(String orderNumber);

    /**
     * 分页获取自提点订单列表
     * @param page 分页参数
     * @param status 订单状态
     * @param stationId 自提点id
     * @return 自提点订单列表
     */
    IPage<MyOrderDto> orderListByStatus(PageParam<MyOrderDto> page, Integer status, Long stationId);

    /**
     * 提货
     * @param orderNumberList 提货单列表
     * @param userId 用户id
     * @param stationId 自提点id
     * @return 是否提货成功
     */
    Boolean orderStationByOrderNumber(List<String> orderNumberList, String userId, Long stationId);

    /**
     * 分页获取订单信息
     * @param page 分页参数
     * @param userId 用户id
     * @return 订单信息
     */
    IPage<Order> pageByUserId(PageParam<Order> page, String userId);

    /**
     * 获取订单的创建状态，给秒杀提供查询是否已经创建订单成功的功能
     * @param orderNumber 订单id
     * @return
     */
    Integer countByOrderNumber(String orderNumber);

    /**
     * 根据订单号和商家id获取信息
     * @param orderNumber 订单编号
     * @param shopId 店铺id
     * @return 信息
     */
    UnDeliveryOrderExcelVO getOrderAndOrderItemsByOrderNumberAndShopId(String orderNumber, Long shopId);

    /**
     * 查询订单状态
     * @param orderNumbers 多个订单的订单id
     * @return 订单状态列表
     */
    List<Order> getOrdersStatus(List<String> orderNumbers);

    /**
     * 如果订单没有被取消的话，获取订单金额，否之会获取失败
     * @param userId
     * @param orderNumbers
     * @param payType 支付方式
     * @return
     */
    OrderAmountVO getOrdersAmountAndIfNoCancel(String userId, List<String> orderNumbers, Integer payType);

    /**
     * 更新成支付成功的状态
     *
     * @param orderNumbers
     */
    void updateByToPaySuccess(List<String> orderNumbers);

    /**
     * 根据用户id和店铺id分页获取订单信息
     * @param page
     * @param userId
     * @param shopId
     * @return
     */
    IPage<Order> pageByUserIdAndShopId(PageParam<Order> page, String userId, Long shopId);

    /**
     * 获取订单信息，订单支付成功时返回给前端用于显示的订单数据
     * @param orderNumber 订单编号
     * @param userId 用户id
     * @param stationId 自提点id
     * @return
     */
    OrderShopDto orderDetail(String orderNumber, String userId, Long stationId);

    /**
     * 查询订单和订单项数据列表
     * @param orderNumbers
     * @return
     */
    List<Order> listOrderAndOrderItemByOrderNumber(List<String> orderNumbers);

    /**
     * 获取待发货订单发货超时的列表
     *
     * @param lessThanUpdateTime 最后更新时间
     * @param dateTime
     * @return 非退款状态下的订单
     */
    List<OrderAndPayInfoVO> listDeliveryOutTimeOrder(DateTime lessThanUpdateTime, DateTime dateTime);


    /**
     * 推送物流信息
     * @param orders
     */
    void pushDeliveryTo17Tracking(DeliveryOrderSimpleVO orders);


    /**
     * 取消订单
     * @param orders 订单列表
     */
    void cancelOrders(List<Order> orders);
}
