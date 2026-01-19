package com.yami.shop.bean.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author gaozijie
 * @date 2023-10-25
 */
@Data
public class UploadSuccessDTO {

    @Schema(description = "文件id")
    private Long fileId;

    @Schema(description = "文件分组id")
    private Long attachFileGroupId;

    @Schema(description = "文件类型(1:图片, 2:视频, 3:文件)[客服聊天，用户端文件上传不上传此参数]")
    private Integer type;

    @Schema(description = "文件大小")
    private Integer fileSize;

    @Hidden
    @Schema(description = "店铺id")
    private Long shopId;
}
