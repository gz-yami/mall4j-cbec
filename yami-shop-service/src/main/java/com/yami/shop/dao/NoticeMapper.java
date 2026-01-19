/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.shop.bean.app.dto.NoticeDto;
import com.yami.shop.bean.model.Notice;
import com.yami.shop.common.util.PageParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告管理
 *
 * @author hzm
 * @date 2019-04-18 21:21:40
 */
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * 分页获取公告
     * @param page 分页参数
     * @param notice 搜索条件
     * @return 公告分页数据
     */
    Page<NoticeDto> pageNotice(Page<NoticeDto> page, @Param("notice") Notice notice);

    /**
     * 删除公告
     * @param id
     * @param shopId
     */
    void removeByIdAndShopId(@Param("id") Long id, @Param("shopId") long shopId);

    /**
     * 条件查询分页获取公告
     * @param page 分页参数
     * @param notice 公告参数
     * @return 公告分页数据
     */
    IPage<Notice> listPage(@Param("page")PageParam<Notice> page, @Param("notice")Notice notice);

    /**
     * 根据条件获取公告列表
     * @param notice
     * @return
     */
    List<Notice> getListByParam(@Param("notice") Notice notice);

    /**
     * 修改公告
     *
     * @param notice
     */
    void updateByNoticeId(@Param("notice") Notice notice);

    /**
     * 获取公告
     * @param id
     * @return
     */
    Notice getNoticeById(@Param("id") Long id);
}
