package com.yami.shop.multishop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.shop.bean.dto.LangDTO;
import com.yami.shop.bean.vo.DefaultLangVO;
import com.yami.shop.bean.vo.LangVO;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.service.LangService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gaozijie
 * @since 2024-04-03
 */
@Tag(name = "多语言")
@AllArgsConstructor
@RequestMapping("/platform/lang")
@RestController
public class LangController {

    private final LangService langService;

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public ServerResponseEntity<Page<LangVO>> page(PageParam<LangVO> pageParam,
                                                   LangDTO langDTO) {
        Page<LangVO> page = langService.pageLang(pageParam, langDTO);
        return ServerResponseEntity.success(page);
    }

    @Operation(summary = "获取列表")
    @GetMapping("/list")
    public ServerResponseEntity<List<LangVO>> list(LangDTO langDTO) {
        List<LangVO> langs = langService.listLang(langDTO);
        return ServerResponseEntity.success(langs);
    }

    @Operation(summary = "获取默认语言")
    @GetMapping("/default")
    public ServerResponseEntity<DefaultLangVO> getDefaultLang() {
        DefaultLangVO defaultLang = langService.getDefaultLang();
        return ServerResponseEntity.success(defaultLang);
    }

    @Operation(summary = "获取详情")
    @GetMapping("/{id}")
    public ServerResponseEntity<LangVO> info(@PathVariable("id") Integer id) {
        LangVO langVO = langService.getLangInfo(id);
        return ServerResponseEntity.success(langVO);
    }

    @Operation(summary = "新增")
    @PostMapping("/save")
    public ServerResponseEntity<Void> save(@RequestBody LangDTO langDTO) {
        langService.saveLang(langDTO);
        return ServerResponseEntity.success();
    }

    @Operation(summary = "更新")
    @PutMapping("/update")
    public ServerResponseEntity<Void> update(@RequestBody LangDTO langDTO) {
        langService.updateLang(langDTO);
        return ServerResponseEntity.success();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public ServerResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        langService.deleteLang(id);
        return ServerResponseEntity.success();
    }
}
