package com.yami.shop.bean.app.dto;

import com.yami.shop.bean.model.Brand;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author: zsm
 * @date: 2023/3/13 11:17
 */
@Data
public class CategoryAndBrandInfoDto {

    @Schema(description = "子分类信息")
    List<CategoryDto> categoryInfo;

    @Schema(description = "品牌信息")
    List<Brand> brandInfo;
}
