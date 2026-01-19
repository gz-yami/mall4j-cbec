/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
import http from './http.js'
import loginMethods from './login'

const util = {
  formatTime: date => {
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const day = date.getDate()
    const hour = date.getHours()
    const minute = date.getMinutes()
    const second = date.getSeconds()
    return [year, month, day].map(util.formatNumber).join('/') + ' ' + [hour, minute, second].map(util.formatNumber).join(':')
  },

  formatNumber: n => {
    n = n.toString()
    return n[1] ? n : '0' + n
  },

  formatHtml: content => {
    if (!content) {
      return
    }
    content = content.replace(/<!--(.+)-->/gi, '')
    content = content.replace(/class=/gi, 'sss=')
    content = content.replace(/<p/gi, '<p style="max-width:100% !important;word-wrap:break-word;word-break:break-word;" ')
    content = content.replace(/<img/gi, '<img style="max-width:100% !important;margin:0;display:inline-block !important; vertical-align:middle;" ')
    content = content.replace(/style="/gi, 'style="max-width:100% !important;table-layout:fixed;word-wrap:break-word;word-break:break-word;')
    content = content.replace(/<table/gi, '<table style="table-layout:fixed;word-wrap:break-word;word-break:break-word;" ')
    content = content.replace(/<td/gi, '<td cellspacing="0" cellpadding="0" style="border-width:1px; border-style:solid; border-color:#666; margin: 0px; padding: 0px;"')
    content = content.replace(/<video/gi, '<video style="width:100% !important;height:auto !important;margin:0;display:flex;" ')
    return content
  },

  endOfStartTime: (startTime, endTime) => {
    let result = {
      day: '00',
      hou: '00',
      min: '00',
      sec: '00',
      signs: 0
    }

    if (endTime - startTime > 0) {
      const time = (endTime - startTime) / 1000 // 获取天、时、分、秒
      const day = parseInt(time / (60 * 60 * 24))
      const hou = parseInt(time % (60 * 60 * 24) / 3600)
      const min = parseInt(time % (60 * 60 * 24) % 3600 / 60)
      const sec = parseInt(time % (60 * 60 * 24) % 3600 % 60)
      result = {
        day: `${util.timeFormat(day)}`,
        hou: `${util.timeFormat(hou)}`,
        min: `${util.timeFormat(min)}`,
        sec: `${util.timeFormat(sec)}`,
        signs: 1
      }
    }

    return result
  },

  // 小于10的格式化函数
  timeFormat: times => {
    return times < 10 ? '0' + times : times
  },

  dateToTimestamp: dateStr => {
    if (!dateStr) {
      return ''
    }

    const newDataStr = dateStr.replace(/\.|-/g, '/')
    const date = new Date(newDataStr)
    const timestamp = date.getTime()
    return timestamp
  },

  /**
   * 初始化支付类型
   */
  initPayType: () => {
    const payTypeInfo = {
      payType: PayType.PAYPAL,
      payTypeStr: 'payPal'
    }
    const paySwitchInfo = uni.getStorageSync('cbecB2cPaySwitchInfo')?.switchInfo || {}
    let firstTruePaymentMethod = null

    for (const key in paySwitchInfo) {
      if (paySwitchInfo[key] === true) {
        firstTruePaymentMethod = key
        break
      }
    }
    if (firstTruePaymentMethod === 'balancePaySwitch') {
      payTypeInfo.payTypeStr = 'balancePay'
      payTypeInfo.payType = PayType.BALANCEPAY
    }
    if (firstTruePaymentMethod === 'payPalSwitch') {
      payTypeInfo.payTypeStr = 'payPal'
      payTypeInfo.payType = PayType.PAYPAL
    }
    return payTypeInfo
  },
  // 检查是否开启支付方式
  checkPayWay (isOrderPay) {
    // 系统支付设置
    const paySwitchInfo = uni.getStorageSync('cbecB2cPaySwitchInfo')?.switchInfo || {}
    const appType = uni.getStorageSync('cbecB2cAppType')
    let notAvailable = true
    for (const payKey in paySwitchInfo) {
      if (paySwitchInfo[payKey]) {
        if (payKey === 'aliPaySwitch' && (appType === AppType.MP || appType === AppType.MINI)) continue
        // #ifndef H5
        if (payKey === 'payPalSwitch') continue
        // #endif
        notAvailable = false
        break
      }
    }
    if (notAvailable) {
      uni.showModal({
        title: $t('tips'),
        content: $t('notPayWayTips'),
        showCancel: false,
        confirmText: $t('confirm'),
        success: () => {
          if (isOrderPay) {
            uni.redirectTo({
              url: '/package-user/pages/order-list/order-list'
            })
          }
        }
      })
    }
    return !notAvailable
  },

  // 检查是否授权
  checkAuthInfo: (fn) => {
    // eslint-disable-next-line no-undef
    const pages = getCurrentPages()
    if (uni.getStorageSync('cbecB2cToken')) {
      fn && fn()
      return
    }
    if (pages[0] && pages[0].route === 'pages/cart/cart') {
      return
    }
    // 设置登录后的跳转地址
    loginMethods.setRouteUrlAfterLogin()

    uni.navigateTo({
      url: '/package-user/pages/user-login/user-login'
    })
  },

  /**
 * 设置登录后的跳转地址
 */
  setRouteUrlAfterLogin: () => {
    const pages = getCurrentPages()
    // 登录后的回跳地址
    if (pages[pages.length - 1].route.indexOf('register') === -1 && pages[pages.length - 1].route.indexOf('accountLogin') === -1) {
      uni.setStorageSync('cbecB2cRouteUrlAfterLogin', pages[pages.length - 1].$page.fullPath)
    }
  },

  // 函数参数必须是字符串，因为二代身份证号码是十八位，而在javascript中，十八位的数值会超出计算范围，造成不精确的结果，导致最后两位和计算的值不一致，从而该函数出现错误。
  // 详情查看javascript的数值范围
  checkIDCard: (idcode) => {
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
  },

  /**
   * 手机号正则校验
   */
  checkPhoneNumber: (phoneNumber) => {
    const regexp = /^[1][0-9]{10}$/
    return regexp.test(phoneNumber)
  },

  /**
   * 邮箱正则校验
   */
  checkEmail: (email) => {
    const regexp = /^[a-zA-Z0-9]+(\.[a-zA-Z0-9]+)*@[a-zA-Z0-9]+(\.[a-zA-Z0-9]+)+$/
    return regexp.test(email)
  },

  /**
   * 用户名正则校验
   */
  checkUserName: (userName) => {
    const regexp = /^([a-zA-Z0-9_]{4,16})$/
    return regexp.test(userName)
  },

  /**
   * 获取链接上的参数
   */
  getUrlKey: (name) => {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || ['', ''])[1]
      .replace(/\+/g, '%20')) || null
  },

  /**
   * 函数节流
   * @param fn
   * @param t
   * @returns {Function}
   * @constructor
   */
  throttle: (fn, t) => {
    let last
    let timer
    const interval = t || 500
    return function () {
      const args = arguments
      const now = +new Date()
      if (last && now - last < interval) {
        clearTimeout(timer)
        timer = setTimeout(() => {
          last = now
          fn.apply(this, args)
        }, interval)
      } else {
        last = now
        fn.apply(this, args)
      }
    }
  },

  /**
   * 记录页面点击次数
   */
  /**
   * 获取唯一值
   */
  getUuid: () => {
    const s = []
    const hexDigits = '0123456789abcdef'
    for (let i = 0; i < 36; i++) {
      s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1)
    }
    s[14] = '4' // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1) // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = '-'

    const uuid = s.join('')
    return uuid
  },

  /**
   * 时间戳转化为年 月 日 时 分 秒
   * number: 传入时间戳
   * format：返回格式，支持自定义，但参数必须与formateArr里保持一致
  */
  tsToDate: (number, format) => {
    const formateArr = ['Y', 'M', 'D', 'h', 'm', 's']
    const returnArr = []
    const date = new Date(number)
    returnArr.push(date.getFullYear())
    returnArr.push(date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1)
    returnArr.push(date.getDate() < 10 ? '0' + date.getDate() : date.getDate())
    returnArr.push(date.getHours() < 10 ? '0' + date.getHours() : date.getHours())
    returnArr.push(date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes())
    returnArr.push(date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds())
    for (const i in returnArr) {
      format = format.replace(formateArr[i], returnArr[i])
    }
    return format
  },

  /**
   * tabbar国际化
   */
  transTabbar: () => {
    const tabBarList = [
      {
        index: 0,
        text: $t('homepage')
      }, {
        index: 1,
        text: $t('classification')
      }, {
        index: 2,
        text: $t('cart')
      }, {
        index: 3,
        text: $t('myself')
      }
    ]
    for (let i = 0; i < tabBarList.length; i++) {
      nextTick(() => {
        uni.setTabBarItem({
          index: tabBarList[i].index,
          text: tabBarList[i].text
        })
      })
    }
  },

  /**
   * 移除购物车Tabbar的数字
   */
  removeTabBadge: () => {
    uni.removeTabBarBadge({
      index: 2
    })
  },
  /**
 * 防抖
 * @param fn
 * @param t
 * @returns {Function}
 * @constructor
 */
  debounce: (fn, t) => {
    const delay = t || 300
    let timer
    return function () {
      const args = arguments
      if (timer) {
        clearTimeout(timer)
      }
      const callNow = !timer
      timer = setTimeout(() => {
        timer = null
      }, delay)
      if (callNow) fn.apply(this, args)
    }
  },

  /**
   * 用户密码校验
   * @returns {Boolean}
   */
  checkPassword: (password) => {
    const regexp = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*(\W|_))[A-Za-z\d(\W|_)]{8,20}$/
    return regexp.test(password)
  },
  /**
   * 选择的图片是否小于2M
   * @param {Array} tempFiles 上传的图片临时文件数组
   * @return {Boolean} 是否小于2M
   */
  lessThan2M: (tempFiles) => {
    let flag = true
    for (let i = 0; i < tempFiles.length; i++) {
      const file = tempFiles[i]
      if (file.size > 1024 * 1024 * 2) {
        uni.showToast({
          title: $t('lessThan2M'),
          duration: 2000,
          icon: 'none'
        })
        flag = false
        break
      }
    }
    return flag
  },

  /**
   * 根据地址返回省市区
   * @param {String} address 地址
   * @param {String} name 地名
   * @return {Array} 省市区的信息
   */
  area: async (address, name) => {
    if (!address) return
    const areaInfoArr = []
    let province, city, area // 名字
    let provinceIndex, cityIndex, areaIndex // 所处的索引
    let provinceId, cityId, areaId // areaId
    // 特殊的区域
    if (name.includes('苏滁现代产业园')) {
      province = '安徽省'
      city = '苏滁市'
      area = '苏滁现代产业园'
    } else if (name.includes('保定白沟新城')) {
      province = '河北省'
      city = '保定市'
      area = '保定白沟新城'
    } else if (name.includes('中沙群岛')) {
      province = '海南省'
      city = '三沙市'
      area = '中沙群岛的岛礁及其海域'
    } else {
      const provSuffix = ['省', '自治区', '黑龙江', '北京市', '天津市', '上海市', '重庆市']
      const citySuffix = ['市', '地区', '自治州', '盟']
      const areaSuffix = ['区', '市', '县', '群岛']
      let aIndex = 0
      const common = (arr) => {
        aIndex = 0
        for (let i = 0; i < arr.length; i++) {
          const index = address.indexOf(arr[i])
          if (index !== -1) {
            aIndex = index + arr[i].length
            break
          }
        }
      }
      common(provSuffix)
      if (aIndex === 0) return
      province = address.substring(0, aIndex)
      address = address.substring(aIndex)
      // 直辖市
      if (province === '北京市' || province === '天津市' || province === '上海市' || province === '重庆市') {
        common(areaSuffix)
        if (aIndex === 0) return
        area = address.substring(0, aIndex)
        city = area.includes('县') ? '县' : '市辖区'
      } else {
        common(citySuffix)
        if (aIndex === 0) return
        city = address.substring(0, aIndex)
        address = address.substring(aIndex)
        common(areaSuffix)
        if (aIndex === 0) return
        area = address.substring(0, aIndex)
      }
    }
    const getListByPid = (pid) => {
      const paramsData = {}
      if (pid === 0) {
        paramsData.level = 1
      } else {
        paramsData.pid = pid
      }
      return new Promise((resolve) => {
        const params = {
          url: '/p/area/listByPid',
          method: 'GET',
          data: paramsData
        }
        http.request(params).then((res) => {
          resolve(res.data)
        })
      })
    }
    // 省
    const res1 = await getListByPid(0)
    for (let i = 0; i < res1.length; i++) {
      if (province === res1[i].areaName) {
        provinceIndex = i
        provinceId = res1[i].areaId
        break
      }
    }
    // 市
    const res2 = await getListByPid(provinceId)
    for (let i = 0; i < res2.length; i++) {
      if (city === res2[i].areaName) {
        cityIndex = i
        cityId = res2[i].areaId
        break
      }
    }
    // 区
    const res3 = await getListByPid(cityId)
    for (let i = 0; i < res3.length; i++) {
      if (area === res3[i].areaName) {
        areaIndex = i
        areaId = res3[i].areaId
        break
      }
    }
    areaInfoArr.push({
      name: province,
      index: provinceIndex,
      areaId: provinceId
    })
    areaInfoArr.push({
      name: city,
      index: cityIndex,
      areaId: cityId
    })
    areaInfoArr.push({
      name: area,
      index: areaIndex,
      areaId
    })
    areaInfoArr.push(res1) // 省数组
    areaInfoArr.push(res2) // 市数组
    areaInfoArr.push(res3) // 区数组
    return areaInfoArr
  },
  parsePrice: function (val) {
    if (!val) {
      val = 0
    }
    return val.toFixed(2).split('.')
  },
  /**
   * 文件地址校验
   * @param fileUrl 获取到的文件路径
   */
  checkFileUrl: (fileUrl) => {
    // 防止 fileUrl 为null时 indexOf() 方法失效报错
    const url = fileUrl || ''
    const baseUrl = import.meta.env.VITE_APP_RESOURCES_URL
    // 判断 fileUrl 中是否已存在基础路径
    const check = url.indexOf('http') !== -1
    if (check || !fileUrl) {
      return url
    } else {
      return baseUrl + url
    }
  },
  /**
 * 回到首页
 */
  toHomePage: () => {
    const pages = getCurrentPages()
    const nowRoute = pages[pages.length - 1].route
    if (nowRoute === 'pages/index/index') {
      return
    }
    uni.switchTab({
      url: '/pages/index/index'
    })
  },

  // 登录返回上一页
  previousPage: () => {
    getApp().globalData.isRefreshPage = true // 用于重新加载页面数据
    const routeUrlAfterLogin = uni.getStorageSync('cbecB2cRouteUrlAfterLogin')
    const pages = getCurrentPages()
    const nowRoute = pages[pages.length - 1].route
    if (nowRoute === 'package-prod/pages/prod/prod') {
      // 如果当前页面是商品详情页或拼团详情页，则不跳转其它页面
      uni.removeStorageSync('cbecB2cRouteUrlAfterLogin')
      return
    }
    if (pages.length === 1) {
      if (routeUrlAfterLogin && routeUrlAfterLogin.indexOf('pages/index/index') === -1) {
        uni.reLaunch({
          url: routeUrlAfterLogin
        })
      } else {
        util.toHomePage()
      }
      uni.removeStorageSync('cbecB2cRouteUrlAfterLogin')
      return
    }
    const prevPage = pages[pages.length - 2]
    if (!prevPage) {
      util.toHomePage()
      return
    }
    // 判断上一页面是否为tabbar页面 (首页和分类页无需登录接口)
    const isTabbar = prevPage.route === 'pages/user/user' || prevPage.route === 'pages/basket/basket'
    if (isTabbar) {
      uni.switchTab({
        url: '/' + prevPage.route
      })
    } else {
      // 非tabbar页面
      let backDelata = 0
      pages.forEach((page, index) => {
        if (page.$page.fullPath === routeUrlAfterLogin) {
          backDelata = pages.length - index - 1
        }
      })
      if (backDelata) {
        uni.navigateBack({
          delta: backDelata
        })
      } else {
        util.toHomePage()
      }
    }
  },

  /**
   * 进行相隔时间判断
   *
   * true 删除显示时间
   * false 保留显示时间
   */
  timeBeApart: (uppTime, preTime) => {
    if (!uppTime) {
      return false
    }
    const dateDiff = preTime - uppTime // 时间差的毫秒数
    const dayDiff = Math.floor(dateDiff / (24 * 3600 * 1000))// 计算出相差天数
    const leave1 = dateDiff % (24 * 3600 * 1000) // 计算天数后剩余的毫秒数
    const hours = Math.floor(leave1 / (3600 * 1000))// 计算出小时数
    // 计算相差分钟数
    const leave2 = leave1 % (3600 * 1000) // 计算小时数后剩余的毫秒数
    const minutes = Math.floor(leave2 / (60 * 1000))// 计算相差分钟数

    return !(dayDiff >= 1 || hours >= 1 || minutes > 4)
  },
  // 页面跳转
  // eslint-disable-next-line max-lines-per-function
  toRedirectLink: async (type, link, shopId) => {
    const mobileLoginList = [
      '/package-user/pages/order-list/order-list',
      '/package-user/pages/prod-classify/prod-classify?sts=5'
    ]
    // 以上页面未登录会跳转到登录页
    if ((type == 4 || type == 6) && (mobileLoginList.indexOf(link) > -1 || (link.url && mobileLoginList.indexOf(link.url) > -1))) {
      const inx = mobileLoginList.indexOf(link) > -1 ? mobileLoginList.indexOf(link) : mobileLoginList.indexOf(link.url)
      // 登录后的回跳地址
      util.checkAuthInfo(async () => {
      }, mobileLoginList[inx])
      if (!uni.getStorageSync('cbecB2cToken')) return
    }
    let newUrl = ''
    switch (type) {
      case '1': // 跳转到商品
        uni.navigateTo({
          url: '/package-prod/pages/prod/prod?prodId=' + link
        })
        break
      case '2': // 跳转到分类
        if (Array.isArray(link)) {
          uni.navigateTo({
            url: `/package-prod/pages/sub-category/sub-category?parentId=${link[1].value}&categoryId=${link[2].value}&titleName=${link[0].label}`
          })
        }
        break
      case '4': // 跳转到页面
        if (mobileLoginList.indexOf(link) > -1) {
          uni.navigateTo({
            url: link
          })
        } else {
          if (link === '/pages/user/user' || link === '/pages/basket/basket' || link === '/') {
            uni.switchTab({
              url: link
            })
          } else {
            uni.navigateTo({
              url: link
            })
          }
        }
        break
      case '6': // 跳转到自定义链接
      {
        newUrl = link.url || link
        if (newUrl === '/') return
        const reg1 = /^\//
        const reg2 = /^(http|https)/
        if (reg1.test(newUrl)) { // 如果是以 / 开头
          let isStatus = false
          uni.navigateTo({
            url: newUrl,
            fail: function () { },
            complete: function (val) {
              if (val.path === '/') {
                isStatus = true
              }
            }
          })
          setTimeout(() => {
            if (isStatus) {
              uni.showToast({
                icon: 'error',
                title: '跳转链接异常'
              })
            }
          }, 100)
        } else if (reg2.test(newUrl)) { // 否则以http开头
          // #ifdef H5
          location.href = newUrl
          // #endif
          // #ifdef APP-PLUS
          plus.runtime.openURL(newUrl)
          // #endif
        } else {
          uni.showToast({
            icon: 'error',
            title: '跳转链接异常'
          })
        }
        break
      }
      default:
        break
    }
  },
  // 获取用户授权
  getUserAuth: (callBack) => {
    const system = uni.getSystemInfoSync()
    if (system.platform === 'android') {
      // 查询是否已经授权了定位权限
      plus.android.requestPermissions(
        ['android.permission.ACCESS_FINE_LOCATION'],
        // 成功回调
        function (resultObj) {
          let result = 0
          for (let i = 0; i < resultObj.granted.length; i++) {
            result = 1
          }
          for (let i = 0; i < resultObj.deniedPresent.length; i++) {
            result = 0
          }
          for (let i = 0; i < resultObj.deniedAlways.length; i++) {
            // 永久拒绝权限，需要到设置页面打开
            result = -1
          }
          if (result === 1) {
            // 授权成功执行回调
            callBack()
          } else if (result === 0) {
            // 本次拒绝了权限，提示用户
            uni.showToast({
              title: $t('authorityErrorTip1'),
              icon: 'none'
            })
          } else if (result === -1) {
            uni.showModal({
              title: $t('tips'),
              content: $t('authorityErrorTip2'),
              showCancel: true, // 是否显示取消按钮
              cancelText: $t('cancel'),
              confirmText: $t('confirm'),
              success: (res) => {
                if (res.confirm) {
                  // 点击确定
                  // 若所需权限被拒绝,则打开APP设置界面,可以在APP设置界面打开相应权限
                  const Intent = plus.android.importClass('android.content.Intent')
                  const Settings = plus.android.importClass('android.provider.Settings')
                  const Uri = plus.android.importClass('android.net.Uri')
                  const mainActivity = plus.android.runtimeMainActivity()
                  const intent = new Intent()
                  intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                  const uri = Uri.fromParts('package', mainActivity.getPackageName(), null)
                  intent.setData(uri)
                  mainActivity.startActivity(intent)
                }
              }
            })
          }
        },
        // 失败回调
        function (error) {
          uni.showToast({
            title: $t('authorityError') + error.code + ' = ' + error.message,
            icon: 'none'
          })
        }
      )
    } else {
      callBack()
    }
  },

  /**
   * 请求系统支付开关
   * @param {Boolean} forcedRefresh 是否强制刷新数据
   */
  getSysPaySwitch: async (forcedRefresh) => {
    // 获取缓存中的配置
    const paySwitchInfo = uni.getStorageSync('cbecB2cPaySwitchInfo') || {}
    if (!paySwitchInfo.expiredTime || new Date().getTime() > paySwitchInfo.expiredTime || forcedRefresh) {
      const params = {
        url: '/sys/config/info/getSysPaySwitch',
        method: 'GET'
      }
      const { data } = await http.request(params)
      const paySwitchInfo = {
        switchInfo: data,
        expiredTime: 5 * 60 * 60 * 1000 + new Date().getTime()
      }
      uni.setStorageSync('cbecB2cPaySwitchInfo', paySwitchInfo)
    }
  },
  // 生成海报
  getPoster: (pic, price, prodName, code) => {
    return {
      css: {
        width: '600rpx',
        height: '850rpx'

      },
      views: [{
        type: 'view',
        css: {
          width: '600rpx',
          height: '850rpx',
          position: 'fixed',
          backgroundColor: '#fff',
          borderRadius: '24rpx',
          padding: '30rpx',
          boxSizing: 'border-box'
        },
        views: [
          {
            type: 'image',
            src: '/static/images/icon/def.png',
            css: {
              width: '540rpx',
              height: '540rpx',
              display: 'block',
              margin: '0 auto'
            }
          },
          {
            type: 'image',
            src: util.checkFileUrl(pic),
            css: {
              width: '540rpx',
              height: '540rpx',
              display: 'block',
              position: 'absolute',
              top: '-30rpx',
              left: '-30rpx',
              background: '#fff',
              objectFit: 'cover'
            }
          },
          {
            type: 'view',
            css: {
              height: '200rpx',
              width: '540rpx',
              marginTop: '50rpx'
            },
            views: [
              {
                type: 'view',
                css: {
                  width: '360rpx',
                  height: '190rpx',
                  borderRight: '1px solid #E9E9EA'
                },
                views: [{
                  type: 'view',
                  css: {},
                  views: [
                    {
                      type: 'text',
                      text: '$',
                      css: {
                        fontSize: '28rpx',
                        color: '#f81a1a',
                        verticalAlign: 'bottom'
                      }
                    },
                    {
                      type: 'text',
                      text: price.toFixed(2).split('.')[0] + '.',
                      css: {
                        fontSize: '40rpx',
                        verticalAlign: 'bottom',
                        lineHeight: '40rpx',
                        color: '#f81a1a'

                      }
                    },
                    {
                      type: 'text',
                      text: price.toFixed(2).split('.')[1],
                      css: {
                        fontSize: '28rpx',
                        verticalAlign: 'bottom',
                        color: '#f81a1a'
                      }
                    }
                  ]
                }, {
                  type: 'text',
                  text: prodName,
                  css: {
                    marginTop: '10rpx',
                    fontSize: '28rpx',
                    color: '#333333',
                    display: '-webkit-box',
                    boxOrient: 'vertical',
                    lineClamp: 4,
                    overflow: 'hidden',
                    textOverflow: 'ellipsis'
                  }
                }]
              },
              {
                type: 'view',
                css: {
                  width: '150rpx',
                  height: '150rpx',
                  position: 'absolute',
                  right: '0rpx',
                  bottom: $t('lang') !== 'zh_CN' ? '50rpx' : '30rpx'
                },
                views: [{
                  type: 'image',
                  src: code,
                  css: {
                    width: '150rpx',
                    height: '150rpx'
                  }
                }]
              },
              {
                type: 'text',
                text: $t('pressToView'),
                css: {
                  fontSize: '20rpx',
                  lineHeight: '30rpx',
                  textAlign: 'center',
                  width: $t('lang') !== 'zh_CN' ? '160rpx' : '150rpx',
                  height: '30rpx',
                  position: 'absolute',
                  right: '0rpx',
                  bottom: $t('lang') !== 'zh_CN' ? '20rpx' : '0rpx'
                }
              }
            ]
          }]
      }]
    }
  },

  /**
   * 小程序处理base64小程序码
   * @param {*} base64data
   */
  base64src: (base64data) => {
    const fsm = uni.getFileSystemManager()
    const FILE_BASE_NAME = 'tmp_base64src' + Math.random() * 1000000000000
    return new Promise((resolve, reject) => {
      const format = 'jpg'
      const bodyData = base64data
      if (!format) {
        reject(new Error('ERROR_BASE64SRC_PARSE'))
      }
      const filePath = `${wx.env.USER_DATA_PATH}/${FILE_BASE_NAME}.${format}`
      const buffer = uni.base64ToArrayBuffer(bodyData)
      fsm.writeFile({
        filePath,
        data: buffer,
        encoding: 'binary',
        success () {
          resolve(filePath)
        },
        fail () {
          reject(new Error('ERROR_BASE64SRC_WRITE'))
        }
      })
    })
  },
  // 前往直播间
  gotoLiveRoom () {
    uni.showToast({
      title: $t('closed'),
      icon: 'none'
    })
  },
  /**
   * 微信小程序跳转通联小程序
   */
  toAllinPay (params) {
    wx.navigateToMiniProgram({
      appId: 'wxc46c6d2eed27ca0a', // 通联小程序 appId
      path: 'pages/merchantAddress/merchantAddress', // 通联小程序页面路径
      extraData: { targetUrl: params.targetUrl }, // 商户拼接的请求报文
      envVersion: 'release', // develop 开发版 trial 体验版 release 正式版
      // 打开成功
      success: params.success,
      // 打开失败
      fail: params.fail,
      // 打开完成(成功或失败都会执行)
      complete: params.complete
    })
  },
  /**
   * 套餐商品的支持的物流方式
   */
  initComboDelivery: (comboProdItems) => {
    const length = comboProdItems.length
    const deliveryModeVO = {}
    // mold   虚拟订单不判断
    deliveryModeVO.hasCityDelivery = comboProdItems.filter(el => el.deliveryModeVO.hasCityDelivery || el.mold === 1).length === length
    deliveryModeVO.hasShopDelivery = comboProdItems.filter(el => el.deliveryModeVO.hasShopDelivery || el.mold === 1).length === length
    deliveryModeVO.hasUserPickUp = comboProdItems.filter(el => el.deliveryModeVO.hasUserPickUp || el.mold === 1).length === length

    return deliveryModeVO
  },
  chooseImage ({ compress, count, sourceType, sizeType, isImFile = false, isCallUpload = true }) {
    return new Promise((resolve) => {
      const resData = []
      const obj = { src: '', quality: 0.2 }
      let isH5 = false
      // #ifdef H5
      isH5 = true
      // #endif
      // appType： 1小程序
      const appType = uni.getStorageSync('cbecB2cAppType')
      uni[appType === 1 ? 'chooseMedia' : 'chooseImage']({
        count: isH5 ? 1 : (count || 1),
        sizeType: sizeType || ['compressed'],
        sourceType: sourceType || ['album'],
        success: (res) => {
          uni.showLoading({
            title: $t('uploadingTip')
          })
          let tempFilePaths = res.tempFilePaths
          // #ifdef MP-WEIXIN
          tempFilePaths = res.tempFiles
          // #endif
          tempFilePaths.forEach((item, index) => {
            // 限制图片上传格式
            let _type = res.tempFiles[index].type
            // #ifdef MP-WEIXIN
            _type = item.tempFilePath.split('.')[1]
            // #endif
            // #ifdef APP-PLUS
            obj.src = item
            _type = item.substring(item.lastIndexOf('.') + 1)
            // #endif
            const contentType = _type.indexOf('image/') > -1 ? _type : 'image/' + _type
            if (_type.indexOf('png') === -1 && _type.indexOf('jpg') === -1 && _type.indexOf('jpeg') === -1) {
              uni.hideLoading()
              uni.showToast({
                title: $t('uploadPictureFile'),
                icon: 'none'
              })
              return
            }

            const getPreSignUrlParams = { fileName: undefined, isImFile }
            const uploadImgParams = { isCallUpload, index, res, resData, contentType }
            // #ifdef MP-WEIXIN
            obj.src = item.tempFilePath
            getPreSignUrlParams.fileName = res.tempFiles[index].tempFilePath
            // #endif
            // #ifdef H5
            obj.src = item
            getPreSignUrlParams.fileName = res.tempFiles[index].name
            // #endif

            compress(obj, res.tempFiles[index]).then(async filePath => {
              // #ifdef APP-PLUS
              const appFileBuffer = await util.plusIo(filePath)
              getPreSignUrlParams.fileName = res.tempFiles[index].path
              // #endif

              util.getPreSignUrl(getPreSignUrlParams).then(({ data }) => {
                // #ifdef H5
                uni.request({
                  url: filePath,
                  method: 'GET',
                  responseType: 'arraybuffer',
                  success: (requestRes) => {
                    util.uploadImg(Object.assign(uploadImgParams, { data, requestRes })).then(() => {
                      uni.hideLoading()
                      resolve(resData)
                    })
                  }
                })
                // #endif

                // #ifdef MP-WEIXIN
                uni.getFileSystemManager().readFile({
                  filePath,
                  success: (requestRes) => {
                    util.uploadImg(Object.assign(uploadImgParams, { data, requestRes })).then(() => {
                      uni.hideLoading()
                      resolve(resData)
                    })
                  }
                })
                // #endif

                // #ifdef APP-PLUS
                const requestRes = { data: appFileBuffer }
                util.uploadImg(Object.assign(uploadImgParams, { data, requestRes })).then(() => {
                  uni.hideLoading()
                  resolve(resData)
                })
                // #endif
              })
            })
          })
        },
        fail: failMsg => {
          uni.hideLoading()
          // eslint-disable-next-line no-console
          console.log('选择图片失败', failMsg)
        }
      })
    })
  },
  uploadImg: (params) => {
    const { data, requestRes, res, resData, isCallUpload, index, contentType } = params
    return new Promise((resolve) => {
      uni.request({
        url: data.preSignUrl,
        method: 'PUT',
        header: {
          'Content-Type': contentType
        },
        data: requestRes.data,
        success: function () {
          resData.push({ filePath: data.filePath, fileId: data.fileId, fileSize: res.tempFiles[index].size })
          if (res.tempFiles.length === resData.length) {
            if (isCallUpload) {
              util.uploadSuccess(resData).then(() => {
                resolve(resData)
              })
            } else {
              resolve(resData)
            }
          }
        }
      })
    })
  },
  plusIo: (filePath) => {
    return new Promise((resolve) => {
      plus.io.resolveLocalFileSystemURL(filePath, async function (entry) {
        entry.file(function (file) { // 读取文件
          const reader = new plus.io.FileReader()
          reader.readAsDataURL(file) // 以URL格式读取文件
          reader.onload = function () {
            const base64 = this.result.split(',')[1] // 获取base64字符串
            const buffer = uni.base64ToArrayBuffer(base64) // 转换为arrayBuffer格式
            resolve(buffer)
          }
        })
      })
    })
  },
  uploadSuccess (resData) {
    return http.request({
      url: '/p/file/uploadSuccess',
      method: 'PUT',
      data: resData
    })
  },
  // 保存上传的图片到平台资源管理
  saveAttachFileToPlat (data) {
    if (!data || !data.length) return true
    return new Promise(resolve => {
      http.request({
        url: '/p/file/uploadSuccessToPlatform',
        method: 'PUT',
        data
      }).then(() => {
        resolve(true)
      }).catch(() => {
        resolve(false)
      })
    })
  },
  getPreSignUrl (data) {
    return http.request({
      url: '/p/file/getPreSignUrl',
      method: 'GET',
      data
    })
  },

  /**
   * 获取当前定位信息
   * @param {Function} fn 定位成功回调函数
   * @param {Function} fnFail 定位失败回调函数
   */
  getLocation (fn, fnFail = null) {
    uni.showLoading()
    uni.getLocation({
      type: 'wgs84', // 默认为 wgs84 返回 gps 坐标，gcj02 返回国测局坐标，可用于 uni.openLocation 的坐标，app平台高德SDK仅支持返回gcj02
      altitude: true,
      isHighAccuracy: true,
      highAccuracyExpireTime: 3500,
      success: (res) => {
        if (res) {
          fn(wgs84togcj02(res))
        }
      },
      fail: () => {
        if (fnFail) {
          fnFail()
        } else {
          uni.showToast({
            title: $t('locateFailure'),
            icon: 'none',
            duration: 1500
          })
        }
      },
      complete: () => {
        uni.hideLoading()
      }
    })
  },

  /**
   * 计算并返回对应语言环境的折扣比例
   * @param num 折扣比例数值
   */
  countDiscountRatio (num) {
    const lang = uni.getStorageSync('cbecB2cLang') || 'en'
    if (lang === 'zh_CN' || lang === 'zh_HK') {
      return num
    } else {
      const val = (10 - num) * 10
      return val + '%'
    }
  },
  /**
   * 未开源提示
   */
  notOpenTips () {
    uni.showToast({
      title: '该功能未开源',
      icon: 'none'
    })
  }

}

export default util
