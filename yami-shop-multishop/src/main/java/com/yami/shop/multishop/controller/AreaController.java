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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.dto.AreaDto;
import com.yami.shop.bean.enums.AreaLevelEnum;
import com.yami.shop.bean.model.Area;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.service.AreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author lgh on 2018/10/26.
 */
@RestController
@RequestMapping("/admin/area")
@Tag(name = "地区信息接口")
@AllArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @GetMapping("/list")
    @Operation(summary = "获取省市区地区信息列表（可传入地区名称搜索）" , description = "分页获取省市区地区信息列表（可传入地区名称搜索）")
    @PreAuthorize("@pms.hasPermission('admin:area:list')")
    public ServerResponseEntity<List<Area>> list(Area area) {
        List<Area> areas = areaService.list(new LambdaQueryWrapper<Area>()
                .like(area.getAreaName() != null, Area::getAreaName, area.getAreaName())
                .lt(!Objects.isNull(area.getMaxGrade()),Area::getLevel,area.getMaxGrade())
                .gt(!Objects.isNull(area.getMinGrade()),Area::getLevel,area.getMinGrade())
        );
        return ServerResponseEntity.success(areas);
    }

    @GetMapping("/listByPid")
    @Operation(summary = "通过父级id获取区域列表" , description = "通过父级id获取区域列表")
    @PreAuthorize("@pms.hasPermission('admin:area:list')")
    public ServerResponseEntity<List<Area>> listByPid(Long pid, Integer level) {
        List<Area> list = areaService.listByPid(pid, level);
        return ServerResponseEntity.success(list);
    }

    @GetMapping("/areaListInfo")
    @Operation(summary = "获取省市区信息" , description = "获取省市区信息")
    public ServerResponseEntity<List<AreaDto>> getAreaListInfo() {
        return ServerResponseEntity.success(areaService.getAreaListInfo());
    }

    @GetMapping("/listAreaOfEnable")
    @Operation(summary = "获取可用的省市区列表(完整路径）" , description = "获取可用的省市区列表（完整路径）")
    public ServerResponseEntity<List<AreaDto>> listAreaOfEnable() {
        List<AreaDto> list = areaService.listAreaOfEnable();
        return ServerResponseEntity.success(list);
    }

    @GetMapping("/areaList")
    @PreAuthorize("@pms.hasPermission('admin:area:list')")
    @Operation(summary = "获取可用的区域省市区信息（四级地址列表）" , description = "获取可用的区域省市区信息")
    public ServerResponseEntity<List<AreaDto>> getAllAreaList() {
        return ServerResponseEntity.success(areaService.getAllAreaList());
    }
    @GetMapping("/page")
    @Operation(summary = "分页查询" , description = "分页查询")
    @PreAuthorize("@pms.hasPermission('admin:area:page')")
    public ServerResponseEntity<IPage<Area>> page(Area area, PageParam<Area> page) {
        IPage<Area> sysUserPage = areaService.page(page, new LambdaQueryWrapper<Area>());
        return ServerResponseEntity.success(sysUserPage);
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "获取信息" , description = "获取信息")
    public ServerResponseEntity<Area> info(@PathVariable("id") Long id) {
        Area area = areaService.getById(id);
        return ServerResponseEntity.success(area);
    }

    @PostMapping
    @Operation(summary = "保存" , description = "保存")
    @PreAuthorize("@pms.hasPermission('admin:area:save')")
    public ServerResponseEntity<Void> save(@Valid @RequestBody Area area) {
        if (area.getParentId() != null && !Objects.equals(area.getParentId(), 0L)) {
            areaService.removeAreaCacheByParentId(null, 1);
            Area parentArea = areaService.getById(area.getParentId());
            area.setLevel(parentArea.getLevel() + 1);
            areaService.removeAreaCacheByParentId(area.getParentId(), null);
            // 因为获取地址的时候，过滤掉了没有三级地址的一级地址，情况缓存的时候，一级地址也需要清空一下
            if (Objects.equals(area.getLevel(), AreaLevelEnum.THIRD_LEVEL.value())) {
                areaService.removeAreaCacheByParentId(0L, null);
            }
        }
        hasSameName(area);
        areaService.removeAreaListCache();
        areaService.save(area);
        return ServerResponseEntity.success();
    }

    @PutMapping
    @Operation(summary = "修改" , description = "修改")
    @PreAuthorize("@pms.hasPermission('admin:area:update')")
    public ServerResponseEntity<Void> update(@Valid @RequestBody Area area) {
        Area areaDb = areaService.getById(area.getAreaId());
        // 判断当前省市区级别，如果是1级、2级则不能修改级别，不能修改成别人的下级
        if(Objects.equals(areaDb.getLevel(),AreaLevelEnum.FIRST_LEVEL.value()) && !Objects.equals(area.getLevel(),AreaLevelEnum.FIRST_LEVEL.value())){
            // 不能改变一级行政地区的级别
            throw new YamiShopBindException("yami.area.exception.levelOneCannotChange");
        }
        if(Objects.equals(areaDb.getLevel(),AreaLevelEnum.SECOND_LEVEL.value()) && !Objects.equals(area.getLevel(),AreaLevelEnum.SECOND_LEVEL.value())){
            // 不能改变而级行政地区的级别
            throw new YamiShopBindException("yami.area.exception.levelTwoCannotChange");
        }
        hasSameName(area);
        areaService.updateById(area);
        areaService.removeAreaCacheByParentId(null, 1);
        areaService.removeAreaCacheByParentId(area.getParentId(), null);
        areaService.removeAreaListCache();
        return ServerResponseEntity.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除" , description = "删除")
    @PreAuthorize("@pms.hasPermission('admin:area:delete')")
    public ServerResponseEntity<Void> delete(@PathVariable Long id) {
        if (areaService.count(new LambdaQueryWrapper<Area>().eq(Area::getParentId,id)) > 0) {
            // 请先删除子地区
            throw new YamiShopBindException("yami.area.delete");
        }

        Area area = areaService.getById(id);
        if(area==null){
            // 地址不存在或已被删除
            throw new YamiShopBindException("yami.area.exception.areaNotExist");
        }
        areaService.removeById(id);
        areaService.removeAreaCacheByParentId(null, 1);
        areaService.removeAreaCacheByParentId(area.getParentId(), null);
        areaService.removeAreaListCache();
        // 因为获取地址的时候，过滤掉了没有三级地址的一级地址，情况缓存的时候，一级地址也需要清空一下
        if (Objects.equals(area.getLevel(), AreaLevelEnum.THIRD_LEVEL.value())) {
            areaService.removeAreaCacheByParentId(0L, null);
        }
        return ServerResponseEntity.success();
    }

    private void hasSameName(Area area) {
        long count = areaService.count(new LambdaQueryWrapper<Area>()
                .eq(Area::getParentId, area.getParentId())
                .eq(Area::getAreaName, area.getAreaName())
                .ne(Objects.nonNull(area.getAreaId()) && !Objects.equals(area.getAreaId(), Constant.ZERO_LONG), Area::getAreaId, area.getAreaId())
        );
        if (count > 0) {
            throw new YamiShopBindException("yami.area.exception.areaExist");
        }
    }
}
