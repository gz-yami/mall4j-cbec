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
 * @author Yami
 */
@Data
@TableName("tz_index_img")
public class IndexImg implements Serializable {
    private static final long serialVersionUID = -3468251351681518798L;
    /**
     * 主键
     */
    @TableId
    @Schema(description = "轮播图id" )
    private Long imgId;

    @Schema(description = "店铺id" )
    private Long shopId;

    @Schema(description = "图片" )
    private String imgUrl;

    @Schema(description = "描述" )
    private String des;

    @Schema(description = "标题" )
    private String title;

    @Schema(description = "链接" )
    private String link;

    @Schema(description = "状态" )
    private Integer status;

    @Schema(description = "顺序" )
    private Integer seq;

    @Schema(description = "上传时间" )
    private Date uploadTime;

    @Schema(description = "类型" )
    private int type;

    @Schema(description = "图片类型 0:移动端 1:pc" )
    private Integer imgType;

    @Schema(description = "关联id" )
    private Long relation;

    @Schema(description = "商品图片" )
    @TableField(exist = false)
    private String pic;

    @Schema(description = "商品名称" )
    @TableField(exist = false)
    private String prodName;

}
