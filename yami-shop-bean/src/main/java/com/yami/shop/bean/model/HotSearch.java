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

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Yami
 */
@Data
@TableName("tz_hot_search")
public class HotSearch implements Serializable {
    /**
     * 主键
     */
    @TableId
    @Schema(description = "热搜id" )
    private Long hotSearchId;

    @Schema(description = "店铺id" )
    private Long shopId;

    @Schema(description = "标题" )
    private String title;

    @Schema(description = "内容" )
    private String content;

    @Schema(description = "录入时间" )
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date recDate;

    @Schema(description = "顺序" )
    private Integer seq;

    @Schema(description = "状态 默认是1,0为下线" )
    private Integer status;

    @Schema(description = "热搜类型 1:商品 2:店铺" )
    private Integer type;

}
