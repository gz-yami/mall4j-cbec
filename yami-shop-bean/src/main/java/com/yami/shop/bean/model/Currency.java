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
import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author lhd
 */
@Data
@TableName("tz_currency")
@Schema(description = "货币汇率表")
public class Currency implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId
    private Long id;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "货币编码")
    private String currencyCode;
    @Schema(description = "货币符号")
    private String symbol;
    @Schema(description = "汇率值")
    private Double exchangeRate;
    @Schema(description = "启用状态 1.是 0.否")
    private Integer status;
    @Schema(description = "是否默认货币 1.是 0.否")
    private Integer defaultCurrency;
    @Schema(description = "消息模板多语言集合")
    @TableField(exist = false)
    private List<CurrencyLang> currencyLangs;
    @Schema(description = "货币名称")
    @TableField(exist = false)
    private String currencyName;
}
