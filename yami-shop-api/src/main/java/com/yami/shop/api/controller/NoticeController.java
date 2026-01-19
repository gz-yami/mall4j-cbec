/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.api.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.app.dto.NoticeDto;
import com.yami.shop.bean.enums.NoticeType;
import com.yami.shop.bean.enums.VisibleUserType;
import com.yami.shop.bean.model.Notice;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.enums.StatusEnum;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.security.common.bo.UidInfoBO;
import com.yami.shop.security.common.util.AuthUserContext;
import com.yami.shop.service.NoticeService;
import com.yami.shop.service.NoticeTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LGH
 */
@RestController
@RequestMapping("/notice")
@Tag(name = "用户未登录状态的公告管理接口")
@AllArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final NoticeTagService noticeTagService;

    @GetMapping("/topNoticeList")
    @Operation(summary = "置顶公告列表信息" , description = "获取所有置顶公告列表信息")
    @Parameter(name = "shopId", description = "店铺id" , required = true)
    public ServerResponseEntity<List<NoticeDto>> getTopNoticeListByShopId() {
        List<Notice> noticeList = noticeService.listTopNoticeByShopId(Constant.PLATFORM_SHOP_ID);
        //筛选出立即发送和到时间定时发送的公告
        List<Notice> list = noticeList.stream().filter(noticeVO -> Objects.equals(noticeVO.getImmediatelySend(), 1) || (Objects.equals(noticeVO.getImmediatelySend(), 0) && noticeVO.getSendTime().before(new Date()))).collect(Collectors.toList());
        List<NoticeDto> noticeDtoList = BeanUtil.mapAsList(list, NoticeDto.class);
        UidInfoBO uidInfo = AuthUserContext.get();
        List<NoticeDto> result = new ArrayList<>();
        //登录状态显示 指定全部用户和指定用户可见的公告
        if(Objects.nonNull(uidInfo)){
            String userId = AuthUserContext.getUserId();
            List<Long> noticeIds = noticeTagService.listNoticeIdByUserId(userId);
            for (NoticeDto noticeDto : noticeDtoList) {
                if(Objects.equals(noticeDto.getVisibleUserType(),0)){
                    result.add(noticeDto);
                    continue;
                }
                if(Objects.equals(noticeDto.getVisibleUserType(),VisibleUserType.TAG.value()) && noticeIds.contains(noticeDto.getId())){
                    result.add(noticeDto);
                    continue;
                }
                List<String> userIds = Arrays.asList(noticeDto.getUserIds().split(StrUtil.COMMA));
                if(Objects.equals(noticeDto.getVisibleUserType(), VisibleUserType.SPECIFY.value()) && userIds.contains(userId)){
                    result.add(noticeDto);
                }
            }
        }else{
            //未登录状态只显示 全部用户可见的公告
            result = noticeDtoList.stream().filter(notice -> Objects.equals(notice.getVisibleUserType(),0)).collect(Collectors.toList());
        }
        return ServerResponseEntity.success(result);
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "公告详情" , description = "获取公告id公告详情")
    @Parameter(name = "id", description = "公告id" , required = true)
    public ServerResponseEntity<NoticeDto> getNoticeById(@PathVariable("id") Long id) {
        Notice notice = noticeService.getNoticeById(id);
        NoticeDto noticeDto = BeanUtil.map(notice, NoticeDto.class);
        return ServerResponseEntity.success(noticeDto);
    }

    /**
     * 公告列表
     */
    @GetMapping("/noticeList")
    @Operation(summary = "公告列表信息" , description = "获取所有公告列表信息")
    @Parameter(name = "shopId", description = "店铺id" , required = true)
    public ServerResponseEntity<IPage<NoticeDto>> pageNotice(PageParam<NoticeDto> page) {
        Notice notice = new Notice();
        notice.setShopId(Constant.PLATFORM_SHOP_ID);
        notice.setType(NoticeType.TO_USER.value());
        notice.setAccountId(Objects.nonNull(AuthUserContext.get()) ? AuthUserContext.getUserId() : null);
        notice.setSendTime(new Date());
        notice.setStatus(StatusEnum.ENABLE.value());
        return ServerResponseEntity.success(noticeService.pageNotice(page, notice));
    }
}
