/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.common.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author FrozenWatermelon
 * @date 2020/11/16
 */
@Data
public class EsPageVO<T> {

    @Schema(description = "总页数" )
    private Integer pages;

    @Schema(description = "总条目数" )
    private Long total;

    @Schema(description = "结果集" )
    private List<T> records;
}
