// 原  wxs/number.wxs 文件，使用时在 js中引入
// const wxs = number()

function number () {
  const wxs = {
    // 转换为价格的格式
    toPrice: (val) => {
      if (!val) {
        val = 0
      }
      val = Number(val)
      return val.toFixed(2)
    },
    // 价格展示
    parsePrice: (val) => {
      if (!val) {
        val = 0
      }
      return val.toFixed(2).split('.')
    },

    // 取整
    rounding: (val) => {
      if (!val) {
        val = 0
      }
      return parseInt(val)
    },
    // 满减格式
    parseDiscount: (discountRule, lang) => {
      if (discountRule == 0) {
        return lang == 'zh_CN' ? '满额减' : 'Amount'
      } else if (discountRule == 1) {
        return lang == 'zh_CN' ? '满件减' : 'Pieces'
      } else if (discountRule == 2) {
        return lang == 'zh_CN' ? '满额折' : 'Amount-discount'
      } else if (discountRule == 3) {
        return lang == 'zh_CN' ? '满件折' : 'Pieces-discount'
      } else {
        return ''
      }
    },
    // 满减提示
    parseDiscountMsg: (discountRule, needAmount, discount, lang) => {
      if (discountRule == 0) {
        return lang == 'zh_CN' ? '购满' + needAmount + '元减' + discount + '元' : 'Over ' + needAmount + ' minus ' + discount
      } else if (discountRule == 1) {
        return lang == 'zh_CN' ? '购满' + needAmount + '件减' + discount + '元' : discount + ' less for ' + needAmount + ' pieces'
      } else if (discountRule == 2) {
        return lang == 'zh_CN' ? '购满' + needAmount + '元打' + discount + '折' : discount + '% off over ' + needAmount
      } else if (discountRule == 3) {
        return lang == 'zh_CN' ? '购满' + needAmount + '件打' + discount + '折' : discount + '% off over ' + needAmount + ' pieces'
      } else {
        return ''
      }
    },

    // 优惠券名称
    parseDiscountProd: (discountRule, needAmount, discount, lang) => {
      if (discountRule == 1) {
        return lang == 'zh_CN' ? '满' + needAmount + '元减' + discount + '元' : '￥' + discount + ' off on ' + '￥' + needAmount
      } else if (discountRule == 2) {
        return lang == 'zh_CN' ? '满' + needAmount + '元打' + discount + '折' : (100 - discount * 10) + '% off on ' + '￥' + needAmount
      } else {
        return ''
      }
    },

    // 满减名称
    getCurrDiscountName: (discountId, discounts, lang) => {
      for (let i = 0; i < discounts.length; i++) {
        if (discounts[i].discountId == discountId) {
          return discounts[i].discountName
        }
      }
      return lang == 'zh_CN' ? '不参与促销' : 'Not participating in promotion'
    },

    /**
     * 裁剪日期  2020-03-20 15:04:40  ->  2020-03-20
     */
    spliceDate: (dateStr) => {
      if (!dateStr) return
      return dateStr.split(' ')[0]
    },
    toFixxed: val => {
      /**
       * 解决四舍五入精度问题
       * @param {*} d 保留位数
       * @param {*} value 值
       * @returns
       */
      const toFixxed = function (d, value) {
        let s = value + ''
        if (!d)d = 0
        if (s.indexOf('.') == -1)s += '.'
        s += new Array(d + 1).join('0')
        if (new RegExp('^(-|\\+)?(\\d+(\\.\\d{0,' + (d + 1) + '})?)\\d*$').test(s)) {
          s = '0' + RegExp.$2
          const pm = RegExp.$1
          let a = RegExp.$3.length
          let b = true
          if (a == d + 2) {
            a = s.match(/\d/g)
            if (parseInt(a[a.length - 1]) > 4) {
              for (let i = a.length - 2; i >= 0; i--) {
                a[i] = parseInt(a[i]) + 1
                if (a[i] == 10) {
                  a[i] = 0
                  b = i != 1
                } else break
              }
            }
            s = a.join('').replace(new RegExp('(\\d+)(\\d{' + d + '})\\d$'), '$1.$2')
          }
          if (b)s = s.substr(1)
          return (pm + s).replace(/\.$/, '')
        }
        return value + ''
      }
      if (!val) {
        val = 0
      }
      return toFixxed(2, val)
    }
  }
  return wxs
}

export default number
