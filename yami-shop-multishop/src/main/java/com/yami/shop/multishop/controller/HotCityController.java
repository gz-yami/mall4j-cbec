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
import com.yami.shop.bean.dto.AreaDto;
import com.yami.shop.bean.model.Area;
import com.yami.shop.bean.model.HotCity;
import com.yami.shop.common.enums.StatusEnum;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ResponseEnum;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.service.AreaService;
import com.yami.shop.service.HotCityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author chiley
 * @date 2022-12-01 14:02:57
 */
@RestController
@RequestMapping("/platform/hotCity")
@Tag(name = "热门城市")
@AllArgsConstructor
public class HotCityController {
    private final AreaService areaService;
    private final HotCityService hotCityService;

    @GetMapping("/page")
    @Operation(summary = "分页获取热门城市", description =  "分页获取热门城市")
    @PreAuthorize("@pms.hasPermission('platform:hotCity:page')")
    public ServerResponseEntity<IPage<HotCity>> page(HotCity hotCity, PageParam<HotCity> page) {
        PageParam<HotCity> hotCityPage = hotCityService.page(page, new LambdaQueryWrapper<HotCity>()
                .likeRight(StrUtil.isNotBlank(hotCity.getAreaNameEn()), HotCity::getAreaNameEn, hotCity.getAreaNameEn())
                .eq(hotCity.getStatus() != null, HotCity::getStatus, hotCity.getStatus())
                .orderByDesc(HotCity::getSeq).orderByDesc(HotCity::getCreateTime)
        );
        return ServerResponseEntity.success(hotCityPage);
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "获取热门城市信息", description =  "获取热门城市信息")
    @Parameter(name = "hotCityId", description = "热门城市id")
    @PreAuthorize("@pms.hasPermission('platform:hotCity:info')")
    public ServerResponseEntity<HotCity> info(@PathVariable("id") Long hotCityId) {
        return ServerResponseEntity.success(hotCityService.getById(hotCityId));
    }

    @PostMapping
    @Operation(summary = "新增热门城市", description =  "新增热门城市")
    @PreAuthorize("@pms.hasPermission('platform:hotCity:save')")
    public ServerResponseEntity<Void> save(@RequestBody @Valid HotCity hotCity) {
        checkArea(hotCity);
        HotCity city = hotCityService.getOne(new LambdaQueryWrapper<HotCity>().eq(HotCity::getAreaId, hotCity.getAreaId()));
        if (Objects.nonNull(city)) {
            throw new YamiShopBindException("yami.area.repeat.check");
        }
        hotCityService.save(hotCity);
        //清除缓存
        if (Objects.equals(hotCity.getStatus(), StatusEnum.ENABLE.value())) {
            hotCityService.removeHotCityList();
        }
        return ServerResponseEntity.success();
    }

    @PutMapping
    @Operation(summary = "修改热门城市", description =  "修改热门城市")
    @PreAuthorize("@pms.hasPermission('platform:hotCity:update')")
    public ServerResponseEntity<Void> update(@RequestBody @Valid HotCity hotCity) {
        checkArea(hotCity);
        HotCity city = hotCityService.getOne(new LambdaQueryWrapper<HotCity>().eq(HotCity::getAreaId, hotCity.getAreaId()).ne(HotCity::getHotCityId,hotCity.getHotCityId()));
        if (Objects.nonNull(city)) {
            throw new YamiShopBindException("yami.area.repeat.check");
        }
        hotCityService.updateById(hotCity);
        //清除缓存
        hotCityService.removeHotCityList();
        return ServerResponseEntity.success();
    }

    @DeleteMapping
    @Operation(summary = "删除热门城市", description =  "删除热门城市")
    @Parameter(name = "ids", description = "热门城市id列表")
    @PreAuthorize("@pms.hasPermission('platform:hotCity:delete')")
    public ServerResponseEntity<Void> delete(@RequestBody List<Long> ids) {
        hotCityService.removeByIds(ids);
        //清除缓存
        hotCityService.removeHotCityList();
        return ServerResponseEntity.success();
    }

    @GetMapping("/listCityInfo")
        @Operation(summary = "获取国家地区列表", description =  "获取国家地区列表")
    @PreAuthorize("@pms.hasPermission('platform:hotCity:list')")
    public ServerResponseEntity<List<AreaDto>> listCityInfo() {
        List<AreaDto> areas = areaService.getCountryAreaListInfo();
        return ServerResponseEntity.success(areas);
    }

    private void checkArea(HotCity hotCity) {
        Area area = areaService.getById(hotCity.getAreaId());
        if (Objects.isNull(area)) {
            throw new YamiShopBindException(ResponseEnum.METHOD_ARGUMENT_NOT_VALID);
        }
        hotCity.setAreaName(area.getAreaName());
    }
}
