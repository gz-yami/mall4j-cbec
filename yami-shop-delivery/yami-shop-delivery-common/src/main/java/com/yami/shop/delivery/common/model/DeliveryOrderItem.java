/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.delivery.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 *
 *
 * @author lhd
 * @date 2020-04-22 15:46:05
 */
@Data
@TableName("tz_delivery_order_item")
public class DeliveryOrderItem implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     * 关联id
     */
    @TableId
    private Long id;
    /**
     * 订单物流包裹id
     */
    private Long orderDeliveryId;
    /**
     * 订单项id
     */
    private Long orderItemId;
    /**
     * 商品数量
     */
    private Integer prodCount;

}
