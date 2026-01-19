/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.security.common.util;


import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Set;

/**
 * @author FrozenWatermelon
 * @date 2020/7/16
 */
public class PmsContext {

    private static final ThreadLocal<Set<String>> PMS_HOLDER = new TransmittableThreadLocal<>();

    public static Set<String> get() {
        return PMS_HOLDER.get();
    }

    public static void set(Set<String> prems) {
        PMS_HOLDER.set(prems);
    }
    public static void clean() {
        if (PMS_HOLDER.get() != null) {
            PMS_HOLDER.remove();
        }
    }
}
