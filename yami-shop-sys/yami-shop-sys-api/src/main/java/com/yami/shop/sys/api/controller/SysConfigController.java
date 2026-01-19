/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.sys.api.controller;

import com.yami.shop.bean.vo.DistributionConfigVO;
import com.yami.shop.common.bean.LangConfig;
import com.yami.shop.common.bean.LangItemConfig;
import com.yami.shop.common.bean.SysPayConfig;
import com.yami.shop.common.bean.SysServiceConfig;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.i18n.bean.I18nLangInfo;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.manager.impl.LangManager;
import com.yami.shop.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 系统配置信息
 * @author yami
 */
@RestController
@Tag(name = "系统配置信息")
@RequestMapping("/sys/config")
@AllArgsConstructor
public class SysConfigController {

    private final SysConfigService sysConfigService;
    private final LangManager langManager;

    @Operation(summary = "获取服务条款信息" , description = "获取服务条款信息")
    @GetMapping("/info/serviceTerms")
    public ServerResponseEntity<String> serviceTerms(){
        I18nLangInfo langInfo = I18nMessage.getI18nLang();
        String serviceTerms = sysConfigService.getValue(String.format(Constant.SERVICE_TERMS_CONFIG, langInfo.getCurrentLang().getLangId()));
        // 不存在则返回默认语言信息
        if (Objects.isNull(serviceTerms)) {
            serviceTerms = sysConfigService.getValue(String.format(Constant.SERVICE_TERMS_CONFIG, langInfo.getDefaultLang().getLangId()));
        }
        return ServerResponseEntity.success(serviceTerms);
    }

    @Operation(summary = "获取隐私策略信息" , description = "获取隐私策略信息")
    @GetMapping("/info/servicePolicy")
    public ServerResponseEntity<String> servicePolicy(){
        I18nLangInfo langInfo = I18nMessage.getI18nLang();
        String policy = sysConfigService.getValue(String.format(Constant.SERVICE_POLICY_CONFIG, langInfo.getCurrentLang().getLangId()));
        // 不存在则返回默认语言信息
        if (Objects.isNull(policy)) {
            policy = sysConfigService.getValue(String.format(Constant.SERVICE_POLICY_CONFIG, langInfo.getDefaultLang().getLangId()));
        }
        return ServerResponseEntity.success(policy);
    }

    @Operation(summary = "获取系统支付开关" , description = "获取系统支付开关")
    @GetMapping("/info/getSysPaySwitch")
    public ServerResponseEntity<SysPayConfig> getSysPaySwitch(){
        SysPayConfig sysPayConfig = sysConfigService.getSysConfigObject(Constant.PAY_SWITCH_CONFIG, SysPayConfig.class);
        return ServerResponseEntity.success(sysPayConfig);
    }

    @Operation(summary = "获取系统服务条例开关" , description = "获取系统服务条例开关")
    @GetMapping("/info/getSysServiceSwitch")
    public ServerResponseEntity<SysServiceConfig> getSysServiceSwitch() {
        SysServiceConfig sysServiceConfig = sysConfigService.getSysConfigObject(Constant.SERVICE_SWITCH_CONFIG, SysServiceConfig.class);
        return ServerResponseEntity.success(sysServiceConfig);
    }

    @GetMapping("/lang")
    @Operation(summary = "国际化语言配置信息", description = "国际化语言配置信息")
    public ServerResponseEntity<LangConfig> getLangConfig(){
        LangConfig langConfig = langManager.getLangConfig();
        List<LangItemConfig> langItemConfigList = langConfig.getLangItemList().stream().filter(langItemConfig -> Objects.equals(langItemConfig.getLang(), langConfig.getLang())).collect(Collectors.toList());
        langConfig.getLangItemList().removeAll(langItemConfigList);
        langItemConfigList.addAll(langConfig.getLangItemList());
        langConfig.setLangItemList(langItemConfigList);
        return ServerResponseEntity.success(langConfig);
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
}
