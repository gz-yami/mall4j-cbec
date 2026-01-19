import { createPinia } from 'pinia'

const store = createPinia()

// 全局注册 Pinia
export function setupPinia (app) {
  app.use(store)
}

export { store }
