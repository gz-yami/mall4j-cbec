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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.app.dto.SkuDto;
import com.yami.shop.bean.app.vo.SkuVO;
import com.yami.shop.bean.model.Sku;
import com.yami.shop.bean.param.ProductParam;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.enums.StatusEnum;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.service.SkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author person
 */
@RestController
@RequestMapping("/sku")
@Tag(name = "sku规格接口")
@AllArgsConstructor
public class SkuController {

    private final SkuService skuService;

    @GetMapping("/getAllSkuList")
    @Operation(summary = "通过prodId获取全部的规格列表" , description = "通过prodId获取全部的规格列表")
    @Parameter(name = "prodId", description = "商品id" )
    @PreAuthorize("@pms.hasPermission('sku:list')")
    public ServerResponseEntity<List<Sku>> getSkuListByProdId(Long prodId) {
        List<Sku> skus = skuService.listSkuAndSkuStock(prodId, null);
        return ServerResponseEntity.success(skus);
    }

    @GetMapping("/getEnableSkuList")
    @Operation(summary = "通过prodId获取启用的规格列表" , description = "通过prodId获取启用的规格列表")
    @Parameter(name = "prodId", description = "商品id" )
    @PreAuthorize("@pms.hasPermission('sku:enableList')")
    public ServerResponseEntity<List<Sku>> getEnableSkuList(Long prodId) {
        List<Sku> skus = skuService.listPutOnSkuAndSkuStock(prodId, null);
        skus = skus.stream().filter(item -> Objects.equals(item.getStatus(), StatusEnum.ENABLE.value())).collect(Collectors.toList());
        return ServerResponseEntity.success(skus);
    }

    @GetMapping("/pageSku")
    @Operation(summary = "分页查询sku信息" , description = "分页查询sku信息")
    @PreAuthorize("@pms.hasPermission('sku:page')")
    public ServerResponseEntity<IPage<Sku>> pageSku(PageParam<Sku> page, ProductParam product) {
        product.setLang(I18nMessage.getLang());
        product.setShopId(Constant.PLATFORM_SHOP_ID);
        IPage<Sku> skuPage = skuService.pageSku(page, product);
        return ServerResponseEntity.success(skuPage);
    }


    @GetMapping("/stockWarningCount")
    @Operation(summary = "获取库存预警数量" , description = "获取库存预警数量")
    public ServerResponseEntity<Long> stockWarningCount(){
        return ServerResponseEntity.success(skuService.stockWarningCount(Constant.PLATFORM_SHOP_ID));
    }

}
