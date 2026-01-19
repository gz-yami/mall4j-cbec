/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.security.common.permission;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.yami.shop.bean.event.GetPermissionEvent;
import com.yami.shop.bean.event.PermissionErrorHandleEvent;
import com.yami.shop.security.common.util.AuthUserContext;
import com.yami.shop.security.common.util.PmsContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * 接口权限判断工具
 * @author FrozenWatermelon
 */
@Slf4j
@Component("pms")
@AllArgsConstructor
public class PermissionService {

    private final ApplicationContext applicationContext;

    /**
     * 判断接口是否有xxx:xxx权限
     *
     * @param permission 权限
     * @return {boolean}
     */
    public boolean hasPermission(String permission) {
        if (StrUtil.isBlank(permission)) {
            return false;
        }

        // 从上下文获取权限
        Set<String> perms = PmsContext.get();
        // 从登录的用户获取权限
        if (CollUtil.isEmpty(perms)) {
            GetPermissionEvent getPermissionEvent = new GetPermissionEvent();
            applicationContext.publishEvent(getPermissionEvent);
            perms = getPermissionEvent.getPerms();
        }


        // 框架处理会抛出异常在前端提示：服务器出了小差，请稍后重试
        // 所以没有权限，就自行处理并抛出异常，且告诉用户缺失的菜单权限是哪些，同时避免服务器异常的提示
        boolean hasPermission = perms
                .stream()
                .filter(StringUtils::hasText)
                .anyMatch(x -> PatternMatchUtils.simpleMatch(permission, x));

        if(!hasPermission) {
            applicationContext.publishEvent(new PermissionErrorHandleEvent(permission, AuthUserContext.getShopId()));
        }
        return hasPermission;
    }

}
