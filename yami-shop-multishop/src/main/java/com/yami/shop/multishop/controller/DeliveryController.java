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

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yami.shop.bean.model.Delivery;
import com.yami.shop.common.enums.StatusEnum;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 *
 * @author lgh on 2018/11/26.
 */
@RestController
@RequestMapping("/platform/delivery")
@Tag(name = "物流公司接口")
@AllArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/list")
    @Operation(summary = "获取物流公司列表" , description = "获取物流公司列表")
    @PreAuthorize("@pms.hasPermission('platform:delivery:list')")
    public ServerResponseEntity<List<Delivery>> list(){
        List<Delivery> list = deliveryService.list(Wrappers.lambdaQuery(Delivery.class)
                .eq(Delivery::getStatus, StatusEnum.ENABLE.value())
        );
        return ServerResponseEntity.success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取物流公司列表" , description = "分页获取物流公司列表")
    public ServerResponseEntity<IPage<Delivery>> page(PageParam<Delivery> page, Delivery delivery){
        IPage<Delivery> deliveryPage = deliveryService.page(page, new LambdaQueryWrapper<Delivery>()
                .eq(Delivery::getStatus, StatusEnum.ENABLE.value())
                .eq(!ObjectUtils.isEmpty(delivery.getDvyId()), Delivery::getDvyId, delivery.getDvyId())
                .like(!ObjectUtils.isEmpty(delivery.getDvyName()), Delivery::getDvyName, delivery.getDvyName())
                .like(!ObjectUtils.isEmpty(delivery.getDvyNoTrack()), Delivery::getDvyNoTrack, delivery.getDvyNoTrack())
                .like(!ObjectUtils.isEmpty(delivery.getDvyNoHd()), Delivery::getDvyNoHd, delivery.getDvyNoHd())
        );
        return ServerResponseEntity.success(deliveryPage);
    }

    @GetMapping("/info/{dvyId}")
    @Operation(summary = "查询物流公司信息" , description = "查询物流公司信息")
    @Parameter(name = "dvyId", description = "物流id" )
    @PreAuthorize("@pms.hasPermission('platform:delivery:info')")
    public ServerResponseEntity<Delivery> getById(@PathVariable("dvyId") Long dvyId) {
        return ServerResponseEntity.success(deliveryService.getById(dvyId));
    }

    @PostMapping
    @PreAuthorize("@pms.hasPermission('platform:delivery:save')")
    @Operation(summary = "新增物流公司" , description = "新增物流公司")
    public ServerResponseEntity<Boolean> save(@RequestBody @Valid Delivery delivery) {
        delivery.setRecTime(new Date());
        delivery.setModifyTime(new Date());
        List<Delivery> deliveryList = deliveryService.list(new LambdaQueryWrapper<Delivery>()
                .eq(Delivery::getDvyName, delivery.getDvyName())
                .ne(Delivery::getStatus, -1));
        if (CollUtil.isNotEmpty(deliveryList)) {
            // 物流名称不能重复
            throw new YamiShopBindException("yami.platform.delivery.name.repeat");
        }
        return ServerResponseEntity.success(deliveryService.save(delivery));
    }

    @PutMapping
    @PreAuthorize("@pms.hasPermission('platform:delivery:update')")
    @Operation(summary = "修改物流公司信息" , description = "修改物流公司信息")
    public ServerResponseEntity<Boolean> updateById(@RequestBody @Valid Delivery delivery) {
        delivery.setModifyTime(new Date());
        delivery.setQueryUrl(null);
        List<Delivery> deliveryList = deliveryService.list(new LambdaQueryWrapper<Delivery>()
                .eq(Delivery::getDvyName, delivery.getDvyName())
                .ne(Delivery::getDvyId, delivery.getDvyId())
                .ne(Delivery::getStatus, -1));
        if (CollUtil.isNotEmpty(deliveryList)) {
            // 物流名称不能重复
            throw new YamiShopBindException("yami.platform.delivery.name.repeat");
        }
        boolean updateRes = deliveryService.update(delivery, Wrappers.lambdaUpdate(Delivery.class)
                .eq(Delivery::getDvyId, delivery.getDvyId())
                .gt(Delivery::getStatus, StatusEnum.DELETE.value())
        );
        if (!updateRes) {
            // 修改失败，当前物流公司信息可能已经被删除
            throw new YamiShopBindException("yami.delivery.exception.updateFail");
        }
        return ServerResponseEntity.success(Boolean.TRUE);
    }

    @DeleteMapping("/{dvyId}")
    @PreAuthorize("@pms.hasPermission('platform:delivery:delete')")
    @Operation(summary = "删除物流公司" , description = "删除物流公司")
    @Parameter(name = "dvyId", description = "物流id" )
    public ServerResponseEntity<Boolean> removeById(@PathVariable Long dvyId) {
        // 逻辑删除
        deliveryService.update(Wrappers.lambdaUpdate(Delivery.class)
                .set(Delivery::getStatus, StatusEnum.DELETE.value())
                .eq(Delivery::getDvyId, dvyId)
        );
        return ServerResponseEntity.success(Boolean.TRUE);
    }
}
