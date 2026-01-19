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
import com.yami.shop.bean.model.Currency;
import com.yami.shop.bean.vo.CurrencyVO;
import com.yami.shop.bean.vo.PreSignUrlVO;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.service.AttachFileService;
import com.yami.shop.service.CurrencyService;
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
@RequestMapping("/currency")
@AllArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/list")
    @Operation(summary = "获取商城配置的所有货币")
    public ServerResponseEntity<List<CurrencyVO>> getFileById() {
        List<CurrencyVO> currencies = currencyService.listEnableCurrency();
        return ServerResponseEntity.success(currencies);
    }

}
