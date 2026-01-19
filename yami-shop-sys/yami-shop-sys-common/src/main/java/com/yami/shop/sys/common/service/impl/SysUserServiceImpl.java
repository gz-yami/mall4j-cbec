/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.sys.common.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.PhoneUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.constants.SysCacheNames;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.PrincipalUtil;
import com.yami.shop.common.util.RedisUtil;
import com.yami.shop.sys.common.dao.SysUserMapper;
import com.yami.shop.sys.common.dao.SysUserRoleMapper;
import com.yami.shop.sys.common.model.SysUser;
import com.yami.shop.sys.common.service.SysUserService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 系统用户
 * @author lgh
 */
@Service("sysUserService")
@AllArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private SysUserRoleMapper sysUserRoleMapper;
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "listSysUserIds", key = "'0'")
    public void saveUserAndUserRole(SysUser user) {
        user.setCreateTime(new Date());
        sysUserMapper.insert(user);
        if(CollUtil.isEmpty(user.getRoleIdList())){
            return ;
        }
        //保存用户与角色关系
        sysUserRoleMapper.insertUserAndUserRole(user.getUserId(), user.getRoleIdList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(cacheNames = "getSysUserById", key = "#user.userId"),
            @CacheEvict(cacheNames = SysCacheNames.SYS_USER_PERMISSION, key = "#user.userId"),
            @CacheEvict(cacheNames = "listSysUserIds", key = "'0'")
    })
    public void updateUserAndUserRole(SysUser user) {
        // 超级管理员的用户名不做修改
        if (user.getUserId() == Constant.SUPER_ADMIN_ID) {
            user.setUsername(null);
        }
        SysUser sysUser = sysUserMapper.selectById(user.getUserId());
        if (!PrincipalUtil.isDbPhone(user.getMobile(), sysUser.getMobile(), true)) {
            throw new YamiShopBindException("yami.user.err.phone");
        }
        if (user.getMobile().contains(Constant.ASTERISK)) {
            user.setMobile(sysUser.getMobile());
        }
        // 更新用户
        sysUserMapper.updateById(user);

        //先删除用户与角色关系
        sysUserRoleMapper.deleteByUserId(user.getUserId());

        if(CollUtil.isEmpty(user.getRoleIdList())){
            return ;
        }
        //保存用户与角色关系
        sysUserRoleMapper.insertUserAndUserRole(user.getUserId(), user.getRoleIdList());
    }

    @Override
    public List<SysUser> listSysUser() {
        List<SysUser> sysUsers = this.list();
        if (CollectionUtils.isEmpty(sysUsers)) {
            return sysUsers;
        }
        for (SysUser sysUser : sysUsers) {
            // 手机号加密
            sysUser.setMobile(PhoneUtil.hideBetween(sysUser.getMobile()).toString());
        }
        return sysUsers;
    }

    @Override
    @CacheEvict(cacheNames = "getSysUserById", key = "#userId")
    public void updatePasswordByUserId(Long userId, String newPassword) {
        SysUser user = new SysUser();
        user.setPassword(newPassword);
        user.setUserId(userId);
        sysUserMapper.updateById(user);
    }

    @Override
    public SysUser getByUserName(String username) {
        return sysUserMapper.selectByUsername(username);
    }

    @Override
    @Cacheable(cacheNames = "getSysUserById", key = "#userId")
    public SysUser getSysUserById(Long userId) {
        return sysUserMapper.selectById(userId);
    }

    @Override
    @CacheEvict(cacheNames = "listSysUserIds", key = "'0'")
    public void removeByUserIds(List<Long> ids) {
        this.removeByIds(ids);
        List<String> keys = new ArrayList<>(ids.size());
        for (Long userId : ids) {
            keys.add("getSysUserById" + Constant.UNION + userId);
            //删除用户与角色关系
            sysUserRoleMapper.deleteByUserId(userId);
        }
        RedisUtil.del(keys);
    }

    @Override
    @Cacheable(cacheNames = "listSysUserIds", key = "'0'")
    public List<Long> listSysUserIds() {
        return sysUserMapper.listSysUserIds();
    }
}
