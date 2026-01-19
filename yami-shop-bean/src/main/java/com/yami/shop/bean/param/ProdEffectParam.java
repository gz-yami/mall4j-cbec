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
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 商品洞察-商品效果参数
 */
/**
 * @author Yami
 */
@Data
public class ProdEffectParam {
    /**
     * 时间类型 1今日实时 2 近7天 3 近30天 4自然日 5自然月
     */
    @Schema(description = "时间类型 1今日实时 2 近7天 3 近30天 4自然日 5自然月" )
    private Integer dateType;
    /**
     * 开始时间
     */
    @Schema(description = "开始时间" )
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 结束时间
     */
    @Schema(description = "结束时间" )
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     * 时间
     */
    @Schema(description = "前端不传字段" )
    private Date dateTime;

    /**
     * 店铺id
     */
    @Schema(description = "店铺id，前端不传字段" )
    private Long shopId;

    /**
     * 分组详情 0 全部分组 1隐藏
     */
    @Schema(description = "分组详情 0 全部分组 1隐藏" )
    private Integer group;

    /**
     * 活动区域  0全部状态  1出售中 2仓库中 3已售罄
     */
    @Schema(description = "活动区域  0全部状态  1出售中 2仓库中 3已售罄" )
    private Integer status;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称" )
    private String prodName;
}
