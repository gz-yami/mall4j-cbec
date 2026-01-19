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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.app.dto.MyOrderDto;
import com.yami.shop.bean.app.dto.MyOrderItemDto;
import com.yami.shop.bean.model.Order;
import com.yami.shop.common.util.PageParam;

/**
 * 我的订单
 * @author lgh
 */
public interface MyOrderService extends IService<Order> {

    /**
     * 通过用户id和订单状态分页获取订单信息
     * @param page   分页参数
     * @param userId 用户id
     * @param status 订单状态
     * @param prodName 订单状态
     * @return
     */
    IPage<MyOrderDto> pageMyOrderByUserIdAndStatus(Page<MyOrderDto> page, String userId, Integer status, String prodName);

    /**
     * 通过用户id和订单状态分页获取订单信息
     * @param page 分页参数
     * @param userId 用户id
     * @param status 订单状态
     * @param orderName 订单编号/ 商品名称
     * @param orderTimeStatus
     * @param orderType
     * @param orderNumber
     * @param shopId
     * @param orderMold
     * @return
     */
    IPage<MyOrderDto> pageMyOrderByParams(Page<MyOrderDto> page, String userId, Integer status, String orderName, Integer orderTimeStatus,
                                          Integer orderType, String orderNumber, Long shopId,Integer orderMold);

    /**
     * 通过商家id获取自提订单列表
     * @param page 分页信息
     * @param shopId 自提点id
     * @param userId 用户id
     * @return 我的订单列表
     */
    IPage<MyOrderDto> getOrderListByShopId(PageParam<MyOrderDto> page,Long shopId, String userId);

    /**
     * 根据订单评价状态获取订单列表信息
     * @param page 分页参数
     * @param userId 用户id
     * @return 我的订单分页数据
     */
    IPage<MyOrderDto> myOrderComment(PageParam<MyOrderDto> page, String userId);

    /**
     * 根据订单评价状态获取订单项列表信息
     * @param page 分页参数
     * @param userId 用户id
     * @param commStatus 订单状态 0:待评价 1已评价
     * @return 我的订单项分页数据
     */
    IPage<MyOrderItemDto> myOrderItemsComment(PageParam<MyOrderItemDto> page, String userId, Integer commStatus);

}
