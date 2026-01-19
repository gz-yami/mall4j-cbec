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
import com.yami.shop.bean.dto.CurrencyDTO;
import com.yami.shop.bean.model.Currency;
import com.yami.shop.bean.vo.CurrencyVO;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件上传 controller
 *
 * @author lgh
 */

@RestController
@RequestMapping("/platform/currency")
@AllArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/page")
    @Operation(summary = "获取货币汇率表列表", description = "分页获取货币汇率表列表")
    public ServerResponseEntity<IPage<CurrencyVO>> getCurrencyPage(PageParam<CurrencyVO> pageParam, CurrencyDTO currencyDTO) {
        return ServerResponseEntity.success(currencyService.pageCurrency(pageParam,currencyDTO));
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "获取货币汇率表", description = "根据id获取货币汇率表")
    @Parameter(name = "id", description = "货币汇率表", required = true)
    public ServerResponseEntity<CurrencyVO> getById(@PathVariable("id") Long id) {
        return ServerResponseEntity.success(currencyService.getCurrencyById(id));
    }

    @PostMapping
    @PreAuthorize("@pms.hasPermission('platform:currency:save')")
    @Operation(summary = "保存货币汇率表", description = "保存货币汇率表")
    public ServerResponseEntity<Void> save(@RequestBody @Valid Currency currency) {
        currencyService.saveCurrency(currency);
        return ServerResponseEntity.success();
    }

    @PutMapping
    @PreAuthorize("@pms.hasPermission('platform:currency:update')")
    @Operation(summary = "更新货币汇率表", description = "更新货币汇率表")
    public ServerResponseEntity<Void> updateById(@RequestBody @Valid Currency currency) {
        currencyService.updateCurrency(currency);
        return ServerResponseEntity.success();
    }

    @PutMapping("/enableCurrency")
    @PreAuthorize("@pms.hasPermission('platform:currency:update')")
    @Operation(summary = "更换默认货币", description = "更换默认货币")
    public ServerResponseEntity<Void> enableCurrency(@RequestBody Currency currency) {
        currencyService.updateDefaultCurrency(currency);
        return ServerResponseEntity.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('platform:currency:delete')")
    @Operation(summary = "删除货币汇率表", description = "根据货币汇率表id删除货币汇率表")
    @Parameter(name = "id", description = "货币汇率表", required = true)
    public ServerResponseEntity<Void> removeById(@PathVariable Long id) {
        currencyService.removeCurrency(id);
        return ServerResponseEntity.success();
    }

    @GetMapping("/getDefaultCurrency")
    @Operation(summary = "获取商城配置的默认货币")
    public ServerResponseEntity<Currency> getDefaultCurrency() {
        Currency currency = currencyService.getOne(new LambdaQueryWrapper<Currency>().eq(Currency::getDefaultCurrency,1));
        return ServerResponseEntity.success(currency);
    }
}
