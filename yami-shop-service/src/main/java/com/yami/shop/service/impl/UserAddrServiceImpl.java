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
import com.yami.shop.bean.model.UserAddr;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.dao.UserAddrMapper;
import com.yami.shop.service.UserAddrService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yami
 */
@Service
@AllArgsConstructor
public class UserAddrServiceImpl extends ServiceImpl<UserAddrMapper, UserAddr> implements UserAddrService {

    private final UserAddrMapper userAddrMapper;

    @Override
    public UserAddr getDefaultUserAddr(String userId) {
        return userAddrMapper.getDefaultUserAddr(userId);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void updateDefaultUserAddr(Long addrId, String userId) {
        userAddrMapper.removeDefaultUserAddr(userId);
        int setCount = userAddrMapper.setDefaultUserAddr(addrId,userId);
        if (setCount == 0) {
            // 无法修改用户默认地址，请稍后再试
            throw new YamiShopBindException("yami.user.addr.update.error");
        }
    }

    @Override
    @CacheEvict(cacheNames = "UserAddrDto", key = "#userId+':'+#addrId")
    public void removeUserAddrByUserId(Long addrId, String userId) {

    }

    @Override
    @Cacheable(cacheNames = "UserAddrDto", key = "#userId+':'+#addrId")
    public UserAddr getUserAddrByUserId(Long addrId, String userId) {
        if (addrId == 0) {
            return userAddrMapper.getDefaultUserAddr(userId);
        }
        return userAddrMapper.getUserAddrByUserIdAndAddrId(userId, addrId);
    }


}
