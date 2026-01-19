import { createI18n } from 'vue-i18n'
import en from '@/lang/modules/en.js'
import zh from '@/lang/modules/zhCn.js'
import hk from '@/lang/modules/zh_HK.js'
import ar from '@/lang/modules/ar.js'
import de from '@/lang/modules/de.js'
import es from '@/lang/modules/es.js'
import he from '@/lang/modules/he.js'
import ko from '@/lang/modules/ko.js'
import pt from '@/lang/modules/pt.js'

const messages = {
  // 0-简体中文
  zh_CN: {
    ...zh
  },
  // 1-繁体中文
  zh_HK: {
    ...hk
  },
  // 2-英文
  en: {
    ...en
  },
  // 3-葡萄牙语
  pt: {
    ...pt
  },
  // 4-韩语
  ko: {
    ...ko
  },
  // 5-希伯来语
  he: {
    ...he
  },
  // 6-德语
  de: {
    ...de
  },
  // 7-阿拉伯语
  ar: {
    ...ar
  },
  // 8-西班牙语
  es: {
    ...es
  }
}

const localeData = {
  globalInjection: true, // 如果设置true, $t() 函数将注册到全局
  legacy: false, // 如果想在composition api中使用需要设置为false
  // 默认语言
  // locale: settings.defaultLanguage,
  locale: localStorage.getItem('cbecB2cLang') || 'en',
  messages // set locale messages
}

// 装修模块使用的语言实例
const localeDataDecoration = {
  globalInjection: true, // 如果设置true, $td() 函数将注册到全局
  legacy: false, // 如果想在composition api中使用需要设置为false
  // 默认语言
  // locale: settings.defaultLanguage,
  locale: localStorage.getItem('cbecB2cDecorationLang') || 'en',
  messages // set locale messages
}

export const i18n = createI18n(localeData)
export const i18nDecoration = createI18n(localeDataDecoration)
export const setupI18n = {
  install (app) {
    app.use(i18n)
  }
}
