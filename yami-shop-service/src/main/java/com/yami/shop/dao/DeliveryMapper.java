/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yami.shop.bean.model.Delivery;
import com.yami.shop.bean.vo.DeliveryOrderVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yami
 */
public interface DeliveryMapper extends BaseMapper<Delivery> {
    /**
     * 根据订单编号获取发货数量
     * @param orderNumbers
     * @return
     */
    List<DeliveryOrderVO> listDeliveryCountByOrderNumber(@Param("orderNumbers") List<String> orderNumbers);
}
