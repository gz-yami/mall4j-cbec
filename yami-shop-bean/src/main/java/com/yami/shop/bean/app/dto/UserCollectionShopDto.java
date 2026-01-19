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
@Schema(description = "店铺收藏对象")
@Data
public class UserCollectionShopDto {

    @Schema(description = "收藏id" )
    private Long collectionId;

    @Schema(description = "店铺id" )
    private Long shopId;

    @Schema(description = "店铺装修id" )
    private Long renovationId;

    @Schema(description = "店铺名称" )
    private String shopName;

    @Schema(description = "店铺logo图片" )
    private String shopLogo;

    @Schema(description = "店铺状态(-1:已删除 0: 停业中 1:营业中 2:平台下线 3:平台下线待审核 4:开店申请中 5:开店申请待审核)" )
    private Integer shopStatus;

    @Schema(description = "签约起始时间" )
    private Date contractStartTime;

    @Schema(description = "签约终止时间" )
    private Date contractEndTime;

    @Schema(description = "收藏时间" )
    private Date createTime;

}
