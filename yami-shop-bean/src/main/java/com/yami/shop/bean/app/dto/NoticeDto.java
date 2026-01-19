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
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author Yami
 */
@Schema(description = "公告对象")
@Data
public class NoticeDto {

    @Schema(description = "公告id" )
    private Long id;

    @Schema(description = "店铺id" )
    private Long shopId;

    @Schema(description = "类型不能为空（平台端同时创建多类型公告,用逗号隔开）(1:在商家端展示 2:在用户端展示 3：在供应商端展示)" )
    private String types;

    @Schema(description = "标题" )
    private String title;

    @Schema(description = "公告内容" )
    private String content;

    @Schema(description = "是否置顶（1:是 0：否）" )
    private Integer isTop;

    @Schema(description = "公告发布时间" )
    private Date publishTime;

    @Schema(description = "商家可见范围 (用逗号隔开，为空则全部可见)" )
    private String multiShopIds;

    @Schema(description = "可见用户类型 0.全部用户可见 1.标签用户可见 2.指定用户可见" )
    private Integer visibleUserType;

    @Schema(description = "用户标签集合" )
    private List<Long> userTagIds;

    @Schema(description = "指定用户ids" )
    private String userIds;

    @Schema(description = "是否立即发送 1：立即发送  0：定时发送" )
    private Integer immediatelySend;

    @Schema(description = "定时发送时间" )
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    @Schema(description = "状态(1:公布 0:撤回)" )
    private Integer status;

    @Schema(description = "创建时间" )
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "更新时间" )
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public interface NoContent{}

    public interface WithContent extends NoContent{}

}
