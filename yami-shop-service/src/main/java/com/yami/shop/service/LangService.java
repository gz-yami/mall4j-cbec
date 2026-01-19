package com.yami.shop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.dto.LangDTO;
import com.yami.shop.bean.model.Lang;
import com.yami.shop.bean.vo.DefaultLangVO;
import com.yami.shop.bean.vo.LangVO;
import com.yami.shop.common.util.PageParam;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author letere
 * @since 2024-03-25
 */
public interface LangService extends IService<Lang> {

    /**
     * 分页查询多语言
     * @param pageParam 分页参数
     * @param langDTO 多语言DTO
     * @return 多语言分页数据
     */
    Page<LangVO> pageLang(PageParam<LangVO> pageParam,
                          LangDTO langDTO);

    /**
     * 获取多语言列表
     * @param langDTO 多语言DTO
     * @return 多语言列表
     */
    List<LangVO> listLang(LangDTO langDTO);

    /**
     * 获取默认语言
     * @return 默认语言
     */
    DefaultLangVO getDefaultLang();

    /**
     * 获取多语言详情
     * @param id 多语言id
     * @return 多语言详情
     */
    LangVO getLangInfo(Integer id);

    /**
     * 获取语言映射表
     * @return key:语言代码， value：语言VO类
     */
    Map<String, LangVO> getLangMap();

    /**
     * 保存多语言
     * @param langDTO 多语言DTO
     */
    void saveLang(LangDTO langDTO);

    /**
     * 更新多语言
     * @param langDTO 多语言DTO
     */
    void updateLang(LangDTO langDTO);

    /**
     * 删除多语言
     * @param id 多语言id
     */
    void deleteLang(Integer id);
}
