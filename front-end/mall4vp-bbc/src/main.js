import { createApp } from 'vue'
import App from './App.vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import moment from 'moment'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import router from '@/router'
// 全局样式
import '@/styles/index.scss'
// i18n
import { setupI18n } from '@/lang'
// svg
import 'virtual:svg-icons-register'
import svgIcon from '@/icons/SvgIcon.vue'
// 自定义指令
import { setupDirective } from '@/directive/index.js'
// 全局图片替换
import { checkFileUrl } from '@/utils/index.js'
import { useCurrencyStore } from '@/stores/currency.js'

moment.locale('zh-cn', {
  longDateFormat: {
    LT: 'HH:mm',
    LTS: 'HH:mm:ss',
    L: 'YYYY-MM-DD',
    LL: 'YYYY-MM-DD HH:mm:ss'
  },
  week: {
    // GB/T 7408-1994《数据元和交换格式·信息交换·日期和时间表示法》与ISO 8601:1988等效
    dow: 1, // 星期一， 是一个星期的第一天
    doy: 4 // 1月4日所在的的一周是一年的第一周
  }
})
const app = createApp(App)

app.config.globalProperties.checkFileUrl = checkFileUrl
// useCurrencyStore: 默认货币状态管理工具
app.config.globalProperties.useCurrencyStore = useCurrencyStore

// router
app.use(router)
// pinia
const pinia = createPinia()
app.use(pinia)

// i18n
app.use(setupI18n)
app.component('SvgIcon', svgIcon)
// 注册指令(directive)
setupDirective(app)

// element-plus
app.use(ElementPlus)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
