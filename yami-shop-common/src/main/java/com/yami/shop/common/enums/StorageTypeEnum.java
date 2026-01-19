package com.yami.shop.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 存储服务类型
 * @author gaozijie
 * @date 2023-10-25
 */
@AllArgsConstructor
@Getter
public enum StorageTypeEnum {
    /**
     * minIO
     */
    MINIO(1),
    /**
     * 阿里云
     */
    OSS(2),
    /**
     * 腾讯云
     */
    COS(3),
    /**
     * 华为云
     */
    OBS(4),
    /**
     * 七牛云
     */
    KODO(5);

    private final int value;
}
