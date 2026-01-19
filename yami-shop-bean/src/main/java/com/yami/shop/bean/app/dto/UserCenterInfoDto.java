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

/**
 * 用户个人中心信息
 * @author LGH
 */
@Data
public class UserCenterInfoDto {


    @Schema(description = "订单数量数据" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private OrderCountData orderCountData;

    @Schema(description = "审核状态：0 未审核 1已通过 -1未通过 null 未开店" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer shopAuditStatus;

    @Schema(description = "店铺状态(-1:已删除 0: 停业中 1:营业中 2:平台下线 3:平台下线待审核 4:开店申请中 5:开店申请待审核)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer shopStatus;

    @Schema(description = "是否已经设置过支付密码" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isSetPassword;

    @Schema(description = "店铺id" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shopId;

}
