/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.multishop.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.shop.bean.app.dto.NoticeDto;
import com.yami.shop.bean.model.Notice;
import com.yami.shop.common.annotation.SysLog;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 公告管理
 *
 * @author hzm
 * @date
 */
@RestController
@AllArgsConstructor
@RequestMapping("/platform/notice")
@Tag(name = "公告")
public class NoticeController {

    private final NoticeService noticeService;


    @GetMapping("/page")
    @Operation(summary = "分页查询" , description = "分页查询")
    @PreAuthorize("@pms.hasPermission('platform:notice:page')")
    public ServerResponseEntity<IPage<NoticeDto>> getNoticePage(Page<NoticeDto> page, Notice notice) {
        notice.setShopId(Constant.PLATFORM_SHOP_ID);
        notice.setAccountId(String.valueOf(Constant.PLATFORM_SHOP_ID));
        Page<NoticeDto> noticeDtoPage = noticeService.pageNotice(page, notice);
        return ServerResponseEntity.success(noticeDtoPage);
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "公告信息" , description = "公告信息")
    @Parameter(name = "id", description = "公告id" )
    @PreAuthorize("@pms.hasPermission('platform:notice:info')")
    public ServerResponseEntity<Notice> getById(@PathVariable("id") Long id) {
        return ServerResponseEntity.success(noticeService.getInfoById(id));
    }

    @SysLog("新增公告管理")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('platform:notice:save')")
    @Operation(summary = "新增公告" , description = "新增公告")
    public ServerResponseEntity<Void> save(@RequestBody @Valid NoticeDto noticeDto) {
        Notice notice = BeanUtil.map(noticeDto, Notice.class);
        notice.setShopId(Constant.PLATFORM_SHOP_ID);
        if (notice.getStatus() == 1) {
            notice.setPublishTime(new Date());
        }
        notice.setUpdateTime(new Date());
        noticeService.saveNotice(notice,noticeDto.getUserTagIds());
        noticeService.removeTopNoticeListCacheByShopId(Constant.PLATFORM_SHOP_ID);
        noticeService.removeNoticeCacheById(notice.getId());
        return ServerResponseEntity.success();
    }

    @SysLog("修改公告管理")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('platform:notice:update')")
    @Operation(summary = "修改公告" , description = "修改公告")
    public ServerResponseEntity<Void> updateById(@RequestBody @Valid NoticeDto noticeDto) {
        Notice oldNotice = noticeService.getById(noticeDto.getId());
        Notice notice = BeanUtil.map(noticeDto, Notice.class);
        notice.setShopId(Constant.PLATFORM_SHOP_ID);
        if (oldNotice.getStatus() == 0 && notice.getStatus() == 1) {
            notice.setPublishTime(new Date());
        }
        notice.setUpdateTime(new Date());
        noticeService.updateByNoticeId(notice,noticeDto.getUserTagIds());
        noticeService.removeTopNoticeListCacheByShopId(Constant.PLATFORM_SHOP_ID);
        noticeService.removeNoticeCacheById(notice.getId());
        return ServerResponseEntity.success();
    }

    @SysLog("删除公告管理")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('platform:notice:delete')")
    @Operation(summary = "删除公告" , description = "删除公告")
    @Parameter(name = "id", description = "公告id" )
    public ServerResponseEntity<Void> removeById(@PathVariable Long id) {
        noticeService.removeByIdAndShopId(id,Constant.PLATFORM_SHOP_ID);
        noticeService.removeTopNoticeListCacheByShopId(Constant.PLATFORM_SHOP_ID);
        noticeService.removeNoticeCacheById(id);
        return ServerResponseEntity.success();
    }

}
