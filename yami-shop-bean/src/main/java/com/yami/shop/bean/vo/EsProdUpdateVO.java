/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Yami
 */
@Data
public class EsProdUpdateVO implements Serializable {

    private static final long serialVersionUID = 6457261945829470666L;

    @Schema(description = "产品ID" )
    private Long prodId;

    @Schema(description = "商品数量" )
    private Integer count;

    @Schema(description = "类型 0：减少 1：增加" )
    private Integer type;
}
