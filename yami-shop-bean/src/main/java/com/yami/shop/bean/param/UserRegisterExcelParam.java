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

import java.util.Date;

/**
 * 用户批量导入注册参数
 * @author: cl
 * @date: 2021-04-19 13:16:59
 */
@Data
public class UserRegisterExcelParam {

    @Schema(description = "序号" )
    private String serialNo;

    @Schema(description = "手机号(必填)" )
    private String phone;

    @Schema(description = "用户昵称(最多32个字)" )
    private String nickName;

    @Schema(description = "邮箱" )
    private String userMail;

    @Schema(description = "密码(必填)" )
    private String password;

    @Schema(description = "性别(M(男) / F(女))" )
    private String sex;

    @Schema(description = "生日(格式:1989-08-08)" )
    private String birthDate;

    @Schema(description = "备注" )
    private String userMemo;

    @Schema(description = "积分(限整数)" )
    private Long score;

    @Schema(description = "余额(精确小数点后两位)" )
    private Double balance;

    @Schema(description = "成长值(限整数)" )
    private Integer growth;

    @Schema(description = "会员类型 0普通会员 1付费会员" )
    private Integer levelType;

    @Schema(description = "会员等级" )
    private Integer level;

    @Schema(description = "vip结束时间" )
    private Date vipEndTime;

    /**
     * 余额最大值
     */
    public static final Double MAX_BALANCE = 999999999.99;
    /**
     * 积分最大值
     */
    public static final Integer MAX_SCORE = 100000000;

}
