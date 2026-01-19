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
import com.yami.shop.bean.model.UserAddr;
import org.apache.ibatis.annotations.Param;


/**
 * @author yami
 */
public interface UserAddrMapper extends BaseMapper<UserAddr> {

    /**
     * 获取用户默认收货地址
     * @param userId 用户id
     * @return
     */
    UserAddr getDefaultUserAddr(@Param("userId") String userId);

    /**
     * 移除用户默认地址
     * @param userId 用户id
     */
    void removeDefaultUserAddr(@Param("userId") String userId);

    /**
     * 将地址设置为默认地址
     * @param addrId 地址id
     * @param userId 用户id
     * @return
     */
    int setDefaultUserAddr(@Param("addrId") Long addrId, @Param("userId") String userId);

    /**
     * 获取用户某个地址的信息
     * @param addrId 地址id
     * @param userId 用户id
     * @return
     */
    UserAddr getUserAddrByUserIdAndAddrId(@Param("userId") String userId, @Param("addrId") Long addrId);
}
