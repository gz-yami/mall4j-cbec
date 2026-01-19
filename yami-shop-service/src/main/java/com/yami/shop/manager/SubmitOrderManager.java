/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.manager;

import com.yami.shop.bean.app.dto.ShopCartOrderMergerDto;
import com.yami.shop.bean.app.param.SubmitOrderParam;
import com.yami.shop.bean.event.TryLockStockEvent;
import com.yami.shop.common.response.ServerResponseEntity;
import org.springframework.stereotype.Component;


/**
 * 提交订单适配
 * @author FrozenWatermelon
 * @date 2020/12/07
 */
@Component
public interface SubmitOrderManager {

    /**
     * 检查订单所需参数和设置值
     * @param submitOrderParam 前端参数
     * @param userId 用户id
     * @return
     */
    ServerResponseEntity<ShopCartOrderMergerDto> checkSubmitInfo(SubmitOrderParam submitOrderParam, String userId);

}
