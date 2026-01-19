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
import com.yami.shop.bean.model.UserAddrOrder;
import org.apache.ibatis.annotations.Param;

/**
 * @author yami
 */
public interface UserAddrOrderMapper extends BaseMapper<UserAddrOrder> {

    /**
     * 修改地址信息
     *
     * @param userAddrOrder
     */
    void updateAddrInfoById(@Param("userAddrOrder") UserAddrOrder userAddrOrder);
}
