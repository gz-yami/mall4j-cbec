/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.common.util;


import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 解析SPEL 表达式
 * @author huxingnan
 * @date 2018/5/21 10:51
 */
public class BeanUtil {
    public static <S, D> List<D> mapAsList(final Iterable<S> sourceObject, Class<D> clazz) {
        return JSONObject.parseArray(JSONObject.toJSONString(sourceObject), clazz);
    }
    public static <S, D> D map(final S sourceObject, Class<D> clazz) {
        return JSONObject.parseObject(JSONObject.toJSONString(sourceObject), clazz);
    }
}
