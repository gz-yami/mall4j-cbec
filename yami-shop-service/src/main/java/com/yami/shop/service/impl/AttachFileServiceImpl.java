/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.yami.shop.bean.dto.UploadSuccessDTO;
import com.yami.shop.bean.model.AttachFile;
import com.yami.shop.bean.model.AttachFileGroup;
import com.yami.shop.bean.vo.PreSignUrlVO;
import com.yami.shop.common.bean.Qiniu;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.util.ImgUploadUtil;
import com.yami.shop.common.util.Json;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.dao.AttachFileMapper;
import com.yami.shop.service.AttachFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author lgh
 * @date 2018/07/27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttachFileServiceImpl extends ServiceImpl<AttachFileMapper, AttachFile> implements AttachFileService {

    @Value("${yami.expose.operation.auth:}")
    private Boolean permission;

    @Autowired
    private AttachFileMapper attachFileMapper;
    @Autowired
    private UploadManager uploadManager;
    @Autowired
    private Qiniu qiniu;
    @Autowired
    private Auth auth;
    @Autowired
    private ImgUploadUtil imgUploadUtil;
    public final static String NORM_MONTH_PATTERN = "yyyy/MM/";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadFile(byte[] bytes, String originalName) throws IOException {
        String extName = FileUtil.extName(originalName);
        String imgName = FileUtil.mainName(originalName);
        String filePath = randomFilePath(extName);
        AttachFile attachFile = new AttachFile();
        attachFile.setFilePath(filePath);
        attachFile.setFileSize(bytes.length);
        attachFile.setFileType(extName);
        attachFile.setFileName(imgName);
        attachFile.setUploadTime(new Date());
        attachFileMapper.insert(attachFile);
        return filePath;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadFile(MultipartFile file) throws IOException {
        String extName = FileUtil.extName(file.getOriginalFilename());
        String fileName =DateUtil.format(new Date(), NORM_MONTH_PATTERN)+ IdUtil.simpleUUID() + "." + extName;
        AttachFile attachFile = new AttachFile();
        attachFile.setFilePath(fileName);
        attachFile.setFileSize(file.getBytes().length);
        attachFile.setFileType(extName);
        attachFile.setUploadTime(new Date());
        if (Objects.equals(imgUploadUtil.getUploadType(), 1)) {
            // 本地文件上传
            attachFileMapper.insert(attachFile);
            return imgUploadUtil.upload(file, fileName);
        } else {
            // 七牛云文件上传
            String upToken = auth.uploadToken(qiniu.getBucket(),fileName);
            Response response = uploadManager.put(file.getBytes(), fileName, upToken);
            Json.parseObject(response.bodyString(),  DefaultPutRet.class);
            return fileName;
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIdsAndShopId(List<Long> ids, Long shopId) {
        if (BooleanUtil.isFalse(permission)) {
            throw new YamiShopBindException("yami.no.auth");
        }
        List<AttachFile> attachFileList = attachFileMapper.getByIds(ids);
        // 获取文件的实际路径--数据库中保存的文件路径为： / + 实际的文件路径
        List<String> filePaths = attachFileList.stream().map(item -> {
            if (!Objects.equals(item.getShopId(), shopId)) {
                // 存在非本店铺下的文件，删除失败
                throw new YamiShopBindException("yami.attach.file.exception.deleteFail");
            }
            return item.getFilePath();
        }).toList();
        attachFileMapper.batchDeleteByIds(ids);
    }

    @Override
    public void batchMoveByShopIdAndIdsAndGroupId(Long shopId, AttachFile attachFile) {
        if (CollUtil.isEmpty(attachFile.getFileIds())) {
            // 文件id列表不能为空
            throw new YamiShopBindException("yami.attach.file.exception.idsNotNull");
        }
        Long attachFileGroupId = attachFile.getAttachFileGroupId();

        attachFileMapper.batchMoveByShopIdAndIdsAndGroupId(shopId, attachFile.getFileIds(), attachFileGroupId);
    }

    @Override
    public String getfileTypeByfilePath(String filePath) {
        return attachFileMapper.getfileTypeByfilePath(filePath);
    }

    private String randomFilePath(String fileType) {
        return DateUtil.format(new Date(), NORM_MONTH_PATTERN) + IdUtil.simpleUUID() + "." + fileType;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(String filePath) {
        if (BooleanUtil.isFalse(permission)) {
            throw new YamiShopBindException("yami.no.auth");
        }
        attachFileMapper.delete(new LambdaQueryWrapper<AttachFile>().eq(AttachFile::getFilePath, filePath));
    }

    @Override
    public Boolean updateFile(AttachFile attachFile) {
        return attachFileMapper.updateFile(attachFile);
    }

    @Override
    public IPage<AttachFile> getPage(PageParam<AttachFile> page, AttachFile attachFile) {
        return attachFileMapper.getPage(page, attachFile);
    }

    @Override
    public PreSignUrlVO getPreSingUrl(Long shopId, String fileName, Boolean isImFile) {
        this.checkFileName(fileName);
        // 构建文件类
        AttachFile attachFile = this.buildAttachFile(shopId, fileName, isImFile);
        // 插入数据库（url生成成功后再插入）
        attachFileMapper.insert(attachFile);
        // 返回信息
        PreSignUrlVO preSignUrlVO = new PreSignUrlVO();
        preSignUrlVO.setOriFileName(fileName);
        preSignUrlVO.setFileId(attachFile.getFileId());
        preSignUrlVO.setFilePath(attachFile.getFilePath());
        return preSignUrlVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PreSignUrlVO> getBatchPreSignUrl(Long shopId, List<String> fileNames, Boolean isImFile) {
        if (CollectionUtils.isEmpty(fileNames)) {
            return new ArrayList<>();
        }
        // 构建文件类集合
        List<AttachFile> attachFiles = new ArrayList<>(fileNames.size());
        List<String> filePaths = new ArrayList<>(fileNames.size());
        AttachFile attachFile;
        for (String fileName : fileNames) {
            this.checkFileName(fileName);
            attachFile = this.buildAttachFile(shopId, fileName, isImFile);
            attachFiles.add(attachFile);
            filePaths.add(attachFile.getFilePath());
        }
        // 批量插入数据库（url生成成功后再插入）
        this.saveBatch(attachFiles);
        // 返回消息
        List<PreSignUrlVO> preSignUrlVOList= new ArrayList<>(fileNames.size());
        PreSignUrlVO preSignUrlVO;
        for (int i=0; i<fileNames.size(); i++) {
            preSignUrlVO = new PreSignUrlVO();
            preSignUrlVO.setOriFileName(fileNames.get(i));
            preSignUrlVO.setFileId(attachFiles.get(i).getFileId());
            preSignUrlVO.setFilePath(attachFiles.get(i).getFilePath());
            preSignUrlVOList.add(preSignUrlVO);
        }
        return preSignUrlVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadSuccess(List<UploadSuccessDTO> uploadSuccessDTOList) {
        if (CollectionUtils.isEmpty(uploadSuccessDTOList)) {
            return;
        }
        List<AttachFile> attachFiles = new ArrayList<>(uploadSuccessDTOList.size());
        AttachFile attachFile;
        for (UploadSuccessDTO uploadSuccessDTO : uploadSuccessDTOList) {
            attachFile = new AttachFile();
            BeanUtils.copyProperties(uploadSuccessDTO, attachFile);
            attachFile.setUploadTime(new Date());
            attachFile.setIsUpload(1);
            attachFiles.add(attachFile);
        }
        this.updateBatchById(attachFiles);
    }

    /**
     * 构建附件对象
     * @param shopId 店铺id
     * @param fileName 文件名
     * @param isImFile 是否为客服聊天对象
     * @return 附件对象
     */
    private AttachFile buildAttachFile(Long shopId, String fileName, Boolean isImFile) {
        AttachFile attachFile = new AttachFile();
        attachFile.setShopId(shopId);
        String extName = FileUtil.extName(fileName);
        attachFile.setFileType(extName);
        attachFile.setFilePath(randomFilePath(extName));
        attachFile.setFileName(FileUtil.mainName(fileName));
        attachFile.setUploadTime(new Date());
        attachFile.setIsUpload(0);
        if (BooleanUtil.isTrue(isImFile)) {
            // 客服聊天文件用'im'文件夹装起来
            attachFile.setFilePath("im/" + attachFile.getFilePath());
        }
        return attachFile;
    }

    /**
     * 校验文件名
     * @param fileName 文件名
     */
    private void checkFileName(String fileName) {
        // 只允许特定文件后缀名上传
        String extName = FileUtil.extName(fileName).toLowerCase();
        if (ObjectUtils.isEmpty(extName) ||
                !Constant.FILE_SUFFIX_NAMES.contains(extName)) {
            throw new YamiShopBindException(String.format(I18nMessage.getMessage("yami.upload.filename.forbid"), extName));
        }
    }
}
