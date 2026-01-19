/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.delivery.api.config;


import lombok.AllArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 物流的swagger文档
 * @author LGH
 */
@Configuration("deliverySwaggerConfiguration")
@AllArgsConstructor
public class SwaggerConfiguration {

    @Bean
    public GroupedOpenApi deliveryRestApi() {
        return GroupedOpenApi.builder()
                .group("物流接口")
                .packagesToScan("com.yami.shop.delivery.api.controller")
                .pathsToMatch("/**")
                .build();
    }


}
