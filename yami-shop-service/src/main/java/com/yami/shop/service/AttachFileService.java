/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.dto.UploadSuccessDTO;
import com.yami.shop.bean.model.AttachFile;
import com.yami.shop.bean.vo.PreSignUrlVO;
import com.yami.shop.common.util.PageParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 *
 *
 * @author lgh
 * @date 2018/07/27
 */
public interface AttachFileService extends IService<AttachFile> {

    /**
     * 上传文件
     * @param bytes 文件byte数组
     * @param originalName 文件原名
     * @return 文件路径
     * @throws IOException
     */
    String uploadFile(byte[] bytes,String originalName) throws IOException;

    /**
     * 上传文件到本地
     * @param file
     * @throws IOException e
     * @return
     */
    String uploadFile(MultipartFile file) throws IOException;

    /**
     * 根据文件路径删除文件
     * @param filePath 文件路径
     */
    void deleteFile(String filePath);

    /**
     * 更新文件信息
     * @param attachFile 文件信息
     * @return 是否成功
     */
    Boolean updateFile(AttachFile attachFile);

    /**
     * 分页获取文件信息列表
     * @param page 分页参数
     * @param attachFile 文件搜索参数
     * @return 文件分页结果列表
     */
    IPage<AttachFile> getPage(PageParam<AttachFile> page, AttachFile attachFile);

    /**
     * 根据文件Id列表与店铺id批量删除文件记录
     * @param ids
     * @param shopId
     */
    void deleteByIdsAndShopId(List<Long> ids, Long shopId);

    /**
     * 根据店铺id与文件id列表与分组id批量移动文件
     * @param shopId
     * @param attachFile
     */
    void batchMoveByShopIdAndIdsAndGroupId(Long shopId, AttachFile attachFile);

    /**
     * 根据文件路径获取文件类型
     * @param filePath
     * @return
     */
    String getfileTypeByfilePath(String filePath);

    /**
     * 获取预签名url
     * @param shopId 店铺id
     * @param fileName 文件名
     * @param isImFile 是否客服聊天文件
     * @return 预签名对象
     */
    PreSignUrlVO getPreSingUrl(Long shopId, String fileName, Boolean isImFile);

    /**
     * 批量获取预签名url
     * @param shopId    店铺id
     * @param fileNames 文件名
     * @param isImFile  是否客服聊天文件
     * @return 预签名对象集合
     */
    List<PreSignUrlVO> getBatchPreSignUrl(Long shopId, List<String> fileNames, Boolean isImFile);

    /**
     * 上传成功（修改数据库数据）
     * @param uploadSuccessDTOList 上传成功dto集合
     */
    void uploadSuccess(List<UploadSuccessDTO> uploadSuccessDTOList);
}
