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
import java.util.Date;

/**
 *
 *
 * @author LGH
 * @date 2022-05-05 10:47:48
 */
@Data
@TableName("tz_prod_extension")
@Schema(description = "商品扩展信息表")
public class ProdExtension implements Serializable{
    private static final long serialVersionUID = 1L;

    @TableId
    private Long prodExtendId;

    @Schema(description = "创建时间" )
    private Date createTime;

    @Schema(description = "更新时间" )
    private Date updateTime;

    @Schema(description = "商品id" )
    private Long prodId;

    @Schema(description = "销量" )
    private Integer soldNum;

    @Schema(description = "注水销量" )
    private Integer waterSoldNum;

    @Schema(description = "评论数")
    private Integer commNum;

    @TableField(exist = false)
    @Schema(description = "实际库存" )
    private Integer actualStock;

    @TableField(exist = false)
    @Schema(description = "锁定库存" )
    private Integer lockStock;

    @TableField(exist = false)
    @Schema(description = "可售卖库存" )
    private Integer stock;
}
