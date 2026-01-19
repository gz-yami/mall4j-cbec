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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author FrozenWatermelon
 */
@Data
public class ShopSearchVO {

    @Schema(description = "店铺名称 搜索华为的时候，可以把华为的旗舰店搜索出来" )
    private String shopName;

    @Schema(description = "店铺id" )
    private Long shopId;

    @Schema(description = "店铺logo" )
    private String shopLogo;

    @Schema(description = "店铺商品总销量" )
    private Integer saleNum;

    @Schema(description = "店铺类型1自营店 2普通店" )
    private Integer type;

    @Schema(description = "用户总收藏量" )
    private Integer collectionNum;
}
