/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.model.AttachFile;
import com.yami.shop.common.util.PageParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yami
 */
public interface AttachFileMapper extends BaseMapper<AttachFile> {

    /**
     * 根据文件ID更新文件名称和分组
     * @param attachFile 文件参数
     * @return 是否成功
     */
    Boolean updateFile(@Param("attachFile") AttachFile attachFile);

    /**
     * 分页获取文件数据列表
     * @param page 分页参数
     * @param attachFile 文件搜索参数
     * @return 文件分页结果列表
     */
    IPage<AttachFile> getPage(PageParam<AttachFile> page, @Param("attachFile") AttachFile attachFile);

    /**
     * 批量更新文件的分组
     * @param attachFileGroupId
     */
    void updateBatchByAttachFileGroupId(@Param("attachFileGroupId") Long attachFileGroupId);

    /**
     * 根据id列表获取文件信息列表
     * @param ids
     * @return
     */
    List<AttachFile> getByIds(@Param("ids") List<Long> ids);

    /**
     * 根据id列表批量删除文件记录
     * @param ids
     */
    void batchDeleteByIds(@Param("ids") List<Long> ids);

    /**
     * 根据店铺id与文件id列表与分组id批量移动文件
     * @param shopId 店铺id
     * @param fileIds 文件id列表
     * @param attachFileGroupId 文件分组id
     */
    void batchMoveByShopIdAndIdsAndGroupId(@Param("shopId") Long shopId, @Param("fileIds") List<Long> fileIds, @Param("attachFileGroupId") Long attachFileGroupId);

    /**
     * 保存
     * @param attachFile
     * @return
     */
    Long save(@Param("attachFile")AttachFile attachFile);

    /**
     * 根据文件路径获取文件类型
     *
     * @param filePath
     * @return
     */
    String getfileTypeByfilePath(@Param("filePath") String filePath);
}
