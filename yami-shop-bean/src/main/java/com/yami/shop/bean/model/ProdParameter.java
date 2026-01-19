/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品参数表
 *
 * @author Citrus
 * @date 2021-11-01 16:50:52
 */
@Data
@TableName("tz_prod_parameter")
public class ProdParameter implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    @Schema(description = "商品参数id" )
    private Long prodParameterId;

    @Schema(description = "商品id" )
    private Long prodId;

    @TableField(exist = false)
    @Schema(description = "商品参数名" )
    private String parameterKey;

    @TableField(exist = false)
    @Schema(description = "商品参数值" )
    private String parameterValue;

    @TableField(exist = false)
    private List<ProdParameterLang> prodParameterLangList;
}
