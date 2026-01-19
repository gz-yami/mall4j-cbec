/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 货币语言表
 * @author LGH
 */
@Data
@Schema(description = "货币语言")
public class CurrencyLangDTO {

    private Long id;
    @Schema(description = "语言")
    private Integer langId;
    @Schema(description = "语言名称")
    private String currencyName;
}
