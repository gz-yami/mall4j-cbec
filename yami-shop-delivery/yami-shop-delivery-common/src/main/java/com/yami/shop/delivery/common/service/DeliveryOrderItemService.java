/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.delivery.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.model.OrderItem;
import com.yami.shop.delivery.common.model.DeliveryOrderItem;

import java.util.List;

/**
 *
 *
 * @author lhd
 * @date 2020-04-22 15:46:05
 */
public interface DeliveryOrderItemService extends IService<DeliveryOrderItem> {

    /**
     * 获取每个订单明细项对应的物流信息
     *
     * @param orderItems 订单明细项
     * @return 每个订单明细项对应的物流信息
     */
    List<DeliveryOrderItem> listItemNums(List<OrderItem> orderItems);
}
