/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.NoticeTag;
import com.yami.shop.dao.NoticeTagMapper;
import com.yami.shop.service.NoticeTagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告用户标签关联信息
 * @author LGH
 */
@Service
@AllArgsConstructor
public class NoticeTagServiceImpl extends ServiceImpl<NoticeTagMapper, NoticeTag> implements NoticeTagService {

    private final NoticeTagMapper noticeTagMapper;

    @Override
    public List<Long> listNoticeIdByUserId(String userId) {
        return noticeTagMapper.listNoticeIdByUserId(userId);
    }
}
