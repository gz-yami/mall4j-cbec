/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.security.common.config;

import lombok.AllArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger文档，只有在测试环境才会使用
 * @author LGH
 */
@Configuration("securitySwaggerConfiguration")
@AllArgsConstructor
public class SwaggerConfiguration {


    @Bean
    public GroupedOpenApi securityRestApi() {
        return GroupedOpenApi.builder()
                .group("登录接口")
                .packagesToScan("com.yami.shop.security")
                .pathsToMatch("/**")
                .build();
    }

}
