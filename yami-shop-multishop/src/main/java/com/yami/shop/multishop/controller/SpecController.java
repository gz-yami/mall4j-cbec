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
import com.yami.shop.bean.enums.ProdPropRule;
import com.yami.shop.bean.model.ProdProp;
import com.yami.shop.bean.model.ProdPropValue;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.service.ProdPropService;
import com.yami.shop.service.ProdPropValueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 规格管理
 *
 * @author lgh
 */
@RestController
@RequestMapping("/prod/spec")
@Tag(name = "规格管理接口")
@AllArgsConstructor
public class SpecController {

    private final ProdPropService prodPropService;
    private final ProdPropValueService prodPropValueService;

    /**
     * 分页获取
     */
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('prod:spec:page')")
    @Operation(summary = "分页查询规格" , description = "分页查询规格")
    public ServerResponseEntity<IPage<ProdProp>> page(ProdProp prodProp, PageParam<ProdProp> page) {
        prodProp.setRule(ProdPropRule.SPEC.value());
        prodProp.setShopId(Constant.PLATFORM_SHOP_ID);
        IPage<ProdProp> list = prodPropService.pagePropAndValue(prodProp, page);
        return ServerResponseEntity.success(list);
    }


    /**
     * 获取所有的规格
     */
    @GetMapping("/list")
    @Operation(summary = "获取所有的规格" , description = "获取所有的规格")
    @PreAuthorize("@pms.hasPermission('prod:spec:list')")
    public ServerResponseEntity<List<ProdProp>> list() {
        ProdProp prodProp = new ProdProp();
        prodProp.setRule(ProdPropRule.SPEC.value());
        prodProp.setShopId(Constant.PLATFORM_SHOP_ID);
        List<ProdProp> list = prodPropService.listByLang(prodProp);
        return ServerResponseEntity.success(list);
    }

    /**
     * 根据规格id获取规格值
     */
    @GetMapping("/listSpecValue/{specId}")
    @Operation(summary = "根据规格id获取规格值" , description = "根据规格id获取规格值")
    @PreAuthorize("@pms.hasPermission('prod:spec:list')")
    public ServerResponseEntity<List<ProdPropValue>> listSpecValue(@PathVariable("specId") Long specId) {
        List<ProdPropValue> list = prodPropValueService.propValueListByPropId(specId);
        return ServerResponseEntity.success(list);
    }

    /**
     * 根据获取规格值最大的自增id
     */
    @GetMapping("/listSpecMaxValueId")
    @Operation(summary = "根据获取规格值最大的自增id" , description = "根据获取规格值最大的自增id")
    @PreAuthorize("@pms.hasPermission('prod:spec:maxValue')")
    public ServerResponseEntity<Long> listSpecMaxValueId() {
        ProdPropValue propValue = prodPropValueService.getOne(new LambdaQueryWrapper<ProdPropValue>()
                .orderByDesc(ProdPropValue::getValueId).last("limit 1"));
        return ServerResponseEntity.success(Objects.isNull(propValue) ? 0L : propValue.getValueId());
    }
    /**
     * 保存
     */
    @PostMapping
    @PreAuthorize("@pms.hasPermission('prod:spec:save')")
    @Operation(summary = "新增规格" , description = "新增规格")
    public ServerResponseEntity<Void> save(@Valid @RequestBody ProdProp prodProp) {
        prodProp.setRule(ProdPropRule.SPEC.value());
        prodProp.setShopId(Constant.PLATFORM_SHOP_ID);
        prodPropService.saveProdPropAndValues(prodProp);
        return ServerResponseEntity.success();
    }

    /**
     * 修改
     */
    @PutMapping
    @PreAuthorize("@pms.hasPermission('prod:spec:update')")
    @Operation(summary = "修改规格" , description = "修改规格")
    public ServerResponseEntity<Void> update(@Valid @RequestBody ProdProp prodProp) {
        ProdProp dbProdProp = prodPropService.getById(prodProp.getPropId());
        if (!Objects.equals(dbProdProp.getShopId(), Constant.PLATFORM_SHOP_ID)) {
            // 没有权限获取该商品规格信息
            throw new YamiShopBindException("yami.product.no.auth");
        }
        prodProp.setRule(ProdPropRule.SPEC.value());
        prodProp.setShopId(Constant.PLATFORM_SHOP_ID);
        prodPropService.updateProdPropAndValues(prodProp);
        return ServerResponseEntity.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('prod:spec:delete')")
    @Operation(summary = "根据id删除规格" , description = "根据id删除规格")
    public ServerResponseEntity<Void> delete(@PathVariable Long id) {
        prodPropService.deleteProdPropAndValues(id, ProdPropRule.SPEC.value(), Constant.PLATFORM_SHOP_ID);
        return ServerResponseEntity.success();
    }
}
