/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.security.common.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.yami.shop.common.constants.OauthCacheNames;
import com.yami.shop.common.enums.SysTypeEnum;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.CacheManagerUtil;
import com.yami.shop.common.util.RedisUtil;
import com.yami.shop.security.common.bo.LoginInfoBO;
import com.yami.shop.security.common.bo.UidInfoBO;
import com.yami.shop.security.common.enums.SocialType;
import com.yami.shop.security.common.manager.TokenStore;
import com.yami.shop.security.common.util.AuthUserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author FrozenWatermelon
 * @date 2020/6/30
 */
@RestController
@Tag(name = "注销")
@AllArgsConstructor
public class LogoutController {

    private final TokenStore tokenStore;
    private final CacheManagerUtil cacheManagerUtil;


    @PostMapping("/logOut")
    @Operation(summary = "退出登陆" , description = "点击退出登陆，清除token，清除菜单缓存")
    public ServerResponseEntity<Void> logOut(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        if (StrUtil.isBlank(accessToken)) {
            return ServerResponseEntity.success();
        }
        Object uid;
        try {
            uid = StpUtil.getLoginIdByToken(accessToken);
            if (Objects.isNull(uid)) {
                ServerResponseEntity.success();
            }
        } catch (Exception e) {
            return ServerResponseEntity.success();
        }

        // 获取用户信息
        UidInfoBO uidInfo = AuthUserContext.getUidInfo(uid.toString());
        // 只有微信公众号和小程序的登录会进行登录并绑定的操作
        if (Objects.equals(uidInfo.getSysType(), SysTypeEnum.ORDINARY)) {
            LoginInfoBO loginInfoBO = tokenStore.getLoginInfoByCache();
            if (loginInfoBO != null && Objects.equals(loginInfoBO.getSocialType(), SocialType.MA.value()) || Objects.equals(loginInfoBO.getSocialType(),SocialType.MP.value())) {
                // 移除loginInfo的缓存
                RedisUtil.del(OauthCacheNames.LOGIN_INFO + accessToken);
            }
        }
        // 退出登录，清理权限的缓存
        if (Objects.equals(uidInfo.getSysType(), SysTypeEnum.MULTISHOP)) {
            cacheManagerUtil.evictCache("ShopPermissions", uidInfo.getUserId());
        }
        // 退出登录，清理权限的缓存
        if (Objects.equals(uidInfo.getSysType(), SysTypeEnum.PLATFORM)) {
            cacheManagerUtil.evictCache("SysPermissions", uidInfo.getUserId());
        }
        // 退出登录
        tokenStore.deleteTokenByAccessToken(accessToken, uidInfo);
        return ServerResponseEntity.success();
    }

}
