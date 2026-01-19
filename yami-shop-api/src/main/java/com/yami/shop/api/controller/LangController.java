package com.yami.shop.api.controller;

import com.yami.shop.bean.vo.DefaultLangVO;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.service.LangService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gaozijie
 * @since 2024-04-02
 */
@AllArgsConstructor
@Tag(name = "多语言")
@RequestMapping("/lang")
@RestController
public class LangController {

    private final LangService langService;

    @Operation(summary = "获取默认语言")
    @GetMapping("/default")
    public ServerResponseEntity<DefaultLangVO> getDefaultLang() {
        DefaultLangVO defaultLang = langService.getDefaultLang();
        return ServerResponseEntity.success(defaultLang);
    }
}
