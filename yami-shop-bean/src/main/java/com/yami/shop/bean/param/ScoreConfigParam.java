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

import java.util.List;

/**
 * @author Yami
 */
@Data
public class ScoreConfigParam {
    /**
     * id
     */
    private Long id;

    @Schema(description = "参数名" )
    private String paramKey;

    @Schema(description = "参数值" )
    private String paramValue;

    @Schema(description = "签到获取积分" )
    private String signInScoreString;

    @Schema(description = "签到获取积分" )
    private List<Integer> signInScore;

    @Schema(description = "注册获取积分" )
    private Long registerScore;

    @Schema(description = "购物开关" )
    private Boolean shopScoreSwitch;

    @Schema(description = "购物获取积分" )
    private Double shopGetScore;

    @Schema(description = "购物积分抵现比例" )
    private Double shopUseScore;

    @Schema(description = "购物积分分类获取范围0为全部商品1为根据分类使用" )
    private Integer getDiscountRange;

    @Schema(description = "购物积分分类获取上限比例" )
    private Double getDiscount;

    @Schema(description = "购物积分分类获取上限比例" )
    private List<CategoryScoreConfigParam> categoryConfigs;

    @Schema(description = "购物积分分类使用范围0为全部商品1为根据分类使用" )
    private Integer useDiscountRange;

    @Schema(description = "平台使用积分分类上限比例" )
    private Double useDiscount;
//
//    /**
//     * 购物时使用积分上限
//     */
//    private Double shopUserLimit;


}
