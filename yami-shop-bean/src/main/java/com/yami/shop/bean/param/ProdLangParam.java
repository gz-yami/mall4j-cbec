/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 *
 *
 * @author lhd
 * @date 2020-08-26 15:39:09
 */
@Data
public class ProdLangParam implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     * 商品id
     */
    @Schema(description = "商品id" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long prodId;
    /**
     * 语言 0.中文 1.英文
     */
    @Schema(description = "语言 0.中文 1.英文" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer lang;
    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称长度应该小于{max}")
    @Schema(description = "商品名称" )
    private String prodName;
    /**
     * 简要描述,卖点等
     */
    @Size(max = 500, message = "商品卖点长度应该小于{max}")
    @Schema(description = "简要描述,卖点等" )
    private String brief;
    /**
     * 详细描述
     */
    @Schema(description = "详细描述" )
    private String content;

}
