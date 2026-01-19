/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.model.UserAddrOrder;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.common.util.PrincipalUtil;
import com.yami.shop.dao.OrderMapper;
import com.yami.shop.dao.UserAddrMapper;
import com.yami.shop.dao.UserAddrOrderMapper;
import com.yami.shop.service.UserAddrOrderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author yami
 */
@Service
@AllArgsConstructor
public class UserAddrOrderServiceImpl extends ServiceImpl<UserAddrOrderMapper, UserAddrOrder> implements UserAddrOrderService{

    private final OrderMapper orderMapper;
    private final UserAddrOrderMapper userAddrOrderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddrInfoById(UserAddrOrder userAddrOrder, Order orderDb) {
        UserAddrOrder addrOrder = getById(userAddrOrder.getAddrOrderId());
        if (!PrincipalUtil.isDbPhone(userAddrOrder.getMobile(), addrOrder.getMobile(), false)) {
            throw new YamiShopBindException("yami.user.err.phone");
        }
        if (userAddrOrder.getMobile().contains(Constant.ASTERISK)) {
            userAddrOrder.setMobile(addrOrder.getMobile());
        }
        userAddrOrderMapper.updateAddrInfoById(userAddrOrder);
        // 修改订单相关信息
        orderDb.setReceiverMobile(userAddrOrder.getMobile());
        orderDb.setReceiverName(userAddrOrder.getReceiver());
        orderMapper.updateById(orderDb);
    }
}
