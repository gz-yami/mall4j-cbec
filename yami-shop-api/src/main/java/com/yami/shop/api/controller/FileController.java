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

import com.yami.shop.bean.dto.UploadSuccessDTO;
import com.yami.shop.bean.model.AttachFile;
import com.yami.shop.bean.vo.PreSignUrlVO;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.service.AttachFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件上传 controller
 *
 * @author lgh
 */

@RestController
@RequestMapping("/p/file")
@AllArgsConstructor
public class FileController {

    private final AttachFileService attachFileService;

    @GetMapping("/get_file_by_id")
    @Operation(summary = "根据文件id获取文件信息")
    public ServerResponseEntity<AttachFile> getFileById(@RequestParam("fileId") Long fileId) {
        AttachFile attachFile = attachFileService.getById(fileId);
        return ServerResponseEntity.success(attachFile);
    }

    @GetMapping("/getPreSignUrl")
    @Parameters({
            @Parameter(name = "fileName", description = "文件名"),
            @Parameter(name = "isImFile", description = "是否为客服聊天文件")
    })
    @Operation(summary = "获取预签名url(s3协议)")
    public ServerResponseEntity<PreSignUrlVO> getPreSignUrl(String fileName, Boolean isImFile) {
        PreSignUrlVO preSingUrlVO = attachFileService.getPreSingUrl(null, fileName, isImFile);
        return ServerResponseEntity.success(preSingUrlVO);
    }

    @GetMapping("/getBatchPreSignUrl")
    @Parameters({
            @Parameter(name = "fileNames", description = "文件名集合"),
            @Parameter(name = "isImFile", description = "是否为客服聊天文件")
    })
    @Operation(summary = "批量获取预签名url(s3协议)")
    public ServerResponseEntity<List<PreSignUrlVO>> getBatchPreSignUrl(List<String> fileNames, Boolean isImFile) {
        List<PreSignUrlVO> preSignUrlVOList = attachFileService.getBatchPreSignUrl(null, fileNames, isImFile);
        return ServerResponseEntity.success(preSignUrlVOList);
    }

    @PutMapping("/uploadSuccess")
    @Operation(summary = "上传成功(s3协议)(图片上传完调用此接口)")
    public ServerResponseEntity<Void> uploadSuccess(@RequestBody List<UploadSuccessDTO> uploadSuccessDTOList) {
        // 图片上传由前端完成，需要前端上传完图片后调用此接口，来修改数据库数据
        attachFileService.uploadSuccess(uploadSuccessDTOList);
        return ServerResponseEntity.success();
    }

    @PutMapping("/uploadSuccessToPlatform")
    @Operation(summary = "上传成功(保存至平台端)")
    public ServerResponseEntity<Void> uploadSuccessToPlatform(@RequestBody List<UploadSuccessDTO> uploadSuccessDTOList) {
        for (UploadSuccessDTO uploadSuccessDTO : uploadSuccessDTOList) {
            uploadSuccessDTO.setShopId(Constant.PLATFORM_SHOP_ID);
        }
        attachFileService.uploadSuccess(uploadSuccessDTOList);
        return ServerResponseEntity.success();
    }
}
