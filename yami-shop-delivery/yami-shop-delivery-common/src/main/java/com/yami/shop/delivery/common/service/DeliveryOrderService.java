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
import com.yami.shop.bean.vo.DeliveryOrderSimpleVO;
import com.yami.shop.delivery.common.model.DeliveryOrder;
import com.yami.shop.delivery.common.param.DeliveryOrderItemParam;

import java.util.List;

/**
 *
 *
 * @author lhd
 * @date 2020-04-22 15:46:05
 */
public interface DeliveryOrderService extends IService<DeliveryOrder> {

    /**
     * 保存物流信息
     *
     * @param deliveryOrderParam 保存物流信息参数
     * @param shopId 店铺id
     */
    void saveDeliveriesInfo(DeliveryOrderItemParam deliveryOrderParam, Long shopId);

    /**
     * 更新物流信息
     *
     * @param deliveryOrders 更新物流信息参数
     */
    void updateOrderDeliveries(List<DeliveryOrder> deliveryOrders);

    /**
     * 获取物流信息
     *
     * @param orderNumber 订单号
     * @return 物流信息
     */
    List<DeliveryOrder> getAndDeliveryItemInfo(String orderNumber);

    /**
     * 获取物流信息
     *
     * @param orderNumber 订单号
     * @return 物流信息
     */
    List<DeliveryOrder> listDetailDelivery(String orderNumber);

    /**
     * 获取物流信息
     *
     * @param orderDeliveryId 订单物流包裹id
     * @return 物流信息
     */
    DeliveryOrder deliveryOrderItemInfo(Long orderDeliveryId);

    /**
     * 获取未推送过的待发货或者交易成功的物流配送订单
     * @return
     */
    List<DeliveryOrderSimpleVO> listUnpushDelivery();

}
