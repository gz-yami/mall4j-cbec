import cookie from 'vue-cookies'
import router from '@/router'
import { ElMessage } from 'element-plus'
import Big from 'big.js'

/**
 * 获取uuid
 */
export function getUUID () {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    return (c === 'x' ? (Math.random() * 16) | 0 : 'r&0x3' | '0x8').toString(16)
  })
}
/**
 * 是否有权限
 * @param {*} key
 */
export function isAuth (key) {
  const authorities = JSON.parse(sessionStorage.getItem('cbecB2cAuthorities') || '[]')
  if (authorities.length) {
    for (const i in authorities) {
      const element = authorities[i]
      if (element === key) {
        return true
      }
    }
  }
  return false
}

/**
 * 树形数据转换
 * @param {*} data
 * @param {*} id
 * @param {*} pid
 * @param {*} isFilter 是否过滤不够3层的数据
 */
export function treeDataTranslate (data, id = 'id', pid = 'parentId', isFilter = true) {
  const res = []
  const temp = {}
  for (const datum of data) {
    temp[datum[id]] = datum
  }
  for (const datum of data) {
    if (temp[datum[pid]] && datum[id] !== datum[pid]) {
      temp[datum[pid]].hasChildren = false
      if (!temp[datum[pid]].children) {
        temp[datum[pid]].children = []
      }
      if (!temp[datum[pid]]._level) {
        temp[datum[pid]]._level = 1
      }
      datum._level = temp[datum[pid]]._level + 1
      temp[datum[pid]].children.push(datum)
    } else {
      res.push(datum)
    }
    // 增加一个键值，用于判断是否还存在下级
    if (temp[datum[pid]]?.children?.length > 0) {
      temp[datum[pid]].hasChildren = true
    }
  }
  if (isFilter) {
    // 检测是否全部达到3层级别的类目，如无则删除
    checkLevel(res)
  }
  return res
}

/**
 * 去除不够指定层级的数据
 * @param {Array} tree 树形数据
 * @param {String} children 孩子节点字段
 * @param {Number} level 层级数，默认为3层
 */
export function checkLevel (tree, children = 'children', level = 3) {
  for (let i = 0; i < tree.length; i++) {
    if (tree[i][children]?.length && level > 2) {
      checkLevel(tree[i][children], 'children', level - 1)
    }
    if (!tree[i][children]?.length) {
      tree.splice(i, 1)
      i--
    }
  }
}

/**
* 将树形结构转成数组
 * @param {Array} tree 树形数据
 */
export function treeToArray (tree, children = 'children') {
  return tree.reduce((prev, next) => {
    const { [children]: childrenList, ...node } = next
    return prev.concat(node, childrenList?.length ? treeToArray(childrenList) : [])
  }, [])
}

/**
 * 将数组中的parentId列表取出，倒序排列
 * @param {*} data
 * @param {*} id
 * @param {*} val
 */
export function idList (data, val, id = 'id') {
  const res = []
  idListFromTree(data, val, res, id)
  return res
}

/**
 * @param {*} data
 * @param {*} id
 * @param {*} val
 * @param {*} res
 * @param {*} children
 */
function idListFromTree (data, val, res = [], id = 'id', children = 'children') {
  for (const element of data) {
    if (element[children]) {
      if (idListFromTree(element[children], val, res, id, children)) {
        res.push(element[id])
        return true
      }
    }
    if (element[id] === val) {
      res.push(element[id])
      return true
    }
  }
}

/**
 * 清除登录信息
 */
export function clearLoginInfo () {
  cookie.remove('cbecB2cAuthorization_vp')
  router.options.isAddDynamicMenuRoutes = false
}

/**
 * 判断富文本是否为全空格
 * @param {String} str
 * @returns
 */
export function isHtmlNull (str) {
  if (!str) {
    return true
  }
  const html = str
    .replace(/<(?!img).*?>/g, '')
    .replace(/&nbsp;/gi, '')
    .replace(/(\n)/g, '')
  if (html === '') return true
  const regu = '^[ ]+$'
  const re = new RegExp(regu)
  return re.test(html)
}

/**
 * 判断富文本有无超出字数限制
 * @param {String} str
 * @return {Boolean} true/false
 */
export function validHtmlLength (str) {
  if (!str) {
    return false
  }
  if (str.replace(/<[^<>]+>/g, '').length > 65535) {
    ElMessage.error(
      `${$t('tip.currentWordCount') + str.replace(/<[^<>]+>/g, '').length},${$t(
        'weixin.wordsLimit'
      )}${65535}`
    )
    return true
  } else {
    return false
  }
}

// 计算每个sku后面有多少项
export function getLevels (tree) {
  const level = []
  for (let i = tree.length - 1; i >= 0; i--) {
    if (tree[i + 1] && tree[i + 1].tagItems) {
      level[i] = tree[i + 1].tagItems.length * level[i + 1] || 1
    } else {
      level[i] = 1
    }
  }
  return level
}

/**
 * 笛卡尔积运算
 * @param  {[type]} tree   [description]
 * @param  {Array}  stocks [description]
 * @param defaultObj
 * @return {{}}        [description]
 */
export function flatten (tree, stocks = [], defaultObj) {
  // 返回结果
  const result = []
  // sku的数据
  let skuLen = 0
  // 记录已存在的stock的数据
  const stockMap = {}
  // sku等级关系
  const level = getLevels(tree)
  // 没有参数时
  if (tree.length === 0) {
    if (stocks && stocks.length === 1 && (!stocks[0].properties || stocks[0].properties === '')) {
      return stocks
    } else {
      return [Object.assign({}, defaultObj)]
    }
  }
  // 计算sku数据
  tree.forEach(sku => {
    const { tagItems } = sku
    if (!tagItems || tagItems.length === 0) return true
    skuLen = (skuLen || 1) * tagItems.length
  })
  // 根据已有的stocks生成一个map
  stocks.forEach(stock => {
    const { properties, ...attr } = stock
    stockMap[properties] = attr
  })
  // 生成笛卡尔积
  for (let i = 0; i < skuLen; i++) {
    const mapKey = []
    const mapKeyEn = []
    const temp = {}
    tree.forEach((sku, column) => {
      const { tagItems } = sku
      let item = {}
      if (!tagItems || tagItems.length === 0) return true
      if (tagItems.length > 1) {
        // 计算当前sku的行数
        const row = Number.parseInt(i / level[column], 10) % tagItems.length
        item = tree[column].tagItems[row]
      } else {
        item = tree[column].tagItems[0]
      }
      if (item.pic) {
        temp.pic = item.pic
      }
      if (!sku.tagName || !item.propValue) return
      mapKey.push(`${sku.tagName}:${item.propValue}`)
      mapKeyEn.push(`${sku.tagNameEn || sku.tagName}:${item.propValueEn || item.propValue}`)
    })
    const { ...data } = stockMap[mapKey.join(';')] || {}
    temp.properties = mapKey.join(';')
    temp.propertiesEn = mapKeyEn.join(';')
    // 从map中找出存在的sku并保留其值
    result.push({ ...defaultObj, ...data, ...temp })
  }
  return result
}

/**
* 文件地址校验
* @param fileUrl 获取到的文件路径
*/
export function checkFileUrl (fileUrl) {
  if (fileUrl === '') return ''
  const baseUrl = import.meta.env.VITE_APP_RESOURCES_URL
  // 适配el-image 图片组件预览功能
  if (fileUrl && typeof fileUrl === 'object') {
    // eslint-disable-next-line no-return-assign
    return fileUrl.map(el => el = checkFileUrl(el))
  } else {
    if (fileUrl && fileUrl.indexOf('http') === -1) {
      return baseUrl + fileUrl
    } else {
      return fileUrl
    }
  }
}

export function formatPrice (price, type = 0) {
  if (!price) {
    return 0
  }
  Big.DP = 2
  if (type === 0) {
    return +new Big(price).times(100).valueOf()
  }
  return +new Big(price).div(100).valueOf()
}

/**
 * Check if an element has a class
 * @param {HTMLElement} elm
 * @param {string} cls
 * @returns {boolean}
 */
export function hasClass (ele, cls) {
  return !!ele.className.match(new RegExp('(\\s|^)' + cls + '(\\s|$)'))
}

/**
 * Add class to element
 * @param {HTMLElement} elm
 * @param {string} cls
 */
export function addClass (ele, cls) {
  if (!hasClass(ele, cls)) ele.className += ' ' + cls
}

/**
 * Remove class from element
 * @param {HTMLElement} elm
 * @param {string} cls
 */
export function removeClass (ele, cls) {
  if (hasClass(ele, cls)) {
    const reg = new RegExp('(\\s|^)' + cls + '(\\s|$)')
    ele.className = ele.className.replace(reg, ' ')
  }
}

/**
 * 将数字转换为万，千万、亿等
 * @param value 数字值
 */
export function bigNumberTransform (value) {
  const newValue = ['', '', '']
  let fr = 1000
  let num = 3
  let text1 = ''
  let fm = 1
  while (value / fr >= 1) {
    fr *= 10
    num += 1
    // console.log('数字', value / fr, 'num:', num)
  }
  if (num <= 4) { // 千
    newValue[0] = parseInt(value / 1000) + ''
    newValue[1] = 'k'
  } else if (num <= 8) { // 万
    text1 = parseInt(num - 4) / 3 > 1 ? 'kw' : 'w'
    // tslint:disable-next-line:no-shadowed-variable
    fm = text1 === 'w' ? 10000 : 10000000
    if (value % fm === 0) {
      newValue[0] = parseInt(value / fm) + ''
    } else {
      newValue[0] = parseFloat(value / fm).toFixed(2) + ''
    }
    newValue[1] = text1
  } else if (num <= 16) { // 亿
    text1 = (num - 8) / 3 > 1 ? '千亿' : '亿'
    text1 = (num - 8) / 4 > 1 ? '万亿' : text1
    text1 = (num - 8) / 7 > 1 ? '千万亿' : text1
    // tslint:disable-next-line:no-shadowed-variable
    fm = 1
    if (text1 === '亿') {
      fm = 100000000
    } else if (text1 === '千亿') {
      fm = 100000000000
    } else if (text1 === '万亿') {
      fm = 1000000000000
    } else if (text1 === '千万亿') {
      fm = 1000000000000000
    }
    if (value % fm === 0) {
      newValue[0] = parseInt(value / fm) + ''
    } else {
      newValue[0] = parseFloat(value / fm).toFixed(2) + ''
    }
    newValue[1] = text1
  }
  if (value < 1000) {
    newValue[0] = value + ''
    newValue[1] = ''
  }
  return newValue.join('')
}

/**
 * 详情地址文本展示
 * @param province 国
 * @param city 省
 * @param area 市
 * @param addr具体地址
 * @returns {string} 国/省/市/具体地址
 */
export function handlerAddress (province, city, area, addr) {
  const addressTxt = [province]
  city && addressTxt.push(city)
  area && addressTxt.push(area)
  addressTxt.push(addr)
  return addressTxt.join(' / ')
}

/**
 * 返回（后台端语言管理-默认语言）的名称（displayName）
 * @param {object} row 列表数组某项
 * @param {string} displayName 展示名称-变量名
 * @param {string} langList 名称国际化数组-变量名
 * @param {number|string} lang 默认语言lang
 * @returns {*|string} 展示名称（displayName）
 */
export function handlerDefaultLangNameShow (row, displayName, langList, lang) {
  if (row[displayName]) return row[displayName]
  let displayNameCopy = ''
  for (const item of row[langList]) {
    if (item.lang === lang) {
      displayNameCopy = item[displayName]
      break
    }
  }
  return displayNameCopy
}

/**
 * 计算并返回对应语言环境的折扣比例
 * @param num 折扣比例数值
 */
export function countDiscountRatio (num) {
  const lang = localStorage.getItem('cbecB2cLang') || 'en'
  if (lang === 'zh_CN' || lang === 'zh_HK') {
    return num
  } else {
    const val = (10 - num) * 10
    return val + '%'
  }
}
