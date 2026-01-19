/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.sys.multishop.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.vo.DistributionConfigVO;
import com.yami.shop.common.annotation.SysLog;
import com.yami.shop.common.bean.SysConfig;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 系统配置信息
 *
 * @author yami
 */
@RestController
@Tag(name = "系统配置信息")
@RequestMapping("/sys/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService sysConfigService;

    @Value("${yami.expose.permission:}")
    private Boolean permission;

    @GetMapping("/signingConfig")
    @Operation(summary = "获取签约类目审核配置", description = "获取签约类目审核配置")
    public ServerResponseEntity<String> getSigningConfig() {
        return ServerResponseEntity.success(sysConfigService.getValue(Constant.PROD_SWITCH_CONFIG));
    }

    @GetMapping("/getDistribution")
    @Operation(summary = "获取分销开启状态", description = "获取分销开启状态")
    public ServerResponseEntity<Boolean> getDistribution() {
        DistributionConfigVO distributionConfigVO = sysConfigService.getSysConfigObject(Constant.DISTRIBUTION_CONFIG, DistributionConfigVO.class);
        if (Objects.isNull(distributionConfigVO)) {
            return ServerResponseEntity.success(Boolean.FALSE);
        }
        return ServerResponseEntity.success(distributionConfigVO.getDistributionSwitch() == 1);
    }

    @GetMapping("/page")
    @Operation(summary = "所有配置列表" , description = "所有配置列表")
    @Parameter(name = "paramKey", description = "参数名" )
    @PreAuthorize("@pms.hasPermission('sys:config:page')")
    public ServerResponseEntity<IPage<SysConfig>> page(String paramKey, PageParam<SysConfig> page){
        if (BooleanUtil.isFalse(permission)) {
            return ServerResponseEntity.success();
        }
        IPage<SysConfig> sysConfigs = sysConfigService.page(page, new LambdaQueryWrapper<SysConfig>().like(StrUtil.isNotBlank(paramKey),SysConfig::getParamKey,paramKey));
        return ServerResponseEntity.success(sysConfigs);
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "配置信息" , description = "配置信息")
    @Parameter(name = "id", description = "配置id" )
    @PreAuthorize("@pms.hasPermission('sys:config:info')")
    public ServerResponseEntity<SysConfig> info(@PathVariable("id") Long id){
        if (BooleanUtil.isFalse(permission)) {
            return ServerResponseEntity.success();
        }
        SysConfig config = sysConfigService.getById(id);
        return ServerResponseEntity.success(config);
    }

    @SysLog("保存配置")
    @PostMapping
    @Operation(summary = "保存配置" , description = "保存配置")
    @PreAuthorize("@pms.hasPermission('sys:config:save')")
    public ServerResponseEntity<Void> save(@RequestBody @Valid SysConfig config){
        if (BooleanUtil.isFalse(permission)) {
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.operate.auth"));
        }
        if (sysConfigService.count(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getParamKey, config.getParamKey())) > 0) {
            // 系统已存在相同配置的key
            throw new YamiShopBindException("yami.same.key");
        }
        sysConfigService.save(config);
        // 清除系统配置的缓存
        sysConfigService.removeSysConfig(config.getParamKey());
        return ServerResponseEntity.success();
    }

    @SysLog("修改配置")
    @PutMapping
    @Operation(summary = "修改配置" , description = "修改配置")
    @PreAuthorize("@pms.hasPermission('sys:config:update')")
    public ServerResponseEntity<Void> update(@RequestBody @Valid SysConfig config){
        if (BooleanUtil.isFalse(permission)) {
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.operate.auth"));
        }

        SysConfig dbSysConfig = sysConfigService.getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getParamKey, config.getParamKey()));

        if (dbSysConfig != null && !Objects.equals(dbSysConfig.getParamKey(),config.getParamKey())) {
            // 系统已存在相同配置的key
            throw new YamiShopBindException("yami.same.key");
        }

        sysConfigService.updateById(config);
        // 清除系统配置的缓存
        sysConfigService.removeSysConfig(config.getParamKey());
        return ServerResponseEntity.success();
    }

    @SysLog("删除配置")
    @DeleteMapping
    @Operation(summary = "删除配置" , description = "删除配置")
    @Parameter(name = "configIds", description = "配置id列表" )
    @PreAuthorize("@pms.hasPermission('sys:config:delete')")
    public ServerResponseEntity<Void> delete(@RequestBody Long[] configIds){
        if (BooleanUtil.isFalse(permission)) {
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.operate.auth"));
        }
        if (ArrayUtil.isEmpty(configIds)) {
            return ServerResponseEntity.success();
        }
        List<SysConfig> list = sysConfigService.list(new LambdaQueryWrapper<SysConfig>().in(SysConfig::getId, configIds));
        // 清除系统配置的缓存
        sysConfigService.deleteBatch(configIds);
        for (SysConfig sysConfig : list) {
            sysConfigService.removeSysConfig(sysConfig.getParamKey());
        }
        return ServerResponseEntity.success();
    }
}
