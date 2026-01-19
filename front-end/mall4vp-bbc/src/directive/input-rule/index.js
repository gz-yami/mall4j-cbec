/**
 *
 * 检测输入框为el-input的或是input
 * @param {*} node 节点信息
 * @returns 返回input的节点
 */
const onHandleNode = (node) => {
  if (/input/i.test(node.nodeName)) {
    return node
  }
  return node.querySelector('input')
}

/**
 * 处理指令修饰符所标识的规则
 * @param {*} rules 指令修饰符
 * @returns 替换规则
 */
const onHandleRule = (rules) => {
  // 只允许输入数字
  if (rules.num) {
    return /[^\d]/g
  }
  // 消除字符串两端空格，配合lazy使用达到输入完成后再消除空格
  if (rules.tirm) {
    return /(^\s*)|(\s*$)/g
  }
  return /\s/g
}

// 处理输入的内容
export const onHandleInput = {
  // value；禁止输入的规则
  updated (el, { modifiers }) {
    try {
      const inpNode = onHandleNode(el)
      const newval = inpNode.value.replace(onHandleRule(modifiers), '')
      if (inpNode.value !== newval) {
        inpNode.value = newval
        inpNode.dispatchEvent(new Event(modifiers.lazy ? 'change' : 'input'))
      }
    } catch (e) {
      // eslint-disable-next-line no-console
      console.log(e)
    }
  }
}
