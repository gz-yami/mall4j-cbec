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

import cn.hutool.core.util.PhoneUtil;
import com.yami.shop.bean.enums.EsOperationType;
import com.yami.shop.bean.event.EsProductUpdateEvent;
import com.yami.shop.bean.model.ShopDetail;
import com.yami.shop.bean.param.ShopDetailParam;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.security.multishop.util.SecurityUtils;
import com.yami.shop.service.ShopDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Objects;


/**
 * @author lgh on 2018/08/29.
 */
@RestController
@RequestMapping("/shop/shopDetail")
@Tag(name = "店铺基本信息接口")
@AllArgsConstructor
public class ShopDetailController {

    private final ShopDetailService shopDetailService;
    private final ApplicationEventPublisher eventPublisher;


    @GetMapping("/info")
    @Operation(summary = "获取店铺基本信息", description = "获取店铺基本信息")
    public ServerResponseEntity<ShopDetail> info() {
        ShopDetail shopDetail = shopDetailService.getShopDetailByShopId(Constant.PLATFORM_SHOP_ID);
        shopDetail.setEmployeeId(SecurityUtils.getSysUser().getUserId());
        if (Objects.nonNull(shopDetail.getTel())) {
            shopDetail.setTel(PhoneUtil.hideBetween(shopDetail.getTel()).toString());
        }
        return ServerResponseEntity.success(shopDetail);
    }




    @PutMapping
    @Operation(summary = "更新店铺基本信息", description = "更新店铺基本信息")
    @PreAuthorize("@pms.hasPermission('shop:shopDetail:save')")
    public ServerResponseEntity<Void> update(@Valid @RequestBody ShopDetailParam shopDetailParam) {
        ShopDetail shopDetail = BeanUtil.map(shopDetailParam, ShopDetail.class);
        Long shopId = Constant.PLATFORM_SHOP_ID;
        shopDetail.setShopId(shopId);
        shopDetail.setUpdateTime(new Date());
        this.dealWithUpdateOrCreateInfo(shopDetail);
        // 修改店铺接收手机号在专门的接口
        shopDetailService.updateShopDetail(shopDetail);
        eventPublisher.publishEvent(new EsProductUpdateEvent(shopDetail.getShopId(), null, EsOperationType.UPDATE_BY_SHOP_ID));
        return ServerResponseEntity.success();
    }

    @PutMapping("/updateMobile")
    @Operation(summary = "修改店铺接收手机号", description = "修改店铺接收手机号")
    @PreAuthorize("@pms.hasPermission('shop:shopDetail:update')")
    public ServerResponseEntity<Void> updateMobile(@RequestBody ShopDetail shopDetail) {
        shopDetail.setShopId(Constant.PLATFORM_SHOP_ID);
        shopDetail.setUpdateTime(new Date());
        this.dealWithUpdateOrCreateInfo(shopDetail);
        shopDetailService.updateShopDetail(shopDetail);
        return ServerResponseEntity.success();
    }



    /**
     * 处理店铺信息，这些信息商家无法直接修改
     *
     * @param shopDetail
     */
    private void dealWithUpdateOrCreateInfo(ShopDetail shopDetail) {
        if (Objects.isNull(shopDetail)) {
            return;
        }
        shopDetail.setContractEndTime(null);
        shopDetail.setContractStartTime(null);
        shopDetail.setShopStatus(null);
        shopDetail.setType(null);
    }

}
