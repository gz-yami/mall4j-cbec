package com.yami.shop.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 预签名urlVO
 * @author gaozijie
 * @date 2023-10-25
 */
@Data
public class PreSignUrlVO {

    @Schema(description = "文件id")
    private Long fileId;

    @Schema(description = "原文件名")
    private String oriFileName;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "预签名url")
    private String preSignUrl;
}
