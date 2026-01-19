/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.security.common.adapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;

/**
 * 使用security的防火墙功能，但不使用security的认证授权登录
 * @author FrozenWatermelon
 */
@Configuration
@EnableWebSecurity
public class LuckWebSecurityConfigurerAdapter  {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 禁用basic明文验证
                .httpBasic(AbstractHttpConfigurer::disable)
                // 需要跨域的配置 参考cors config
                .cors(Customizer.withDefaults())
                // 前后端分离架构不需要csrf保护
                .csrf(AbstractHttpConfigurer::disable)
                // 禁用默认登录页
                .formLogin(AbstractHttpConfigurer::disable)
                // 禁用默认登出页
                .logout(AbstractHttpConfigurer::disable)
                // 前后端分离是无状态的，不需要session了，直接禁用。
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 允许所有请求
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers("/**").permitAll())
                .build();
    }


}
