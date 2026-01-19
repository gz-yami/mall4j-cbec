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

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;

import java.util.Collections;
import java.util.List;
/**
 * 分页参数
 * @author yami
 */
@Schema
@ParameterObject
public class PageParam<T> extends PageDTO<T> {

    /**
     * 最大每页条数
     */
    private final long MAX_SIZE = 100;

    /**
     * 每页显示条数，默认 10
     */
    @Schema(description = "每页大小，默认10")
    private long size = 10;

    /**
     * 当前页
     */
    @Schema(description = "当前页，默认1")
    private long current = 1;

    /**
     * 查询数据列表
     */
    @Hidden
    private List<T> records;
    /**
     * 总数
     */
    @Hidden
    private long total = 0;


    /**
     * 是否进行 count 查询
     */
    @Hidden
    private boolean searchCount = true;

    @Hidden
    private String countId;
    @Hidden
    private Long maxLimit;
    @Hidden
    private boolean optimizeCountSql;
    @Hidden
    private List<OrderItem> orders;

    @Override
    public List<T> getRecords() {
        return this.records;
    }

    @Override
    public Page<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    @Override
    public long getTotal() {
        return this.total;
    }

    @Override
    public Page<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    @Hidden
    public boolean searchCount() {
        if (total < 0) {
            return false;
        }
        return searchCount;
    }

    @Override
    public Page<T> setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
        return this;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public Page<T> setSize(long size) {
        if (size > MAX_SIZE) {
            this.size = 100;
        } else {
            this.size = size;
        }
        return this;
    }

    @Override
    public long getCurrent() {
        return this.current;
    }

    @Override
    public Page<T> setCurrent(long current) {
        this.current = current;
        return this;
    }

    @Override
    public void setOrders(List<OrderItem> orders) {
        super.setOrders(Collections.emptyList());
    }


}
