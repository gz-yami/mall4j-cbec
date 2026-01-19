/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.security.common.filter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.handler.HttpHandler;
import com.yami.shop.common.response.ResponseEnum;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.Json;
import com.yami.shop.common.wrapper.ResponseWrapper;
import com.yami.shop.security.common.adapter.AuthConfigAdapter;
import com.yami.shop.security.common.bo.UidInfoBO;
import com.yami.shop.security.common.manager.TokenStore;
import com.yami.shop.security.common.util.AuthUserContext;
import com.yami.shop.security.common.util.PmsContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 授权过滤，只要实现AuthConfigAdapter接口，添加对应路径即可：
 *
 * @author FrozenWatermelon
 * @date 2020/7/11
 */
@Component
@RequiredArgsConstructor
public class AuthFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
    private final AuthConfigAdapter authConfigAdapter;
    private final HttpHandler httpHandler;
    private final TokenStore tokenStore;
    @Value("${sa-token.token-name}")
    private String tokenName;
    private final ObjectMapper objectMapper;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String requestUri = req.getRequestURI();

        List<String> excludePathPatterns = authConfigAdapter.excludePathPatterns();

        AntPathMatcher pathMatcher = new AntPathMatcher();
        // 如果匹配不需要授权的路径，就不需要校验是否需要授权
        if (CollectionUtil.isNotEmpty(excludePathPatterns)) {
            for (String excludePathPattern : excludePathPatterns) {
                if (pathMatcher.match(excludePathPattern, requestUri)) {
                    chain.doFilter(req, resp);
                    return;
                }
            }
        }

        String accessToken = req.getHeader(tokenName);
        // 获取签名方式的接口传参类型,如果是纯数组类型需要传这个请求头为list,不是不用传
        // 也许需要登录，不登陆也能用的uri
        // 比如优惠券接口，登录的时候可以判断是否已经领取过
        // 不能登录的时候会看所有的优惠券，等待领取的时候再登录
        boolean mayAuth = pathMatcher.match(AuthConfigAdapter.MAYBE_AUTH_URI, requestUri);

        String uid = null;
        try {
              // 如果有token，就要获取token
            if (StrUtil.isNotBlank(accessToken)) {
                // 校验登录，并从缓存中取出用户信息
                try {
                    // token访问
                    uid = tokenStore.getUidByToken(accessToken);
                } catch (Exception e) {
                    httpHandler.printServerResponseToWeb(ServerResponseEntity.fail(ResponseEnum.UNAUTHORIZED));
                    return;
                }
            } else if (!mayAuth) {
                // 返回前端未授权
                httpHandler.printServerResponseToWeb(ServerResponseEntity.fail(ResponseEnum.UNAUTHORIZED));
                return;
            }
            // 保存上下文
            AuthUserContext.set(uid);
            chain.doFilter(req, resp);
        } catch (Exception e) {
            if (e instanceof YamiShopBindException) {
                httpHandler.printServerResponseToWeb((YamiShopBindException) e);
                return;
            }
            throw e;
        } finally {
            AuthUserContext.clean();
            PmsContext.clean();
        }

    }

}
