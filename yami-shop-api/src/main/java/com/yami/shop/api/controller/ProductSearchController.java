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

import com.yami.shop.bean.param.EsProductParam;
import com.yami.shop.bean.vo.search.EsProductSearchVO;
import com.yami.shop.bean.vo.search.ProductSearchVO;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.EsPageVO;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.search.common.param.EsPageParam;
import com.yami.shop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 商品搜索
 *
 * @author FrozenWatermelon
 * @date 2020/11/16
 */
@RestController("appSearchSpuController")
@RequestMapping("/search")
@Tag(name = "商品搜索接口")
@RequiredArgsConstructor
public class ProductSearchController {


    private final ProductService productService;

    @GetMapping("/page")
    @Operation(summary = "商品搜索接口（仅商品信息）" , description = "商品搜索接口")
    public ServerResponseEntity<EsPageVO<ProductSearchVO>> page(@Valid PageParam pageParam, EsProductParam productParam) {
        EsPageVO<ProductSearchVO> searchPage = productService.pageProduct(pageParam, productParam);
        return ServerResponseEntity.success(searchPage);
    }


}
