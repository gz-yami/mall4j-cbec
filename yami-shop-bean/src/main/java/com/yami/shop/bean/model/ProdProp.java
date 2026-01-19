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
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Yami
 */
@Data
@TableName("tz_prod_prop")
public class ProdProp implements Serializable {
    private static final long serialVersionUID = -8761177918672000191L;

    /**
     * 属性id
     */
    @TableId
    @Schema(description = "属性id" )
    private Long propId;

    /**
     * 属性名称
     */
    @TableField(exist=false)
    @Schema(description = "属性名称")
    private String propName;

    /**
     * 1:销售属性(规格); 2:参数属性;
     */
    @Schema(description = "1:销售属性(规格); 2:参数属性" )
    private Integer rule;

    /**
     * 店铺id
     */
    @Schema(description = "店铺id" )
    private Long shopId;

    @TableField(exist=false)
    @Schema(description = "规格语言列表" )
    List<ProdPropLang> prodPropLangList;

    /**
     * 属性值
     */
    @TableField(exist=false)
    @NotEmpty(message="规格属性值不能为空")
    @Schema(description = "规格属性值" )
    private List<ProdPropValue> prodPropValues;

}
