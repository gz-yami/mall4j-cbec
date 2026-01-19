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

import com.yami.shop.bean.dto.AreaDto;
import com.yami.shop.bean.model.Area;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.service.AreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lgh on 2018/10/26.
 */
@RestController
@RequestMapping("/p/area")
@Tag(name = "省市区接口")
@AllArgsConstructor
public class AreaController {

    private final AreaService areaService;

    /**
     * 分页获取
     */
    @GetMapping("/listByPid")
    @Operation(summary = "获取省市区信息" , description = "根据省市区的pid获取地址信息")
    @Parameters({
            @Parameter(name = "level", description = "省市区的level(level为1获取所有省份)" ),
            @Parameter(name = "pid", description = "省市区的pid" , required = true)
    })
    public ServerResponseEntity<List<Area>> listByPid(Long pid, Integer level) {
        List<Area> list = areaService.listByPid(pid, level);
        return ServerResponseEntity.success(list);
    }

    @GetMapping("/getAreaListInfo")
    @Operation(summary = "获取省市区信息" , description = "获取完整层级的地址信息")
    public ServerResponseEntity<List<AreaDto>> getAreaListInfo() {
        List<AreaDto> areaDtoList = areaService.getAreaListInfo();
        return ServerResponseEntity.success(areaDtoList);
    }

}
