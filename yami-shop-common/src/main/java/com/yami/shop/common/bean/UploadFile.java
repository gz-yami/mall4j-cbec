package com.yami.shop.common.bean;

import com.yami.shop.common.enums.StorageTypeEnum;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

/**
 * 文件上传配置类
 * @author gaozijie
 * @date 2023-10-25
 */
@Data
public class UploadFile {

    /**
     * 存储类型
     * @see com.yami.shop.common.enums.StorageTypeEnum
     */
    private Integer storageType;

    /**
     * 域名
     */
    private String domain;

    /**
     * 上传终端
     */
    private String endPoint;

    /**
     * 账号
     */
    private String accessKey;

    /**
     * 密码
     */
    private String secretKey;

    /**
     * 桶名
     */
    private String bucketName;

    /**
     * 获取资源访问地址
     * @return 资源访问地址
     */
    public String resourceUrl() {
        if (!ObjectUtils.isEmpty(domain)) {
            return domain;
        }
        // minio桶名拼接在地址后面，其余拼接在域名前面
        if (Objects.equals(storageType, StorageTypeEnum.MINIO.getValue())) {
            return endPoint + "/" + bucketName;
        }
        String[] endpointParams = endPoint.split("://");
        return String.format("%s://%s.%s", endpointParams[0], bucketName, endpointParams[1]);
    }

    /**
     * 给资源url添加域名
     * @param url 资源url
     * @return 资源url
     */
    public String addResourceDomain(String url) {
        if (ObjectUtils.isEmpty(url)
                || url.startsWith("http")) {
            return url;
        }
        return this.resourceUrl() + "/" + url;
    }
}
