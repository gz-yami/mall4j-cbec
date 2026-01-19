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

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.app.dto.DeliveryDto;
import com.yami.shop.bean.model.Delivery;
import com.yami.shop.bean.vo.DeliveryOrderSimpleVO;
import com.yami.shop.bean.vo.DeliveryOrderVO;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 *
 * @author lgh on 2018/11/26.
 */
public interface DeliveryService extends IService<Delivery> {

    /**
     * 根据订单编号获取发货数量
     * @param orderNumbers
     * @return
     */
    List<DeliveryOrderVO> listDeliveryCountByOrderNumber(List<String> orderNumbers);

    /**
     * 获取物流公司信息
     * @param deliveryId 物流公司id
     * @return 物流公司
     */
    Delivery getInfoById(Long deliveryId);

}
