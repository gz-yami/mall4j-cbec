/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.app.dto.NoticeDto;
import com.yami.shop.bean.model.Notice;
import com.yami.shop.common.util.PageParam;

import java.util.List;

/**
 * 公告管理
 *
 * @author hzm
 * @date 2019-04-18 21:21:40
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 获取店铺公告列表
     * @param shopId 店铺id
     * @return 公告列表
     */
    List<Notice> listTopNoticeByShopId(Long shopId);

    /**
     * 删除店铺公告缓存
     * @param shopId 店铺id
     */
    void removeTopNoticeListCacheByShopId(Long shopId);

    /**
     * 分页获取店铺公告
     * @param page 分页参数
     * @param notice 公告参数
     * @return 公告分页数据
     */
    Page<NoticeDto> pageNotice(Page<NoticeDto> page,Notice notice);

    /**
     * 通过id查询公告
     * @param noticeId 公告id
     * @return
     */
    Notice getNoticeById(Long noticeId);

    /**
     * 删除公告缓存
     * @param noticeId 公告id
     */
    void removeNoticeCacheById(Long noticeId);

    /**
     * 删除公告通过店铺和公告id
     * @param id 公告id
     * @param shopId 店铺id
     */
    void removeByIdAndShopId(Long id, long shopId);

    /**
     * 条件查询，分页获取公告
     * @param page 分页参数
     * @param notice 公告参数
     * @return 公告分页数据
     */
    IPage<Notice> listPage(PageParam<Notice> page, Notice notice);

    /**
     * 根据id获取公告详情
     * @param id
     * @return
     */
    Notice getInfoById(Long id);

    /**
     * 修改公告
     *
     * @param notice
     * @param userTagIds
     */
    void updateByNoticeId(Notice notice, List<Long> userTagIds);

    /**
     * 保存
     *
     * @param notice
     * @param userTagIds
     */
    void saveNotice(Notice notice, List<Long> userTagIds);
}
