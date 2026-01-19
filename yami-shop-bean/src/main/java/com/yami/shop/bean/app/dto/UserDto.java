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
public class UserDto {


    @Schema(description = "用户状态：0禁用 1正常" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "用户昵称" )
    private String nickName;

    @Schema(description = "用户名" )
    private String username;

    @Schema(description = "用户id" )
    private String userId;

    @Schema(description = "用户性别M(男 1) or F(女 0)" )
    private String sex;

    @Schema(description = "用户手机号" )
    private String userMobile;

    @Schema(description = "用户邮箱")
    private String userMail;

    @Schema(description = "用户手机号隐藏位数" )
    private String mobile;

    @Schema(description = "头像图片路径" )
    private String pic;

    @Schema(description = "用户生日" )
    private String birthDate;
    /**
     * 用户等级
     */
    @Schema(description = "用户等级" )
    private Integer level;
    /**
     * 等级条件 0 普通会员 1 付费会员
     */
    @Schema(description = "等级条件 0 普通会员 1 付费会员" )
    private Integer levelType;
    /**
     * 用户当前成长值
     */
    @Schema(description = "用户当前成长值" )
    private Integer growth;
    /**
     * 用户积分
     */
    @Schema(description = "用户积分" )
    private Long score;
    /**
     * 用户余额
     */
    @Schema(description = "用户余额" )
    private Double balance;

    @Schema(description = "商品个性化推荐 1:开启 0:关闭" )
    private Integer prodRecommendation;
}
