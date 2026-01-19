/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.common.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 雪花算法配置，提供全局唯一 ID 生成器。
 */
@Configuration
public class SnowflakeConfig {

    /**
     * 创建 Snowflake Bean，用于生成唯一编号。
     *
     * @return Snowflake 实例
     */
    @Bean
    public Snowflake snowflake() {
        return IdUtil.getSnowflake(1, 1);
    }
}
