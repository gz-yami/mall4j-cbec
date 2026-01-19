import http from '@/utils/http'
import { defineStore } from 'pinia'
export const useLangStore = defineStore('lang', {
  namespaced: true,
  state: () => {
    return {
      langList: [], // 语言列表
      langName: '', // 当前所选语言名称
      language: '',

      lang: null, // 当前语言值
      defLang: null, // 默认语言值

      // 装修区专用语
      decLangName: '', // 当前所选语言名称
      decLanguage: '',

      // 用户端默认语言id
      masterLangId: 0
    }
  },
  actions: {
    updateLang (langInfo) {
      langInfo = JSON.parse(JSON.stringify(langInfo))
      const { langItemList, name, language, lang } = langInfo
      const oldLanguage = localStorage.getItem('cbecB2cLang')
      const decLang = localStorage.getItem('cbecB2cDecorationLang')
      const langList = langItemList.filter(f => !f.hide) // 过滤掉隐藏的语言
      // 当前语言存在语言列表中
      let isExist = false
      // 是否已缓存语言
      if (oldLanguage) {
        for (const it of langList) {
          if (it.language === oldLanguage) {
            this.langName = it.name
            this.language = it.language
            this.lang = it.lang
            isExist = true
          }
          if (it.language === decLang) {
            this.decLanguage = it.language
            this.decLangName = it.name
            if (isExist) break
          }
        }
      }
      // 当前无缓存语言或当前缓存语言不在语言列表时
      if (!lang || !isExist) {
        this.langName = name
        this.language = language
        this.lang = lang
        // 页面默认为英文
        localStorage.setItem('cbecB2cLang', 'en')
        localStorage.setItem('cbecB2cDecorationLang', language)
        window.location.reload()
      }
      this.langList = langList
      this.defLang = langList.find(i => i.master === 1).lang
    },
    switchLang (langInfo) {
      langInfo = JSON.parse(JSON.stringify(langInfo))
      const { name, language, lang } = langInfo
      this.langName = name
      this.language = language
      this.lang = lang
      localStorage.setItem('cbecB2cLang', language)
    },
    updateLangSetUp () {
      http({
        url: http.adornUrl('/platform/lang/default'),
        method: 'get',
        params: http.adornParams()
      })
        .then(({ data }) => {
          const isReload = this.langName === data.name // 重载标识，false为需重载
          this.langName = data.name
          this.language = data.language
          this.lang = data.lang
          localStorage.setItem('cbecB2cLang', data.language)
          if (!isReload) window.location.reload() // 默认语言变化，重载页面
          this.langList = data.langList
          this.defLang = data.langList.find(i => i.master === 1).lang
        })
    },
    /**
     * 设置装修区语言
     * @param langInfo
     */
    setDecorationLang (langInfo) {
      const { name, language } = langInfo
      this.decLanguage = language
      this.decLangName = name
      localStorage.setItem('cbecB2cDecorationLang', language)
    },
    /**
     * 获取国际化语言列表
     */
    initLangList () {
      http({
        url: http.adornUrl('/platform/lang/default'),
        method: 'get',
        params: http.adornParams()
      })
        .then(({ data }) => {
          this.masterLangId = data.langId
          this.updateLang(data)
        })
    },
    /**
     *  根据传入的语言列表，返回对应环境的属性值
     *  @param list 语言数据列表数据列表
     *  @param key 需要返回的属性值
     */
    getNowLangKey (list, key) {
      if (!(list.length && list.length > 0)) {
        return ''
      }
      const langObj = list.find(i => i.lang === this.lang)
      const defLangObj = list.find(i => i.lang === this.defLang)
      // 返回当前语言值
      if (langObj && langObj[key]) {
        return langObj[key]
      }
      // 返回默认语言值
      if (defLangObj && defLangObj[key]) {
        return defLangObj[key]
      }
      return list[0][key]
    }
  }
})
