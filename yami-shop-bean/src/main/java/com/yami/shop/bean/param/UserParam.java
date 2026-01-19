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

/**
 * @author Yami
 */
@Data
public class UserParam {
    /**
     * 积分
     */
    @Schema(description = "用户积分" )
    private Long score;

    /**
     * 会员成长值
     */
    @Schema(description = "会员成长值" )
    private Long growth;
    /**
     * 会员等级id
     */
    @Schema(description = "会员等级id" )
    private Integer level;
}
