/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.delivery.multishop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.enums.DeliveryTemplateType;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.delivery.common.model.Transport;
import com.yami.shop.delivery.common.service.TransportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author lgh on 2018/11/16.
 */
@RestController
@RequestMapping("/platform/transport")
@Tag(name = "商家端运费模板接口")
@AllArgsConstructor
public class TransportController {

    private final TransportService transportService;

    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('platform:transport:page')")
    @Operation(summary = "分页查询运费模板信息")
    public ServerResponseEntity<IPage<Transport>> page(Transport transport, PageParam<Transport> page) {
        Long shopId = Constant.PLATFORM_SHOP_ID;
        IPage<Transport> transports = transportService.page(page,
                new LambdaQueryWrapper<Transport>()
                        .eq(Transport::getShopId, shopId)
                        .like(StringUtils.isNotBlank(transport.getTransName()), Transport::getTransName, transport.getTransName())
                        .orderByDesc(Transport::getCreateTime)
        );
        return ServerResponseEntity.success(transports);
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "根据id查询运费模板信息")
    @PreAuthorize("@pms.hasPermission('platform:transport:info')")
    @Parameter(name = "id", description = "运费模板id" , required = true)
    public ServerResponseEntity<Transport> info(@PathVariable("id") Long id) {
        Transport transport = transportService.getTransportAndAllItems(id);
        if (!Objects.equals(Constant.PLATFORM_SHOP_ID, transport.getShopId())) {
            throw new YamiShopBindException("yami.no.auth");
        }
        return ServerResponseEntity.success(transport);
    }

    @PostMapping
    @Operation(summary = "保存运费模板信息")
    @PreAuthorize("@pms.hasPermission('platform:transport:save')")
    public ServerResponseEntity<Long> save(@RequestBody Transport transport) {
        if (DeliveryTemplateType.isSysTemplate(transport.getTransName())) {
            // 与系统内置模板名称重复，请重新输入
            throw new YamiShopBindException("yami.delivery.name.is.same");
        }
        Long shopId = Constant.PLATFORM_SHOP_ID;
        transport.setShopId(shopId);
        Date createTime = new Date();
        transport.setCreateTime(createTime);
        if(Objects.isNull(transport.getTransfees()) && transport.getIsFreeFee() == 0){
            throw new YamiShopBindException("yami.delivery.error");
        }
        return ServerResponseEntity.success(transportService.insertTransportAndTransfee(transport));
    }

    @PutMapping
    @Operation(summary = "修改运费模板信息")
    @PreAuthorize("@pms.hasPermission('platform:transport:update')")
    public ServerResponseEntity<Void> update(@RequestBody Transport transport) {
        if (DeliveryTemplateType.isSysTemplate(transport.getTransName())) {
            // 与系统内置模板名称重复，请重新输入
            throw new YamiShopBindException("yami.delivery.name.is.same");
        }
        if(Objects.isNull(transport.getTransfees()) && transport.getIsFreeFee() == 0){
            throw new YamiShopBindException("yami.delivery.error");
        }
        transportService.updateTransportAndTransfee(transport);
        return ServerResponseEntity.success();
    }

    @DeleteMapping
    @Operation(summary = "根据运费模板id列表批量删除运费模板信息")
    @PreAuthorize("@pms.hasPermission('platform:transport:delete')")
    @Parameter(name = "ids", description = "运费模板id" , required = true)
    public ServerResponseEntity<Void> delete(@RequestBody Long[] ids) {
        transportService.deleteTransportAndTransfeeAndTranscity(ids);
        // 删除运费模板的缓存
        for (Long id : ids) {
            transportService.removeTransportAndAllItemsCache(id);
        }
        return ServerResponseEntity.success();
    }

    @GetMapping("/list")
    @Operation(summary = "获取运费模板列表")
    public ServerResponseEntity<List<Transport>> list() {
        Long shopId = Constant.PLATFORM_SHOP_ID;
        List<Transport> list = transportService.list(new LambdaQueryWrapper<Transport>()
                .eq(Transport::getShopId, shopId)
                .orderByDesc(Transport::getCreateTime)
        );
        return ServerResponseEntity.success(list);
    }
}
