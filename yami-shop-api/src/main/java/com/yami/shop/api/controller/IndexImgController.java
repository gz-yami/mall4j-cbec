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

import com.yami.shop.bean.app.dto.IndexImgDto;
import com.yami.shop.bean.model.IndexImg;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.service.IndexImgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LGH
 */
@RestController
@Tag(name = "首页轮播图接口")
@AllArgsConstructor
public class IndexImgController {

    private final IndexImgService indexImgService;

    @GetMapping("/indexImgs")
    @Operation(summary = "首页轮播图" , description = "获取首页轮播图列表信息")
    @Parameter(name = "imgType", description = "图片类型" )
    public ServerResponseEntity<List<IndexImgDto>> indexImgs(@RequestParam(required = false, defaultValue = "0") Integer imgType) {
        List<IndexImg> indexImgList = indexImgService.listIndexImgsByShopId(Constant.PLATFORM_SHOP_ID, imgType);
        List<IndexImgDto> indexImgDtos = BeanUtil.mapAsList(indexImgList, IndexImgDto.class);
        return ServerResponseEntity.success(indexImgDtos);
    }
}
