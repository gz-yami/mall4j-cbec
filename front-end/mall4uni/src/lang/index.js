import { createI18n } from 'vue-i18n'
import en from './langs/en'
import zh from './langs/zh'
import hk from '@/lang/langs/zh_HK'
import pt from '@/lang/langs/pt'
import ko from '@/lang/langs/ko'
import he from '@/lang/langs/he'
import de from '@/lang/langs/de'
import ar from '@/lang/langs/ar'
import es from '@/lang/langs/es'

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
  // 如果设置true, $t() 函数将注册到全局
  globalInjection: true,
  // 如果想在composition api中使用需要设置为false
  legacy: false,
  // 默认语言
  locale: uni.getStorageSync('cbecB2cLang') || 'en',
  // 所有语言
  messages
}

export const i18n = createI18n(localeData)
export const setupI18n = {
  install (app) {
    app.use(i18n)
  }
}
