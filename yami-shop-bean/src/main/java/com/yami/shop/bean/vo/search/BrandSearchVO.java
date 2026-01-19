/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.vo.search;

import com.yami.shop.bean.bo.BrandLangBO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author FrozenWatermelon
 */
@Data
public class BrandSearchVO {

    @Schema(description = "品牌名称" )
    private String brandName;

    @Schema(description = "品牌id" )
    private Long brandId;

    @Schema(description = "品牌图片" )
    private String brandImg;

    /**
     * 品牌名称列表
     */
    private List<BrandLangBO> brandLangList;
}
