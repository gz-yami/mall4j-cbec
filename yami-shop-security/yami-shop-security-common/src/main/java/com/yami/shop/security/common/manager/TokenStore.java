/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.security.common.manager;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.yami.shop.bean.event.WuKongImEvent;
import com.yami.shop.common.constants.OauthCacheNames;
import com.yami.shop.common.enums.SysTypeEnum;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ResponseEnum;
import com.yami.shop.common.util.HttpContextUtils;
import com.yami.shop.common.util.RedisUtil;
import com.yami.shop.security.common.bo.LoginInfoBO;
import com.yami.shop.security.common.bo.UidInfoBO;
import com.yami.shop.security.common.enums.SocialType;
import com.yami.shop.security.common.util.AuthUserContext;
import com.yami.shop.security.common.vo.TokenInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * token管理 1. 登陆返回token 2. 刷新token 3. 清除用户过去token 4. 校验token
 *
 * @author FrozenWatermelon
 * @date 2020/7/2
 */
@Component
public class TokenStore {

    private static final Logger logger = LoggerFactory.getLogger(TokenStore.class);

    private final RedisTemplate<String, Object> redisTemplate;

    private final String tokenName;
    private final ApplicationEventPublisher eventPublisher;

    public TokenStore(RedisTemplate<String, Object> redisTemplate,@Value("${sa-token.token-name}") String tokenName, ApplicationEventPublisher eventPublisher) {
        this.redisTemplate = redisTemplate;
        this.tokenName = tokenName;
        this.eventPublisher = eventPublisher;
    }


    /**
     * 获取登录信息
     * @return 用户信息
     */
    public LoginInfoBO getLoginInfoByCache() {
        String accessToken = HttpContextUtils.getHttpServletRequest().getHeader(tokenName);
        // 获取登录的信息
        Object loginInfoCache = RedisUtil.get(OauthCacheNames.LOGIN_INFO + accessToken);
        if (Objects.isNull(loginInfoCache)) {
            return new LoginInfoBO();
        }
        return JSONObject.parseObject(loginInfoCache.toString(), LoginInfoBO.class);
    }


    /**
     * 根据uidInfo信息删除用户token
     * @param accessToken
     * @param uidInfo
     */
    public void deleteTokenByAccessToken(String accessToken, UidInfoBO uidInfo) {
        if (StrUtil.isBlank(accessToken)) {
            return;
        }
        StpUtil.logoutByTokenValue(accessToken);
    }


    /**
     * 根据uidInfo信息删除用户token
     * @param uidInfoBO
     */
    public void deleteTokenByUidInfo(UidInfoBO uidInfoBO) {
        if (Objects.isNull(uidInfoBO)) {
            return;
        }
        String uid = AuthUserContext.getUid(uidInfoBO);
        List<String> tokenList = StpUtil.getTokenValueListByLoginId(uid);
        if (CollUtil.isNotEmpty(tokenList)) {
            for (String token : tokenList) {
                // 删除用户缓存
                redisTemplate.delete(OauthCacheNames.LOGIN_INFO + token);
            }
        }
        // 移除token
        StpUtil.logout(uid);
    }

    /**
     * 根据uidList删除多个token
     * @param uidInfoList
     */
    public void deleteAllTokenByUidInfoList(List<UidInfoBO> uidInfoList) {
        if (CollUtil.isEmpty(uidInfoList)) {
            return;
        }
        Map<String, List<String>> uidMap = new HashMap<>(uidInfoList.size());
        for(UidInfoBO uidInfoBO : uidInfoList){
            String uid = AuthUserContext.getUid(uidInfoBO);
            // 移除token
            StpUtil.logout(uid);
        }
        if (CollUtil.isNotEmpty(uidMap)) {
            eventPublisher.publishEvent(new WuKongImEvent(uidMap));
        }
    }


    /**
     * 生成token，并返回token展示信息
     * @param uidInfo
     * @return
     */
    public TokenInfoVO storeAndGetVo(UidInfoBO uidInfo) {

        int expiresIn = getExpiresIn(uidInfo.getSysType().value());
        String uid = AuthUserContext.getUid(uidInfo);
        // 通过satoken生成的token
        StpUtil.login(uid, expiresIn);

        // 数据封装返回
        TokenInfoVO tokenInfoVO = new TokenInfoVO();
        tokenInfoVO.setAccessToken(StpUtil.getTokenValue());
        tokenInfoVO.setExpiresIn(expiresIn);
        return tokenInfoVO;
    }


    /**
     * 计算过期时间（单位:秒）
     * @param sysType
     * @return
     */
    public int getExpiresIn(int sysType) {
        // 3600秒(1小时)
        int expiresIn = 3600;
        // 普通用户token过期时间 1小时 * 24 * 30 = 30天
        if (Objects.equals(sysType, SysTypeEnum.ORDINARY.value())) {
            expiresIn = expiresIn * 24 * 30;
        }
        // 系统管理员的token过期时间 1小时 * 24 * 30 = 30天
        if (Objects.equals(sysType, SysTypeEnum.MULTISHOP.value()) || Objects.equals(sysType, SysTypeEnum.PLATFORM.value()) || Objects.equals(sysType, SysTypeEnum.STATION.value())) {
            expiresIn = expiresIn * 24 * 30;
        }
        return expiresIn;
    }



    /**
     * 获取登录用户的uid
     * @return
     */
    public String getUidByToken(String token) {
        Object uid;
        try {
            uid = StpUtil.getLoginIdByToken(token);
        } catch (NotLoginException e) {
            // 登录过期，请重新登录 -- 如果是sa-token未登录异常，需要替换成商城的未登录异常
            throw new YamiShopBindException(ResponseEnum.UNAUTHORIZED, "yami.security.login.expired");
        }
        return uid.toString();
    }


}
