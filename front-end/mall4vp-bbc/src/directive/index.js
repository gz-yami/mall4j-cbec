import { richDirective } from './rich/index.js'
import { drag } from './drag/index.js'
import { onHandleInput } from './input-rule'

// 全局注册 directive
export function setupDirective (app) {
  app.directive('rich', richDirective)
  app.directive('drag', drag)
  // inout的输入校验
  app.directive('input-rule', onHandleInput)
}
