package com.yami.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.dto.LangDTO;
import com.yami.shop.bean.model.Lang;
import com.yami.shop.bean.vo.DefaultLangVO;
import com.yami.shop.bean.vo.LangVO;
import com.yami.shop.common.constants.SysCacheNames;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.dao.LangMapper;
import com.yami.shop.service.LangService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author letere
 * @since 2024-03-25
 */
@Service
@AllArgsConstructor
public class LangServiceImpl extends ServiceImpl<LangMapper, Lang> implements LangService {

    private final LangMapper langMapper;

    @Override
    public Page<LangVO> pageLang(PageParam<LangVO> pageParam, LangDTO langDTO) {
        return langMapper.pageLang(pageParam, langDTO);
    }

    @Override
    public List<LangVO> listLang(LangDTO langDTO) {
        return langMapper.listLang(langDTO);
    }

    @Override
    @Cacheable(cacheNames = SysCacheNames.LANG_CACHE, key = SysCacheNames.DEFAULT_LANG)
    public DefaultLangVO getDefaultLang() {
        LangDTO langDTO = new LangDTO();
        langDTO.setHide(0);
        List<LangVO> langs = langMapper.listLang(langDTO);
        // 构建默认语言VO，第一条数据为默认语言
        DefaultLangVO defaultLangVO = new DefaultLangVO();
        BeanUtils.copyProperties(langs.get(0), defaultLangVO);
        defaultLangVO.setLangItemList(langs);
        return defaultLangVO;
    }

    @Override
    public LangVO getLangInfo(Integer id) {
        Lang lang = this.getById(id);
        LangVO langVO = new LangVO();
        BeanUtils.copyProperties(lang, langVO);
        return langVO;
    }

    @Override
    @Cacheable(cacheNames = SysCacheNames.LANG_CACHE, key = SysCacheNames.LANG_MAP)
    public Map<String, LangVO> getLangMap() {
        List<LangVO> langVOList = langMapper.listLang(new LangDTO());
        if (CollectionUtils.isEmpty(langVOList)) {
            return new HashMap<>(0);
        }
        Map<String, LangVO> langMap = new LinkedHashMap<>(langVOList.size());
        for (LangVO langVO : langVOList) {
            langMap.put(langVO.getLanguage(), langVO);
        }
        return langMap;
    }

    @Override
    @Caching(
        evict = {
                @CacheEvict(cacheNames = SysCacheNames.LANG_CACHE, key = SysCacheNames.DEFAULT_LANG),
                @CacheEvict(cacheNames = SysCacheNames.LANG_CACHE, key = SysCacheNames.LANG_MAP)
        }
    )
    public void saveLang(LangDTO langDTO) {
        langDTO.setLangId(null);
        this.checkParam(langDTO);
        this.cancelDefault(langDTO, null);
        Lang lang = new Lang();
        BeanUtils.copyProperties(langDTO, lang);
        this.save(lang);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = SysCacheNames.LANG_CACHE, key = SysCacheNames.DEFAULT_LANG),
                    @CacheEvict(cacheNames = SysCacheNames.LANG_CACHE, key = SysCacheNames.LANG_MAP)
            }
    )
    public void updateLang(LangDTO langDTO) {
        Lang dbLang = this.checkParam(langDTO);
        this.cancelDefault(langDTO, dbLang);
        Lang lang = new Lang();
        BeanUtils.copyProperties(langDTO, lang);
        this.updateById(lang);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = SysCacheNames.LANG_CACHE, key = SysCacheNames.DEFAULT_LANG),
                    @CacheEvict(cacheNames = SysCacheNames.LANG_CACHE, key = SysCacheNames.LANG_MAP)
            }
    )
    public void deleteLang(Integer id) {
        Lang lang = this.getById(id);
        if (Objects.isNull(lang)) {
            throw new YamiShopBindException("yami.lang.exception.notExist");
        }
        if (Objects.equals(lang.getMaster(), 1)) {
            throw new YamiShopBindException("yami.lang.exception.defaultCannotDelete");
        }
        this.removeById(id);
    }

    /**
     * 参数检查
     * @param langDTO 多语言DTO
     */
    private Lang checkParam(LangDTO langDTO) {
        if (Objects.equals(langDTO.getHide(), 1)
                && Objects.equals(langDTO.getMaster(), 1)) {
            // 默认语言不能被隐藏
            throw new YamiShopBindException("yami.lang.exception.defaultCannotHide");
        }
        Lang lang = null;
        if (!Objects.isNull(langDTO.getLangId())) {
            lang = this.getById(langDTO.getLangId());
            if (Objects.isNull(lang)) {
                throw new YamiShopBindException("yami.lang.exception.notExist");
            }
        }
        // 重名判断
        boolean hasEq = !ObjectUtils.isEmpty(langDTO.getName())
                || !Objects.isNull(langDTO.getLang())
                || !ObjectUtils.isEmpty(langDTO.getLanguage());
        if (hasEq) {
            long langCount = this.count(new LambdaQueryWrapper<Lang>().and(w -> {
                w.eq(!ObjectUtils.isEmpty(langDTO.getName()), Lang::getName, langDTO.getName())
                        .or().eq(!Objects.isNull(langDTO.getLang()), Lang::getLang, langDTO.getLang())
                        .or().eq(!ObjectUtils.isEmpty(langDTO.getLanguage()), Lang::getLanguage, langDTO.getLanguage());
            }).ne(!Objects.isNull(langDTO.getLangId()), Lang::getLangId, langDTO.getLangId()));
            if (langCount > 0) {
                throw new YamiShopBindException("yami.lang.exception.repeatParam");
            }
        }
        // 默认语言不能隐藏or取消
        if (!Objects.isNull(lang)) {
            if (Objects.equals(lang.getMaster(), 1)) {
                if (Objects.equals(langDTO.getHide(), 1)) {
                    throw new YamiShopBindException("yami.lang.exception.defaultCannotHide");
                }
                if (Objects.equals(langDTO.getMaster(), 0)) {
                    throw new YamiShopBindException("yami.lang.exception.mustHaveDefault");
                }
            }
        }
        return lang;
    }

    /**
     * 取消默认语言
     * @param langDTO 多语言DTO
     */
    private void cancelDefault(LangDTO langDTO, Lang lang) {
        if (!Objects.equals(langDTO.getMaster(), 1)) {
            return;
        }
        // 本身就是默认语言，就不需取消
        if (!Objects.isNull(lang)
                && Objects.equals(lang.getMaster(), 1)) {
            return;
        }
        this.update(new LambdaUpdateWrapper<Lang>()
                .set(Lang::getMaster, 0)
                .eq(Lang::getMaster, 1));
    }
}
