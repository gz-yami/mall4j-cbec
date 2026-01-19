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
 * 全部会员，请求参数
 */
/**
 * @author Yami
 */
@Data
public class UserManagerReqParam {


    @Schema(description = "用户id" )
    private String userId;

    @Schema(description = "用户昵称" )
    private String nickName;

    @Schema(description = "真实姓名" )
    private String realName;

    @Schema(description = "手机号码" )
    private String userMobile;

    @Schema(description = "邮箱")
    private String userMail;

    @Schema(description = "性别 null 不限，M男，F女" )
    private String sex;

    @Schema(description = "状态 1 正常 0 无效" )
    private Integer status;

     /**
     * 客户注册时间 startTime
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date userRegStartTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date userRegEndTime;

    @Schema(description = "客户渠道 null 不限，公众号，小程序，H5，自有App" )
    private Integer appType;

    @Schema(description = "客户身份 0普通会员，1付费会员" )
    private Integer levelType;

    @Schema(description = "会员等级 1大众会员，2往后是自定义" )
    private Integer level;

    @Schema(description = "店铺d")
    private Long shopId;
    // ------交易信息----------------------------------
    /**
     * 最近消费时间
     */
    private Date reConsStartTime;
    private Date reconsEndTime;

    /**
     * 消费金额
     */
    private Double preConsAmount;
    private Double apConsAmount;

    /**
     * 消费次数
     */
    private Integer preConsTimes;
    private Integer apConsTimes;

    /**
     * 订单均价
     */
    private Double preOrderAverAmount;
    private Double apOrderAverAmount;

    /**
     * 平均折扣
     */
    private Double preAverDiscount;
    private Double apAverDiscount;

    /**
     * 当前余额
     */
    private Double preCurrentBalance;
    private Double apCurrentBalance;

    /**
     * 当前积分
     */
    private Integer preCurrentScore;
    private Integer apCurrentScore;

    /**
     * 充值金额
     */
    private Double preRechargeAmount;
    private Double apRechargeAmount;

    /**
     * 充值次数
     */
    private Integer preRechargeTimes;
    private Integer apRechargeTimes;

    @Schema(description = "客户标签id列表" )
    private List<Long> tagIds;

    @Schema(description = "排序字段的名称" )
    private String fieldName;
    /**
     * 排序规则
     * 一次只能安照一个字段排序
     * 升序：ascending
     * 降序：descending
     * 不排序: null
     */
    @Schema(description = "升序：ascending, 降序：descending, 不排序: null" )
    private String sortingType;

    @Schema(description = "会员名称")
    private String memberName;

    @Schema(description = "当前语言")
    private Integer lang;

    @Schema(description = "默认语言")
    private Integer defaultLang;
}
