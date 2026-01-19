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
import com.yami.shop.bean.model.AttachFile;
import com.yami.shop.common.bean.Qiniu;
import com.yami.shop.common.bean.UploadType;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.ImgUploadUtil;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.service.AttachFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 文件上传 controller
 * @author lgh
 *
 */
@RestController
@RequestMapping("/admin/file")
@Tag(name = "上传文件记录接口")
@AllArgsConstructor
public class FileController {

    private final AttachFileService attachFileService;
    @Autowired
    private Qiniu qiniu;
    @Autowired
    private ImgUploadUtil imgUploadUtil;

    @GetMapping("/attachFilePage")
    @Operation(summary = "根据参数分页获取文件记录列表" , description = "根据参数分页获取文件记录列表")
    @PreAuthorize("@pms.hasPermission('admin:file:page')")
    public ServerResponseEntity<IPage<AttachFile>> attachFilePage(PageParam<AttachFile> page, AttachFile attachFile) {
        attachFile.setShopId(Constant.PLATFORM_SHOP_ID);
        IPage<AttachFile> attachFilePage = attachFileService.getPage(page,attachFile);
        return ServerResponseEntity.success(attachFilePage);
    }

    @DeleteMapping("/deleteFile/{fileId}")
    @Operation(summary = "根据文件记录id删除文件" , description = "根据文件记录id删除文件")
    @PreAuthorize("@pms.hasPermission('admin:file:delete')")
    public ServerResponseEntity<Void> deleteFile(@PathVariable("fileId") Long fileId){
        AttachFile attachFile = attachFileService.getById(fileId);
        if (Objects.isNull(attachFile)) {
            return ServerResponseEntity.success();
        }
        attachFileService.deleteFile(attachFile.getFilePath());
        return ServerResponseEntity.success();
    }

    @PutMapping("/updateFile")
    @Operation(summary = "更改文件记录信息" , description = "更改文件记录信息")
    @PreAuthorize("@pms.hasPermission('admin:file:update')")
    public ServerResponseEntity<Boolean> updateFile(@RequestBody AttachFile attachFile) {
        if (Objects.isNull(attachFile.getFileName())){
            // 图片名称不能为空
            throw new YamiShopBindException("yami.img.name.exist");
        }
        attachFile.setShopId(Constant.PLATFORM_SHOP_ID);
        return ServerResponseEntity.success(attachFileService.updateFile(attachFile));
    }

    @DeleteMapping("/deleteByIds")
    @Operation(summary = "根据文件id列表批量删除文件记录" , description = "根据文件id列表批量删除文件记录")
    @PreAuthorize("@pms.hasPermission('admin:file:delete')")
    public ServerResponseEntity<Void> deleteByIds(@RequestBody List<Long> ids) {
        Long shopId = Constant.PLATFORM_SHOP_ID;
        attachFileService.deleteByIdsAndShopId(ids, shopId);
        return ServerResponseEntity.success();
    }

    @PutMapping("/batchMove")
    @PreAuthorize("@pms.hasPermission('admin:file:update')")
    @Operation(summary = "根据文件id列表与分组id批量移动文件" , description = "根据文件id列表与分组id批量移动文件")
    public ServerResponseEntity<Void> batchMove(@RequestBody AttachFile attachFile) {
        Long shopId = Constant.PLATFORM_SHOP_ID;
        attachFileService.batchMoveByShopIdAndIdsAndGroupId(shopId, attachFile);
        return ServerResponseEntity.success();
    }

    @GetMapping("/get_file_by_id")
    @Operation(summary = "根据文件id获取文件信息")
    @PreAuthorize("@pms.hasPermission('admin:file:info')")
    public ServerResponseEntity<AttachFile> getFileById(@RequestParam("fileId") Long fileId){
        AttachFile attachFile = attachFileService.getById(fileId);
        Long shopId = Constant.PLATFORM_SHOP_ID;
        if (!Objects.equals(shopId,attachFile.getShopId())){
            throw new YamiShopBindException("yami.file.isNot.shop");
        }
        return ServerResponseEntity.success(attachFile);
    }

    @PostMapping("/upload/element")
    public ServerResponseEntity<String> uploadElementFile(@RequestParam("file") MultipartFile file) throws IOException {
        if(file.isEmpty()){
            return ServerResponseEntity.success();
        }
        String fileName = attachFileService.uploadFile(file);
        return ServerResponseEntity.success(fileName);
    }

    @PostMapping("/upload/tinymceEditor")
    public ServerResponseEntity<String> uploadTinymceEditorImages(@RequestParam("editorFile") MultipartFile editorFile) throws IOException{
        String fileName =  attachFileService.uploadFile(editorFile);
        String data = "";
        if (Objects.equals(imgUploadUtil.getUploadType(), UploadType.LOCAL.value())) {
            data = imgUploadUtil.getUploadPath() + fileName;
        } else if (Objects.equals(imgUploadUtil.getUploadType(), UploadType.QINIU.value())) {
            data = qiniu.getResourcesUrl() + fileName;
        }
        return ServerResponseEntity.success(data);
    }
}
