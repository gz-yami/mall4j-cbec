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

import com.yami.shop.bean.app.dto.SimpleDeliveryDto;
import com.yami.shop.bean.model.Delivery;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LGH
 */
@Slf4j
@RestController
@RequestMapping("/p/delivery")
@Tag(name = "查看物流接口")
@AllArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;


    @GetMapping("/list")
    @Operation(summary = "查看物流列表" , description = "查看物流列表")
    public ServerResponseEntity<List<SimpleDeliveryDto>> checkDelivery() {
        List<Delivery> list = deliveryService.list();
        List<SimpleDeliveryDto> deliveryDtos = BeanUtil.mapAsList(list, SimpleDeliveryDto.class);
        return ServerResponseEntity.success(deliveryDtos);
    }
}
