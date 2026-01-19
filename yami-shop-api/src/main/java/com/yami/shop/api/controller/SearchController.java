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


import com.yami.shop.bean.dto.HotSearchDto;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.service.HotSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author LGH
 */
@RestController
@RequestMapping("/search")
@Tag(name = "搜索接口")
@AllArgsConstructor
public class SearchController {

    private final HotSearchService hotSearchService;

    @GetMapping("/hotSearchByShopId")
    @Operation(summary = "查看店铺热搜" , description = "根据店铺id,热搜数量获取热搜")
    public ServerResponseEntity<List<HotSearchDto>> hotSearchByShopId() {
        List<HotSearchDto> list = hotSearchService.getHotSearchDtoByshopId(Constant.PLATFORM_SHOP_ID);
        return ServerResponseEntity.success(list);
    }

    @GetMapping("/hotSearch")
    @Operation(summary = "查看全局热搜" , description = "根据店铺id,热搜数量获取热搜")
    @Parameter(name = "type", description = "热搜类型 1:商品 2:店铺" , required = true)
    public ServerResponseEntity<List<HotSearchDto>> hotSearch(Integer type) {
        List<HotSearchDto> list = hotSearchService.getHotSearchDtoByshopId(Constant.PLATFORM_SHOP_ID);
        List<HotSearchDto> result = list.stream().filter(hotSearchDto -> Objects.equals(hotSearchDto.getType(), type)).collect(Collectors.toList());
        return ServerResponseEntity.success(result);
    }
}
