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

import java.io.Serializable;

/**
 * 公告用户标签关联信息
 * @author LGH
 */
@Data
@TableName("tz_notice_tag")
@Schema(description = "公告用户标签关联信息")
public class NoticeTag implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;
    @Schema(description = "公告id")
    private Long noticeId;
    @Schema(description = "用户标签id")
    private Long userTagId;
}
