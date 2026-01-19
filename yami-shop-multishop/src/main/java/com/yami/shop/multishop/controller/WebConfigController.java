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


import cn.hutool.core.util.BooleanUtil;
import com.yami.shop.bean.enums.WebConfigTypeEnum;
import com.yami.shop.bean.model.WebConfig;
import com.yami.shop.bean.vo.WebConfigVO;
import com.yami.shop.common.annotation.SysLog;
import com.yami.shop.common.bean.SysConfig;
import com.yami.shop.common.bean.SysServiceConfig;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.service.SysConfigService;
import com.yami.shop.service.WebConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author SJL
 * @date 2021-02-20 09:44:42
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sys/webConfig")
@Tag(name = "系统配置接口")
public class WebConfigController {

    private final SysConfigService sysConfigService;

    private final WebConfigService webConfigService;
    @Value("${yami.expose.operation.auth:}")
    private Boolean permission;


    @SysLog("保存配置")
    @PostMapping("/save")
    @Operation(summary = "保存配置" , description = "保存配置")
    @PreAuthorize("@pms.hasPermission('sys:webConfig:save')")
    public ServerResponseEntity<Void> save(@RequestBody @Valid SysConfig sysConfig) {
        if (BooleanUtil.isFalse(permission)) {
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.operate.auth"));
        }
        webConfigService.saveWebConfig(sysConfig);
        return ServerResponseEntity.success();
    }

    /**
     * 获取当前激活的后台端网站配置
     *
     * @return
     */
    @GetMapping("/getActivity")
    @Operation(summary = "获取当前激活的后台端网站配置" , description = "获取当前激活的后台端网站配置")
    public ServerResponseEntity<WebConfigVO> getActivityWebConfig() {
        WebConfigVO webConfig = webConfigService.getActivityWebConfig(WebConfigTypeEnum.PLATFROM.value());
        SysServiceConfig sysConfigObject = sysConfigService.getSysConfigObject(Constant.SERVICE_SWITCH_CONFIG, SysServiceConfig.class);
        webConfig.setMerchantRegisterProtocolSwitch(sysConfigObject.getMerchantRegisterProtocolSwitch());
        return ServerResponseEntity.success(webConfig);
    }

    @SysLog("获取配置信息")
    @GetMapping("/info/{key}")
    @Operation(summary = "获取配置信息" , description = "获取配置信息")
    @Parameter(name = "key", description = "参数名" )
    @PreAuthorize("@pms.hasPermission('sys:webConfig:info')")
    public ServerResponseEntity<WebConfig> info(@PathVariable("key") String key) {
        WebConfig webConfig = webConfigService.info(key);
        return ServerResponseEntity.success(webConfig);
    }
}
