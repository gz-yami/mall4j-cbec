/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.sys.common.listener;

import com.yami.shop.bean.event.GetSuperAdminEvent;
import com.yami.shop.common.config.Constant;
import com.yami.shop.sys.common.model.SysUser;
import com.yami.shop.sys.common.service.SysUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author gaozijie
 * @since 2023-12-14
 */
@Component
@AllArgsConstructor
public class GetShopSuperAdminListener {
    private final SysUserService sysUserService;

    @EventListener(GetSuperAdminEvent.class)
    public void getShopSuperAdmin(GetSuperAdminEvent event) {
        SysUser sysUser = sysUserService.getSysUserById(Constant.SUPER_ADMIN_ID);
        BeanUtils.copyProperties(sysUser, event);
    }
}
