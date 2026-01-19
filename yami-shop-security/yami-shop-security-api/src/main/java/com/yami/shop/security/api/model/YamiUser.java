/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.security.api.model;

import lombok.Data;
/**
 * 用户详细信息
 * @author LGH
 */
@Data
public class YamiUser {

    /**
     * 用户ID
     */
    private String userId;

    private String bizUserId;

    /**
     * 自提点Id
     */
    private Long stationId;

    /**
     * 店铺Id - 门店端使用
     */
    private Long shopId;

}
