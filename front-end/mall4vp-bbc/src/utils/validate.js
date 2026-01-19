/**
 * 邮箱
 * @param {*} s
 */
export function isEmail (s) {
  return /^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_-])+((.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(s)
}

/**
 * 手机号码
 * @param {*} s
 */
export function isMobile (s) {
  return /^1[0-9]{10}$/.test(s)
}

/**
 * 检查手机号是否
 * @param value
 * @returns {boolean}
 */
export function isStarPhone (value) {
  if (!value) {
    return false
  }
  return /^(?:(?:\+|00)86)?1\d{2}([\d*]{4})\d{4}$/.test(value)
}

/**
 * 电话号码
 * @param {*} s
 */
export function isPhone (s) {
  return /^([0-9]{3,4}-)?[0-9]{7,8}$/.test(s)
}

/**
 * 电话号码带星号
 * @param {*} s
 */
export function isPhoneStar (s) {
  return /^(\d{3,4}-)?\d{3}([\d*]{4})\d{0,1}$/.test(s)
}

/**
 * URL地址
 * @param {*} s
 */
export function isURL (s) {
  return /^http[s]?:\/\/.*/.test(s)
}

/**
 * qq
 * @param {*} s
 */
export function isQq (s) {
  return /[1-9][0-9]{4,14}/.test(s)
}

/**
 * 判断是否全为空格 只要有一个其他字符返回false
 * @param {String} str
 * @returns {Boolean}
 */
export function validNoEmptySpace (str) {
  const reg = /^\s+$/g
  return reg.test(str)
}

/**
 * 用户名
 * 1.由字母、数字或下划线 4-16位组成
 * 2.不能为纯数字
 * @param userName 输入值
 * @returns 匹配结果
 */
export function isUserName (userName) {
  const reg = /^(?!\d+$)[a-zA-Z0-9_]{4,16}$/
  return reg.test(userName)
}

/**
 * 去除头尾两端空格
 */
export function removeHeadAndTailSpaces (value) {
  return value.trim()
}

/**
 * 统一社会信用代码
 * @param value 输入值
 * @returns 匹配结果
 */
export function isCreditCode (value) {
  const reg = /^(([0-9A-Za-z]{15})|([0-9A-Za-z]{18})|([0-9A-Za-z]{20}))$/
  return reg.test(value)
}

export function validEmail (s) {
  const reg = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
  return reg.test(s)
}

/**
 * 判断身份证是否合法
 * @param {str} idcode 函数参数必须是字符串，因为二代身份证号码是十八位，而在javascript中，十八位的数值会超出计算范围，造成不精确的结果，导致最后两位和计算的值不一致，从而该函数出现错误。
 * 详情查看javascript的数值范围
 * @returns {Boolean}
 */
export function checkIDCard (idcode) {
  // 加权因子
  const weightFactor = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
  // 校验码
  const checkCode = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2']

  const code = idcode + ''
  const last = idcode[17]// 最后一位

  const seventeen = code.substring(0, 17)

  // ISO 7064:1983.MOD 11-2
  // 判断最后一位校验码是否正确
  const arr = seventeen.split('')
  const len = arr.length
  let num = 0
  for (let i = 0; i < len; i++) {
    num = num + arr[i] * weightFactor[i]
  }

  // 获取余数
  const resisue = num % 11
  const lastNo = checkCode[resisue]

  // 格式的正则
  // 正则思路
  /*
  第一位不可能是0
  第二位到第六位可以是0-9
  第七位到第十位是年份，所以七八位为19或者20
  十一位和十二位是月份，这两位是01-12之间的数值
  十三位和十四位是日期，是从01-31之间的数值
  十五，十六，十七都是数字0-9
  十八位可能是数字0-9，也可能是X
  */
  const idcardPatter = /^[1-9][0-9]{5}([1][9][0-9]{2}|[2][0][0|1][0-9])([0][1-9]|[1][0|1|2])([0][1-9]|[1|2][0-9]|[3][0|1])[0-9]{3}([0-9]|[X])$/

  // 判断格式是否正确
  const format = idcardPatter.test(idcode)

  // 返回验证结果，校验码和格式同时正确才算是合法的身份证号码
  return !!(last === lastNo && format)
}
/**
 * @param {String} 密码
 * @returns {Boolean} 检查密码格式是否正确
 */
export function validPassword (s) {
  const reg = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*(\W|_))[A-Za-z\d(\W|_)]{8,20}$/
  return reg.test(s)
}
