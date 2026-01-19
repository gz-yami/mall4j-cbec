import {
  createSSRApp
} from 'vue'
import App from './App.vue'
import { setupRouter } from './router'
// 国际化
import { setupI18n } from './lang'
import legacy from './utils/plugin/legacy.js'
import * as Pinia from 'pinia'

export function createApp () {
  const app = createSSRApp(App)
  app.use(setupI18n)
  // #ifdef H5
  app.use(legacy)
  // #endif
  app.use(Pinia.createPinia())
  setupRouter(app)
  return {
    app,
    Pinia
  }
}
