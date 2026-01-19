/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * @author lgh
 */
@SpringBootApplication
@ComponentScan(
        basePackages = {"com.yami.shop"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.yami\\.shop\\.search\\..*")
)
public class ApiApplication extends SpringBootServletInitializer{

    /**
     * 应用启动入口。
     *
     * @param args 启动参数
     */
	public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
	}

    /**
     * 外部容器部署时的配置入口。
     *
     * @param builder SpringApplicationBuilder
     * @return SpringApplicationBuilder
     */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(ApiApplication.class);
	}
}
