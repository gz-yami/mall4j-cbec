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

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.OrderItem;
import com.yami.shop.delivery.common.dao.DeliveryOrderItemMapper;
import com.yami.shop.delivery.common.model.DeliveryOrderItem;
import com.yami.shop.delivery.common.service.DeliveryOrderItemService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 *
 * @author lhd
 * @date 2020-04-22 15:46:05
 */
@Service
@AllArgsConstructor
public class DeliveryOrderItemServiceImpl extends ServiceImpl<DeliveryOrderItemMapper, DeliveryOrderItem> implements DeliveryOrderItemService {

    private final DeliveryOrderItemMapper deliveryOrderItemMapper;

    @Override
    public List<DeliveryOrderItem> listItemNums(List<OrderItem> orderItems) {
        return deliveryOrderItemMapper.listItemNums(orderItems);
    }
}
