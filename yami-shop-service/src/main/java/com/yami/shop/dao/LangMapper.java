package com.yami.shop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.shop.bean.dto.LangDTO;
import com.yami.shop.bean.model.Lang;
import com.yami.shop.bean.vo.LangVO;
import com.yami.shop.common.util.PageParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author letere
 * @since 2024-03-25
 */
public interface LangMapper extends BaseMapper<Lang> {

    /**
     * 获取多语言列表
     * @param langDTO 多语言DTO
     * @return 多语言列表
     */
    List<LangVO> listLang(@Param("langDTO") LangDTO langDTO);

    /**
     * 分页查询多语言
     * @param pageParam 分页参数
     * @param langDTO 多语言DTO
     * @return 多语言分页
     */
    Page<LangVO> pageLang(PageParam<LangVO> pageParam,
                          @Param("langDTO") LangDTO langDTO);

}
