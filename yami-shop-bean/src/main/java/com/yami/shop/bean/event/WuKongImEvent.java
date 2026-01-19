/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 悟空im的事件
 * @author LGH
 */
@Data
@AllArgsConstructor
public class WuKongImEvent {

    /**
     * 频道集合, 格式：Map<channelId, token列表>
     */
    private Map<String, List<String>> uidMap;
// 目前只有一个退出登录
//    /**
//     * 指令 具体看WuKongConstant.class
//     */
//    private String cmd;

}
