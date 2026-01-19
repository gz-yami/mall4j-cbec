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

import lombok.Data;

import java.util.List;

/**
 * @author Yami
 */
@Data
public class CustomerRespParam {

    /**
     * 访客数
     */
    private Integer visitor;
    /**
     * 之前的访客数
     */
    private Integer preVisitor;
    private Double visitorRate;
    /**
     * 累积粉丝数
     */
    private Integer fashNum;
    /**
     * 之前的累积粉丝数
     */
    private Integer preFashNum;
    private Double fashNumRate;
    /**
     * 累积会员数
     */
    private Integer member;
    /**
     * 之前的累积会员数
     */
    private Integer preMember;
    private Double memberRate;
    /**
     * 成交客户数
     */
    private Integer payNum;
    /**
     * 之前的成交客户数
     */
    private Integer prePayNum;
    private Double payNumRate;

    List<CustomerPayParam> customerTrend;

}
