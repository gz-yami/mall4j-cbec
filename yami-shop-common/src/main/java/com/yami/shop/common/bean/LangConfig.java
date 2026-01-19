/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.common.bean;

import lombok.Data;

import java.util.List;

/**
 * Paypal支付配置
 * @author cl
 */
@Data
public class LangConfig {

    /**
     *  主语言
     */
    private Integer lang;

    /**
     *  主语言
     */
    private String name;

    /**
     *  主语言代码
     */
    private String language;

    /**
     * 事件 id
     */
    private List<LangItemConfig> langItemList;

}
