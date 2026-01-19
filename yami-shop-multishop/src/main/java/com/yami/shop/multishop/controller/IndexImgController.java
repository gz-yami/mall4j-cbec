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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.model.IndexImg;
import com.yami.shop.bean.model.Product;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.service.AttachFileService;
import com.yami.shop.service.IndexImgService;
import com.yami.shop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Objects;

/**
 * @author lgh on 2018/11/26.
 */
@RestController
@RequestMapping("/platform/indexImg")
@Tag(name = "轮播图")
@AllArgsConstructor
public class IndexImgController {

    private final IndexImgService indexImgService;
    private final ProductService productService;
    private final AttachFileService attachFileService;


    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('platform:indexImg:page')")
    @Operation(summary = "分页获取" , description = "分页获取")
    public ServerResponseEntity<IPage<IndexImg>> page(IndexImg indexImg, PageParam<IndexImg> page) {
        IPage<IndexImg> indexImgPage = indexImgService.page(page,
                new LambdaQueryWrapper<IndexImg>()
                        .eq(indexImg.getStatus() != null,IndexImg::getStatus,indexImg.getStatus())
                        .eq(IndexImg::getShopId, Constant.PLATFORM_SHOP_ID)
                        .eq(!Objects.isNull(indexImg.getImgType()),IndexImg::getImgType,indexImg.getImgType())
                        .orderByDesc(IndexImg::getSeq)
                        .orderByDesc(IndexImg::getStatus)
                        .orderByAsc(IndexImg::getImgType));
        return ServerResponseEntity.success(indexImgPage);
    }

    @GetMapping("/info/{imgId}")
    @PreAuthorize("@pms.hasPermission('platform:indexImg:info')")
    @Operation(summary = "获取信息" , description = "获取信息")
    @Parameter(name = "imgId", description = "轮播图id" )
    public ServerResponseEntity<IndexImg> info(@PathVariable("imgId") Long imgId) {
        Long shopId = Constant.PLATFORM_SHOP_ID;
        IndexImg indexImg = indexImgService.getOne(new LambdaQueryWrapper<IndexImg>().eq(IndexImg::getShopId, shopId).eq(IndexImg::getImgId, imgId));
        if (Objects.nonNull(indexImg.getRelation())) {
            Product product = productService.getProductByProdId(indexImg.getRelation());
            if (product !=null) {
                indexImg.setPic(product.getPic());
                indexImg.setProdName(product.getProdName());
            }
        }
        return ServerResponseEntity.success(indexImg);
    }

    @PostMapping
    @PreAuthorize("@pms.hasPermission('platform:indexImg:save')")
    @Operation(summary = "保存" , description = "保存")
    public ServerResponseEntity<Void> save(@RequestBody @Valid IndexImg indexImg) {
        Long shopId = Constant.PLATFORM_SHOP_ID;
        indexImg.setShopId(shopId);
        indexImg.setUploadTime(new Date());
        long count = indexImgService.count(new LambdaQueryWrapper<IndexImg>()
                .eq(IndexImg::getImgType, indexImg.getImgType())
                .eq(IndexImg::getShopId, shopId)
        );
        if (count >= Constant.MAX_INDEX_IMG_NUM) {
            // 该平台的轮播图已达到最大数量，不能再进行新增操作
            throw new YamiShopBindException("yami.index.img.reached.limit");
        }
        indexImgService.save(indexImg);
        indexImgService.removeIndexImgCacheByShopId(shopId);
        return ServerResponseEntity.success();
    }

    @PutMapping
    @PreAuthorize("@pms.hasPermission('platform:indexImg:update')")
    @Operation(summary = "修改" , description = "修改")
    public ServerResponseEntity<Void> update(@RequestBody @Valid IndexImg indexImg) {
        IndexImg dbIndexImg = indexImgService.getById(indexImg.getImgId());
        // 是否校验数量（图片类型不一致时校验数量是否达到最大）
        boolean isCheckNum = dbIndexImg == null
                || !Objects.equals(dbIndexImg.getImgType(), indexImg.getImgType());
        if (isCheckNum) {
            long count = indexImgService.count(new LambdaQueryWrapper<IndexImg>()
                    .eq(IndexImg::getImgType, indexImg.getImgType())
                    .eq(IndexImg::getShopId, indexImg.getShopId())
            );
            if (count >= Constant.MAX_INDEX_IMG_NUM) {
                // 该平台的轮播图已达到最大数量，不能再进行新增操作
                throw new YamiShopBindException("yami.index.img.reached.limit");
            }
        }
        indexImgService.saveOrUpdate(indexImg);
        // 移除缓存
        indexImgService.removeIndexImgCacheByShopId(Constant.PLATFORM_SHOP_ID);
        return ServerResponseEntity.success();
    }

    @DeleteMapping
    @PreAuthorize("@pms.hasPermission('platform:indexImg:delete')")
    @Operation(summary = "删除" , description = "删除")
    @Parameter(name = "ids", description = "轮播图id" )
    public ServerResponseEntity<Void> delete(@RequestBody Long[] ids) {
        indexImgService.deleteIndexImgsByIds(ids, Constant.PLATFORM_SHOP_ID);
        // 移除缓存
        indexImgService.removeIndexImgCacheByShopId(Constant.PLATFORM_SHOP_ID);
        return ServerResponseEntity.success();
    }

}
