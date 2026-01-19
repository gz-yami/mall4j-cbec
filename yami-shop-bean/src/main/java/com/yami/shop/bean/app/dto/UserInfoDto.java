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
 * @author Yami
 */
@Data
public class UserInfoDto {
    /**
     * 用户积分
     */
    @Schema(description = "用户积分" )
    private Integer score;
    /**
     * 用户余额
     */
    @Schema(description = "用户余额" )
    private Double totalBalance;
    /**
     * 优惠券数量
     */
    @Schema(description = "优惠券数量" )
    private Integer couponNum;
    /**
     * 消息
     */
    @Schema(description = "消息数量" )
    private Integer notifyNum;
}
