/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.security.api.util;

import com.yami.shop.common.enums.SysTypeEnum;
import com.yami.shop.common.util.HttpContextUtils;
import com.yami.shop.security.api.model.YamiUser;
import com.yami.shop.security.common.util.AuthUserContext;
import lombok.experimental.UtilityClass;

import java.util.Objects;

/**
 * @author LGH
 */
@UtilityClass
public class SecurityUtils {

    private static final String USER_REQUEST = "/p/";

    /**
     * 获取用户登录信息
     *
     * 用户端（请求一次）：通过token获取uid，拆分得到 userId和sysType
     * 门店端（请求两次）：一次通过token获取uid, 一次通过uid拆分的userId和sysType获取登录用户信息）
     *
     * 所以用户端的UserInfoInTokenBO中没有 enabled 和 bizUserId 的数据
     * 如果需要用到 enabled 和 bizUserId 请使用 UserSecurityManager.getUser() 方法
     * 设计思路：
     *     1.用户端除了支付的接口外,仅用到userId, userId可以在uid中拆解获得，没有必要再去redis中获取用户的数据
     *     2.由1可知，用户端每个接口能减少一次redis请求
     */
    public YamiUser getUser() {
        if (!HttpContextUtils.getHttpServletRequest().getRequestURI().startsWith(USER_REQUEST)) {
            // 用户相关的请求，应该以/p开头！！！
            throw new RuntimeException("yami.user.request.error");
        }
        YamiUser yamiUser = new YamiUser();
        yamiUser.setUserId(AuthUserContext.getUserId());
        // 门店端使用的数据
        if (Objects.equals(AuthUserContext.getSysType(), SysTypeEnum.STATION.value())) {
            yamiUser.setShopId(AuthUserContext.getShopId());
            yamiUser.setStationId(AuthUserContext.getStationId());
        }
        return yamiUser;
    }
}
