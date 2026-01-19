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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author Yami
 */
@Schema(description = "首页图片对象")
@Data
public class IndexImgDto {

    /**
     * 图片
     */
    @Schema(description = "图片Url" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String imgUrl;

    /**
     * 顺序
     */
    @Schema(description = "图片顺序" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer seq;

    /**
     * 上传时间
     */
    @Schema(description = "上传时间" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Date uploadTime;

    /**
     * 类型
     */
    @Schema(description = "类型" , requiredMode = Schema.RequiredMode.REQUIRED)
    private int type;

    /**
     * 图片类型 0:小程序 1:pc
     */
    @Schema(description = "图片类型 0:小程序 1:pc" , requiredMode = Schema.RequiredMode.REQUIRED)
    private int imgType;

    /**
     * 关联id
     */
    @Schema(description = "关联id" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long relation;



}
