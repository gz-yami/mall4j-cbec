/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.delivery.common.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.delivery.common.model.Transport;

/**
 *
 * @author lgh on 2018/11/16.
 */
public interface TransportService extends IService<Transport> {

    /**
     * 新增运费模板
     *
     * @param transport 运费模板
     * @return
     */
    Long insertTransportAndTransfee(Transport transport);

    /**
     * 更新运费模板
     *
     * @param transport 运费模板
     */
    void updateTransportAndTransfee(Transport transport);

    /**
     * 删除运费模板
     *
     * @param ids 运费模板
     */
    void deleteTransportAndTransfeeAndTranscity(Long[] ids);

    /**
     * 获取运费模板详细信息
     *
     * @param transportId 运费模板id
     * @return 运费模板详细信息
     */
    Transport getTransportAndAllItems(Long transportId);

    /**
     * 删除运费模板缓存
     *
     * @param transportId 运费模板id
     */

    void removeTransportAndAllItemsCache(Long transportId);

}
