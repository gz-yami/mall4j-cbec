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

import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 货币语言表
 * @author LGH
 */
@Data
@TableName("tz_currency_lang")
@Schema(description = "货币语言表")
public class CurrencyLang implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;
    @Schema(description = "语言")
    private Integer langId;
    @Schema(description = "语言名称")
    private String currencyName;
}
