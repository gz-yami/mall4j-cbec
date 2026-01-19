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


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.app.dto.NoticeDto;
import com.yami.shop.bean.enums.NoticeType;
import com.yami.shop.bean.model.Notice;
import com.yami.shop.bean.model.NoticeTag;
import com.yami.shop.bean.model.ShopDetail;
import com.yami.shop.bean.model.User;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.dao.NoticeMapper;
import com.yami.shop.service.NoticeService;
import com.yami.shop.service.NoticeTagService;
import com.yami.shop.service.ShopDetailService;
import com.yami.shop.service.UserService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公告管理
 *
 * @author hzm
 * @date 2019-04-18 21:21:40
 */
@Service
@AllArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    private NoticeMapper noticeMapper;
    private NoticeTagService noticeTagService;
    private UserService userService;
    private ShopDetailService shopDetailService;


    @Override
    @Cacheable(cacheNames = "notices", key = "#shopId")
    public List<Notice> listTopNoticeByShopId(Long shopId) {
        Notice notice = new Notice();
        notice.setShopId(shopId);
        notice.setType(NoticeType.TO_USER.value());
        notice.setStatus(1);
        return noticeMapper.getListByParam(notice);
    }

    @Override
    @CacheEvict(cacheNames = "notices", key = "#shopId")
    public void removeTopNoticeListCacheByShopId(Long shopId) {
    }

    @Override
    public Page<NoticeDto> pageNotice(Page<NoticeDto> page, Notice notice) {
        return noticeMapper.pageNotice(page, notice);
    }

    @Override
    @Cacheable(cacheNames = "notice", key = "#noticeId")
    public Notice getNoticeById(Long noticeId) {
        return noticeMapper.selectById(noticeId);
    }

    @Override
    @CacheEvict(cacheNames = "notice", key = "#noticeId")
    public void removeNoticeCacheById(Long noticeId) {
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdAndShopId(Long id, long shopId) {
        noticeMapper.removeByIdAndShopId(id,shopId);
        noticeTagService.remove(new LambdaQueryWrapper<NoticeTag>().eq(NoticeTag::getNoticeId,id));
    }

    @Override
    public IPage<Notice> listPage(PageParam<Notice> page, Notice notice) {
        return noticeMapper.listPage(page,notice);
    }

    @Override
    public Notice getInfoById(Long id) {
        Notice notice = noticeMapper.getNoticeById(id);
        if(StringUtils.isNotBlank(notice.getMultiShopIds())){
            List<Long> multiShopIds = Arrays.stream(notice.getMultiShopIds().split(",")).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
            List<ShopDetail> shopDetailByShopIds = shopDetailService.getShopDetailByShopIds(multiShopIds);
            for (ShopDetail shopDetail : shopDetailByShopIds){
                shopDetail.setMobile(shopDetail.getTel());
            }
            notice.setShopDetailList(shopDetailByShopIds);
        }
        if(StringUtils.isNotBlank(notice.getUserIds())){
            List<String> userIds = Arrays.stream(notice.getUserIds().split(",")).map(s -> s.trim()).collect(Collectors.toList());
            List<User> users = userService.getUserByUserIds(userIds);
            notice.setUserDetailList(users);
        }
        return notice;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByNoticeId(Notice notice, List<Long> userTagIds) {
        noticeMapper.updateByNoticeId(notice);
        noticeTagService.remove(new LambdaQueryWrapper<NoticeTag>().eq(NoticeTag::getNoticeId,notice.getId()));
        // 保存消息关联用户标签
        saveNoticeUserTags(notice, userTagIds);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveNotice(Notice notice, List<Long> userTagIds) {
        save(notice);
        // 保存消息关联用户标签
        saveNoticeUserTags(notice,userTagIds);
    }

    private void saveNoticeUserTags(Notice notice, List<Long> userTagIds) {
        if(CollectionUtils.isEmpty(userTagIds)){
            return;
        }
        List<NoticeTag> noticeTags = new ArrayList<>();
        for (Long userTagId : userTagIds) {
            NoticeTag noticeTag = new NoticeTag();
            noticeTag.setUserTagId(userTagId);
            noticeTag.setNoticeId(notice.getId());
            noticeTags.add(noticeTag);
        }
        noticeTagService.saveBatch(noticeTags);
    }

}
