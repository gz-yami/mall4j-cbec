/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Yami
 */
@Data
public class CategoryVO implements Serializable {

    @TableId
    @Schema(description = "类目ID" )
    private Long categoryId;

    @Schema(description = "店铺id" )
    private Long shopId;

    @Schema(description = "父节点" )
    private Long parentId;

    @Schema(description = "产品类目名称" )
    private String categoryName;

    @Schema(description = "类目图标" )
    private String icon;

    @Schema(description = "类目的显示图片" )
    private String pic;

    @Schema(description = "排序" )
    private Integer seq;

    @Schema(description = "分类扣率" )
    private Double deductionRate;

    @Schema(description = "默认是1，表示正常状态,0为下线状态" )
    private Integer status;

    @Schema(description = "记录时间" )
    private Date recTime;

    @Schema(description = "分类层级" )
    private Integer grade;

    @Schema(description = "更新时间" )
    private Date updateTime;

    @Schema(description = "上级/子分类列表" )
    private List<ParentCategoryVO> categories;
}
