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

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.model.HotSearch;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.service.HotSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author lgh on 2019/03/27.
 */
@RestController
@RequestMapping("/platform/hotSearch")
@Tag(name = "热搜接口")
@AllArgsConstructor
public class HotSearchController {

    private final HotSearchService hotSearchService;

    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('platform:hotSearch:page')")
    @Operation(summary = "分页获取热搜列表" , description = "分页获取热搜列表")
    public ServerResponseEntity<IPage<HotSearch>> page(HotSearch hotSearch, PageParam<HotSearch> page){
        IPage<HotSearch> hotSearchs = hotSearchService.page(page,new LambdaQueryWrapper<HotSearch>()
            .eq(HotSearch::getShopId, Constant.PLATFORM_SHOP_ID)
            .like(StrUtil.isNotBlank(hotSearch.getContent()), HotSearch::getContent,hotSearch.getContent())
                .like(StrUtil.isNotBlank(hotSearch.getTitle()), HotSearch::getTitle,hotSearch.getTitle())
            .eq(hotSearch.getStatus()!=null, HotSearch::getStatus,hotSearch.getStatus())
            .orderByDesc(HotSearch::getSeq)
        );
        return ServerResponseEntity.success(hotSearchs);
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "根据热搜Id获取热搜信息" , description = "根据热搜Id获取热搜信息")
    @PreAuthorize("@pms.hasPermission('platform:hotSearch:info')")
    public ServerResponseEntity<HotSearch> info(@PathVariable("id") Long id){
        HotSearch hotSearch = hotSearchService.getById(id);
        if (!Objects.equals(Constant.PLATFORM_SHOP_ID, hotSearch.getShopId())) {
            throw new YamiShopBindException("yami.no.auth");
        }
        return ServerResponseEntity.success(hotSearch);
    }

    @PostMapping
    @PreAuthorize("@pms.hasPermission('platform:hotSearch:save')")
    @Operation(summary = "新增热搜" , description = "新增热搜")
    public ServerResponseEntity<Void> save(@RequestBody @Valid HotSearch hotSearch){
        hotSearch.setRecDate(new Date());
        hotSearch.setShopId(Constant.PLATFORM_SHOP_ID);
        hotSearchService.save(hotSearch);
        //清除缓存
        hotSearchService.removeHotSearchDtoCacheByshopId(Constant.PLATFORM_SHOP_ID);
        return ServerResponseEntity.success();
    }

    @PutMapping
    @PreAuthorize("@pms.hasPermission('platform:hotSearch:update')")
    @Operation(summary = "更新热搜" , description = "更新热搜")
    public ServerResponseEntity<Void> update(@RequestBody @Valid HotSearch hotSearch){
        hotSearchService.updateById(hotSearch);
        //清除缓存
        hotSearchService.removeHotSearchDtoCacheByshopId(Constant.PLATFORM_SHOP_ID);
        return ServerResponseEntity.success();
    }

    @DeleteMapping
    @PreAuthorize("@pms.hasPermission('platform:hotSearch:delete')")
    @Operation(summary = "根据热搜id列表批量删除热搜" , description = "根据热搜id列表批量删除热搜")
    public ServerResponseEntity<Void> delete(@RequestBody List<Long> ids){
        hotSearchService.removeByIds(ids);
        //清除缓存
        hotSearchService.removeHotSearchDtoCacheByshopId(Constant.PLATFORM_SHOP_ID);
        return ServerResponseEntity.success();
    }
}
