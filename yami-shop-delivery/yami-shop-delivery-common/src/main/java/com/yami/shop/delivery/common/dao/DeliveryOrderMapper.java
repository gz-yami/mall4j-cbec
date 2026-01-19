/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.delivery.common.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yami.shop.bean.vo.DeliveryOrderSimpleVO;
import com.yami.shop.delivery.common.model.DeliveryOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lhd
 * @date 2020-04-22 15:46:05
 */
public interface DeliveryOrderMapper extends BaseMapper<DeliveryOrder> {

    /**
     * 根据订单号获取物流信息
     *
     * @param orderNumber 订单号
     * @param orderDeliveryId 订单物流包裹id
     * @return 订单物流信息
     */
    List<DeliveryOrder> getAndDeliveryItemInfo(@Param("orderNumber") String orderNumber, @Param("orderDeliveryId") Long orderDeliveryId);

    /**
     * 根据订单号获取物流信息
     *
     * @param orderNumber 订单号
     * @return 订单物流信息
     */
    List<DeliveryOrder> listDetailDelivery(@Param("orderNumber") String orderNumber);

    /**
     * 获取未推送过的待发货或者交易成功的物流配送订单
     * @return
     */
    List<DeliveryOrderSimpleVO> listUnpushDelivery();

}
