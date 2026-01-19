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

import com.yami.shop.bean.app.dto.ShopCartItemDto;
import com.yami.shop.bean.model.UserAddr;
import com.yami.shop.bean.vo.UserDeliveryInfoVO;

import java.util.List;

/**
 * @author Lanhai
 */
public interface TransportManagerService {

    /**
     * 如果运费模板相同，则把运费模板相同的商品合成一个商品（合成：重量，体积，件数），然后在计算运费
     *
     * @param productItems 商品明细
     * @param userAddr     用户地址
     * @param userDeliveryInfoVO 运费
     * @return 运费金额
     */
    double calculateTransFee(List<ShopCartItemDto> productItems, UserAddr userAddr, UserDeliveryInfoVO userDeliveryInfoVO);
}
