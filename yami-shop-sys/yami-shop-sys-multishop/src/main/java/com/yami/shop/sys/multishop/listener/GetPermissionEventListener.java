/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.sys.multishop.listener;

import com.yami.shop.bean.event.GetPermissionEvent;
import com.yami.shop.security.common.util.AuthUserContext;
import com.yami.shop.sys.common.service.SysMenuService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author gaozijie
 * @since 2023-12-14
 */
@Component
@AllArgsConstructor
public class GetPermissionEventListener {
    private final SysMenuService sysMenuService;
    @EventListener(GetPermissionEvent.class)
    public void getShopSuperAdmin(GetPermissionEvent event) {
        Set<String> shopPermissions
                = sysMenuService.getUserPermissions(AuthUserContext.getSysUserId());
        event.setPerms(shopPermissions);
    }
}
