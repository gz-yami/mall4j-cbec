/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Yami
 */
@Schema(description = "热搜数据")
@Data
public class HotSearchDto implements Serializable {

    @Schema(description = "热搜id" )
    private Long hotSearchId;

    @Schema(description = "标题" )
    private String title;

    @Schema(description = "内容" )
    private String content;

    @Schema(description = "状态 默认是1,0为下线" )
    private Integer status;

    @Schema(description = "热搜类型 1:商品 2:店铺" )
    private Integer type;

    @Schema(description = "顺序" )
    private Integer seq;

    @Schema(description = "店铺id" )
    private Long shopId;



}
