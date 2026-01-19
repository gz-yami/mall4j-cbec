/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.api.controller;


import com.yami.shop.bean.enums.WebConfigTypeEnum;
import com.yami.shop.bean.vo.WebConfigVO;
import com.yami.shop.common.annotation.SysLog;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.service.WebConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SJL
 * @date 2021-02-20 09:44:42
 */
@RestController
@AllArgsConstructor
@RequestMapping("/webConfig")
@Tag(name = "网站配置接口")
public class WebConfigController {

    private final WebConfigService webConfigService;

    @GetMapping("/getPcWebConfig")
    @Operation(summary = "获取当前激活的PC端网站配置")
    public ServerResponseEntity<WebConfigVO> getWebConfig() {
        return ServerResponseEntity.success(webConfigService.getActivityWebConfig(WebConfigTypeEnum.PC.value()));
    }

    @GetMapping("/getUniWebConfig")
    @Operation(summary = "获取当前激活的H5端网站配置")
    public ServerResponseEntity<WebConfigVO> getUniWebConfig() {
        return ServerResponseEntity.success(webConfigService.getActivityWebConfig(WebConfigTypeEnum.H5.value()));
    }

    @GetMapping("/getStationWebConfig")
    @Operation(summary = "获取自提点端网站配置")
    public ServerResponseEntity<WebConfigVO> getStationWebConfig() {
        return ServerResponseEntity.success(webConfigService.getActivityWebConfig(WebConfigTypeEnum.STATION.value()));
    }


    @SysLog("获取配置信息")
    @GetMapping("/info/{key}")
    @Operation(summary = "获取配置信息" , description = "获取配置信息")
    @Parameter(name = "key", description = "参数名" )
    public ServerResponseEntity<String> info(@PathVariable("key") String key) {
        return ServerResponseEntity.success(webConfigService.getValue(key));
    }
}
