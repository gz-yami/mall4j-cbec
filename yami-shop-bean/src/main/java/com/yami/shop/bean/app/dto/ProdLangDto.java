/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.app.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 * @author lhd
 * @date 2020-08-26 15:39:09
 */
@Data
public class ProdLangDto{

    /**
     * 商品id
     */
    @TableId(type = IdType.INPUT)
    @Schema(description = "商品id" )
    private Long prodId;
    /**
     * 语言 0.中文 1.英文
     */
    @Schema(description = "语言" )
    private Integer lang;
    /**
     * 商品名称
     */
    @Schema(description = "商品名称" )
    private String prodName;
    /**
     * 简要描述,卖点等
     */
    @Schema(description = "简要描述,卖点等" )
    private String brief;
    /**
     * 详细描述
     */
    @Schema(description = "详细描述" )
    private String content;

}
