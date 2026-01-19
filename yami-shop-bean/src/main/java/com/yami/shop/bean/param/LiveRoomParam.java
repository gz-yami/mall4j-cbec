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
import java.util.List;

/**
 * @author lhd
 * @date 2020-08-06 16:29:53
 */
@Data
public class LiveRoomParam {

    @Schema(description = "直播间id")
    private Long roomId;

    @Schema(description = "店铺id")
    private Long shopId;

    @Schema(description = "搜索类型 1.搜索直播间信息 2.搜索商品名 3.搜索主播昵称")
    private Integer searchType;

    @Schema(description = "直播间名称")
    private String name;

    @Schema(description = "主播昵称")
    private String nickName;

    @Schema(description = "主播手机号")
    private String userMobile;

    @Schema(description = "主播用户id")
    private String userId;

    @Schema(description = "是否置顶")
    private Integer roomTop;

    @Schema(description = "直播间状态 0.未开始 1.直播中 2.已结束 3.暂停中 -1.违规下架")
    private Integer liveStatus;

    @Schema(description = "直播背景图")
    private String coverImg;

    @Schema(description = "主播分享图")
    private String shareImg;

    @Schema(description = "直播封面图")
    private String feedsImg;

    @Schema(description = "直播开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @Schema(description = "直播结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @Schema(description = "商品名")
    private String productName;

    @Schema(description = "商品id列表")
    private List<Long> prodIds;

    @Schema(description = "当前语言id")
    private Integer langId;

    @Schema(description = "默认语言id")
    private Integer defaultLangId;
}
