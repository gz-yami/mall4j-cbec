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


import cn.hutool.core.collection.CollUtil;

import java.util.List;
import java.util.function.Function;

/**
 * 大数据批量插入 - 一般用于excel导入
 * @author YXF
 * @date 2024/01/18 10:51
 */
public class DataBatchHandleUtil {

    /**
     * 大数据批量插入 - 事务中使用
     *
     * @param list 操作的数据列表
     * @param insertFunc
     * @param <T>
     */
    public static <T> void batchSplitInsert(List<T> list, Function<List<T>, Boolean> insertFunc) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        // mybatis 批量插入一万以内
        int maxInsertItemNumPerTime = 5000;
        int size = list.size();
        if (size > maxInsertItemNumPerTime) {
            for (int i = 0; i < size; i += maxInsertItemNumPerTime) {
                int endIndex = Math.min(i + maxInsertItemNumPerTime, size);
                List<T> subList = list.subList(i, endIndex);
                insertFunc.apply(subList);
            }
        } else {
            insertFunc.apply(list);
        }
    }
}
