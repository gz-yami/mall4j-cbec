/*
 *
 *  * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *  *
 *  * https://www.mall4j.com/
 *  *
 *  * 未经允许，不可做商业用途！
 *  *
 *  * 版权所有，侵权必究！
 *
 */

package com.yami.shop.api.controller;

import com.yami.shop.bean.model.HotCity;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.service.HotCityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @title: CityController
 * @Author Chiley
 * @Date: 2022/11/29 15:31
 */
@RestController
@RequestMapping("/hotCity")
@Tag(name = "热门城市接口")
@AllArgsConstructor
public class HotCityController {

    private final HotCityService hotCityService;

    @GetMapping("/listHotCity")
    @Operation(summary = "获取热门城市", description =  "获取热门城市")
    public ServerResponseEntity<List<HotCity>> listCityInfo() {
        return ServerResponseEntity.success(hotCityService.listHotCity());
    }

}
